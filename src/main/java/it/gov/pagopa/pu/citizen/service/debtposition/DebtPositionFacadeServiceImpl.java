package it.gov.pagopa.pu.citizen.service.debtposition;

import it.gov.pagopa.pu.citizen.connector.debtpositions.DebtPositionService;
import it.gov.pagopa.pu.citizen.connector.debtpositions.ReceiptService;
import it.gov.pagopa.pu.citizen.connector.pagopapayments.PrintPaymentNoticeService;
import it.gov.pagopa.pu.citizen.dto.DebtPositionExtendedDTO;
import it.gov.pagopa.pu.citizen.dto.FileResourceDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionResponseDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtorUnpaidDebtPositionOverviewDTO;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorDebtPositionDTO;
import it.gov.pagopa.pu.citizen.exception.ConflictException;
import it.gov.pagopa.pu.citizen.exception.ResourceNotFoundException;
import it.gov.pagopa.pu.citizen.mapper.*;
import it.gov.pagopa.pu.citizen.service.ZipFileService;
import it.gov.pagopa.pu.citizen.service.debtposition.cie.CieDebtPositionFacadeService;
import it.gov.pagopa.pu.citizen.service.installment.InstallmentFacadeService;
import it.gov.pagopa.pu.citizen.service.organization.BrokerOrganizationsRetrieverService;
import it.gov.pagopa.pu.citizen.service.organization.OrganizationRetrieverService;
import it.gov.pagopa.pu.citizen.utils.InstallmentUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.*;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static it.gov.pagopa.pu.citizen.utils.DebtPositionConstants.ORDINARY_DEBTPOSITION_ORIGINS;
import static it.gov.pagopa.pu.citizen.utils.DebtPositionConstants.PAYABLE_STATUSES;

@Service
public class DebtPositionFacadeServiceImpl implements DebtPositionFacadeService {

  private final DebtPositionService debtPositionService;
  private final DebtPositionDTOMapper debtPositionDTOMapper;
  private final Integer expirationDays;
  private final DebtPositionResponseDTOMapper debtPositionResponseDTOMapper;
  private final PrintPaymentNoticeService printPaymentNoticeService;
  private final ZipFileService zipFileService;
  private final OrganizationRetrieverService organizationRetrieverService;
  private final BrokerOrganizationsRetrieverService brokerOrganizationsRetrieverService;
  private final PagedDebtorDebtPositionMapper pagedDebtorDebtPositionMapper;
  private final DebtorUnpaidDebtPositionOverviewMapper debtorUnpaidDebtPositionOverviewMapper;
  private final ReceiptService receiptService;
  private final CieDebtPositionFacadeService cieDebtPositionFacadeService;
  private final InstallmentFacadeService installmentFacadeService;
  private final DebtPositionExtendedDTOMapper debtPositionExtendedDTOMapper;

  public DebtPositionFacadeServiceImpl(DebtPositionService debtPositionService,
                                       DebtPositionDTOMapper debtPositionDTOMapper,
                                       @Value("${spontaneous.expiration-days}")Integer expirationDays,
                                       DebtPositionResponseDTOMapper debtPositionResponseDTOMapper,
                                       PrintPaymentNoticeService printPaymentNoticeService,
                                       ZipFileService zipFileService,
                                       OrganizationRetrieverService organizationRetrieverService,
                                       BrokerOrganizationsRetrieverService brokerOrganizationsRetrieverService,
                                       PagedDebtorDebtPositionMapper pagedDebtorDebtPositionMapper,
                                       DebtorUnpaidDebtPositionOverviewMapper debtorUnpaidDebtPositionOverviewMapper,
                                       ReceiptService receiptService,
                                       CieDebtPositionFacadeService cieDebtPositionFacadeService,
                                       InstallmentFacadeService installmentFacadeService, DebtPositionExtendedDTOMapper debtPositionExtendedDTOMapper
  ) {
    this.debtPositionDTOMapper = debtPositionDTOMapper;
    this.debtPositionService = debtPositionService;
    this.expirationDays = expirationDays;
    this.debtPositionResponseDTOMapper = debtPositionResponseDTOMapper;
    this.printPaymentNoticeService = printPaymentNoticeService;
    this.zipFileService = zipFileService;
    this.organizationRetrieverService = organizationRetrieverService;
    this.brokerOrganizationsRetrieverService = brokerOrganizationsRetrieverService;
    this.pagedDebtorDebtPositionMapper = pagedDebtorDebtPositionMapper;
    this.debtorUnpaidDebtPositionOverviewMapper = debtorUnpaidDebtPositionOverviewMapper;
    this.receiptService = receiptService;
    this.cieDebtPositionFacadeService = cieDebtPositionFacadeService;
    this.installmentFacadeService = installmentFacadeService;
    this.debtPositionExtendedDTOMapper = debtPositionExtendedDTOMapper;
  }

  @Override
  public DebtPositionResponseDTO createSpontaneousDebtPosition(Long brokerId, DebtPositionRequestDTO debtPositionRequestDTO, String accessToken) {
    boolean cieBroker = organizationRetrieverService.isCieBroker(brokerId, accessToken);
    if(cieBroker) {
      return cieDebtPositionFacadeService.createSpontaneousDebtPosition(debtPositionRequestDTO,accessToken);
    }
    Organization organization = organizationRetrieverService.getValidOrganization(debtPositionRequestDTO.getOrganizationId(),brokerId,accessToken);
    DebtPositionDTO debtPosition = debtPositionService.createDebtPosition(debtPositionDTOMapper.mapSpontaneousDebtPositionDTO(debtPositionRequestDTO, expirationDays), false, accessToken);

    List<InstallmentDTO> installments = debtPosition.getPaymentOptions().stream().flatMap(po -> po.getInstallments().stream()).toList();
    PostalIbanVerifyResponse postalIbanVerifyResponse = installmentFacadeService.extractPostalIbanVerifyResponse(installments, InstallmentDTO::getInstallmentId, accessToken);
    return debtPositionResponseDTOMapper.map(debtPosition, organization, false, postalIbanVerifyResponse);
  }

  @Override
  public Resource getDebtPositionNoticesZip(Long brokerId, String fiscalCode, Long debtPositionId, String accessToken) {
    DebtPositionDTO debtPosition = getDebtPositionDTO(brokerId, fiscalCode, debtPositionId, accessToken);
    if (debtPosition == null){
      return null;
    }

    List<FileResourceDTO> paymentNoticeFileResources = debtPosition
      .getPaymentOptions()
      .stream()
      .flatMap(po ->
        po.getInstallments()
          .stream()
          .filter(
            i ->
              i.getStatus()!=null && PAYABLE_STATUSES.contains(i.getStatus()) && fiscalCode.equals(i.getDebtor().getFiscalCode())
          )
      )
      .map(i ->
        printPaymentNoticeService.generateNotice(i.getNav(), debtPosition, accessToken))
      .toList();

    if (paymentNoticeFileResources.isEmpty()) {
      return null;
    }

    return zipFileService.zipper(paymentNoticeFileResources);
  }

  @Override
  public DebtPositionExtendedDTO getDebtPositionDetail(Long brokerId, String fiscalCode, Long debtPositionId, String accessToken) {
    DebtPositionDTO debtPosition = getDebtPositionDTO(brokerId, fiscalCode, debtPositionId, accessToken);
    if (debtPosition == null){
      return null;
    }

    List<InstallmentDTO> installments = debtPosition.getPaymentOptions().stream().flatMap(po -> po.getInstallments().stream()).toList();
    PostalIbanVerifyResponse postalIbanVerifyResponse = installmentFacadeService.extractPostalIbanVerifyResponse(installments, InstallmentDTO::getInstallmentId, accessToken);

    return debtPositionExtendedDTOMapper.map(debtPosition, postalIbanVerifyResponse);
  }

  private DebtPositionDTO getDebtPositionDTO(Long brokerId, String fiscalCode, Long debtPositionId, String accessToken) {
    DebtPositionDTO debtPosition = debtPositionService.getDebtPosition(debtPositionId, accessToken);
    if (debtPosition == null){
      return null;
    }
    organizationRetrieverService.validateOrganization(debtPosition.getOrganizationId(), brokerId, accessToken);
    validateDebtPositionDebtor(fiscalCode, debtPosition);
    resolveInstallmentStatus(debtPosition);
    return debtPosition;
  }

  private void resolveInstallmentStatus(DebtPositionDTO debtPosition) {
    if(debtPosition==null){
      return;
    }
    debtPosition.getPaymentOptions().forEach(
      po->po.getInstallments().forEach(
        i-> i.setStatus(InstallmentUtils.resolveInstallmentStatus(i.getStatus()))));
  }

  private static void validateDebtPositionDebtor(String fiscalCode, DebtPositionDTO debtPosition) {
    Objects.requireNonNull(debtPosition).getPaymentOptions().stream().flatMap(po->po.getInstallments().stream())
        .filter(i-> fiscalCode.equals(i.getDebtor().getFiscalCode())).findAny()
        .orElseThrow(() -> new AuthorizationDeniedException("[USER_UNAUTHORIZED] User cannot access DebtPosition having id "+ debtPosition.getDebtPositionId()));
  }

  @Override
  public FileResourceDTO getPaymentNotice(String fiscalCode, Long brokerId, Long organizationId, String nav, String accessToken) {
    boolean cieBroker = organizationRetrieverService.isCieBroker(brokerId, accessToken);
    if(cieBroker) {
      return cieDebtPositionFacadeService.generateNoticeCie(nav, fiscalCode, accessToken);
    }
    organizationRetrieverService.validateOrganization(organizationId,brokerId,accessToken);
    DebtPositionDTO debtPosition = debtPositionService.getDebtPositionsByOrganizationIdAndNav(organizationId, nav, ORDINARY_DEBTPOSITION_ORIGINS, accessToken)
      .stream().findFirst().orElse(null);
    if (debtPosition == null) {
      return null;
    }
    validateDebtPosition(organizationId, fiscalCode, debtPosition);
    Optional<FileResourceDTO> paymentNoticeFileResource = debtPosition
      .getPaymentOptions()
      .stream()
      .flatMap(po ->
        po.getInstallments()
          .stream()
          .filter(i -> nav.equals(i.getNav()))
      )
      .map(i ->
        printPaymentNoticeService.generateNotice(i.getNav(), debtPosition, accessToken))
      .findAny();
    return paymentNoticeFileResource.orElse(null);
  }

  private static void validateDebtPosition(Long organizationId, String fiscalCode, DebtPositionDTO debtPosition) {
    if(!debtPosition.getOrganizationId().equals(organizationId)){
      throw new ConflictException("DEBT_POSITION_CONFLICT", "DebtPosition's organizationId does not match the given organizationId "+ organizationId);
    }
    if(!ORDINARY_DEBTPOSITION_ORIGINS.contains(debtPosition.getDebtPositionOrigin())){
      throw new ValidationException("[INVALID_DEBT_POSITION_ORIGIN] Invalid debtPositionOrigin "+debtPosition.getDebtPositionOrigin());
    }
    validateDebtPositionDebtor(fiscalCode, debtPosition);
  }

  @Override
  public PagedDebtorDebtPositionDTO getPagedUnpaidDebtPositions(String xFiscalCode,
                                                                Long brokerId,
                                                                String orgName,
                                                                String orgFiscalCode,
                                                                Pageable pageable,
                                                                String accessToken) {

    Map<Long, Organization> organizations = retrieveOrganizations(brokerId, orgName, orgFiscalCode, accessToken);
    List<Long> organizationsIds = new ArrayList<>(organizations.keySet());

    PagedDebtorUnpaidDebtPositionDTO pagedDebtorUnpaidDebtPosition = debtPositionService.getPagedDebtorUnpaidDebtPosition(xFiscalCode, organizationsIds, pageable, accessToken);

    return pagedDebtorDebtPositionMapper.map(
      organizations,
      pagedDebtorUnpaidDebtPosition
    );
  }

  private Map<Long,Organization> retrieveOrganizations(Long brokerId, String orgName, String orgFiscalCode, String accessToken){
    List<Organization> organizations = brokerOrganizationsRetrieverService.getAllOrganizationsByBrokerIdAndOrgNameAndOrgFiscalCode(brokerId, orgName, orgFiscalCode, accessToken);
    if (organizations.isEmpty()){
      throw new ResourceNotFoundException("ORGANIZATION_NOT_FOUND", "Organizations not found with brokerId %s orgName %s and orgFiscalCode %s".formatted(brokerId, orgName, orgFiscalCode));
    }

    return organizations.stream()
      .collect(Collectors.toMap(Organization::getOrganizationId, org -> org));
  }

  @Override
  public DebtorUnpaidDebtPositionOverviewDTO getDebtorUnpaidDebtPositionOverview(Long brokerId, Long debtPositionId, String debtorFiscalCode, Long organizationId, String accessToken) {
    DebtorDebtPositionDTO debtorDebtPosition = debtPositionService.getDebtorDebtPositionOverview(debtPositionId, debtorFiscalCode, organizationId, accessToken);
    if (debtorDebtPosition == null){
      return null;
    }

    List<BaseInstallment> installments = Objects.requireNonNull(debtorDebtPosition.getPaymentOptions()).stream()
      .flatMap(po -> Objects.requireNonNull(po.getInstallments()).stream())
      .toList();

    Map<Long, OffsetDateTime> installmentIdAndPaymentDateTimeMap = extractPaymentDateTimeFromReceiptOnMap(accessToken, debtorDebtPosition);
    PostalIbanVerifyResponse postalIbanVerifyResponse = installmentFacadeService.extractPostalIbanVerifyResponse(installments, BaseInstallment::getInstallmentId, accessToken);

    return debtorUnpaidDebtPositionOverviewMapper.map(organizationRetrieverService.getValidOrganization(organizationId, brokerId, accessToken), debtorDebtPosition, installmentIdAndPaymentDateTimeMap, postalIbanVerifyResponse);
  }

  private Map<Long, OffsetDateTime> extractPaymentDateTimeFromReceiptOnMap(String accessToken, DebtorDebtPositionDTO debtorDebtPosition) {

    List<BaseInstallment> installmentsWithReceipt = Objects.requireNonNull(debtorDebtPosition.getPaymentOptions()).stream()
      .flatMap(po -> Objects.requireNonNull(po.getInstallments()).stream())
      .filter(i -> i.getReceiptId() != null)
      .toList();

    Map<Long, Long> receiptIdAndInstallmentIdMap = installmentsWithReceipt.stream().collect(Collectors.toMap(BaseInstallment::getReceiptId, BaseInstallment::getInstallmentId));

    List<ReceiptNoPII> receiptNoPiiList = receiptService.getReceiptNoPiiList(receiptIdAndInstallmentIdMap.keySet(), accessToken);

    return receiptNoPiiList.stream()
        .collect(Collectors.toMap(
          r -> receiptIdAndInstallmentIdMap.get(r.getReceiptId()),
          ReceiptNoPII::getPaymentDateTime
        ));
  }


}
