package it.gov.pagopa.pu.citizen.service.debtposition;

import it.gov.pagopa.pu.citizen.connector.debtpositions.DebtPositionService;
import it.gov.pagopa.pu.citizen.connector.pagopapayments.PrintPaymentNoticeService;
import it.gov.pagopa.pu.citizen.dto.FileResourceDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionResponseDTO;
import it.gov.pagopa.pu.citizen.exception.ConflictException;
import it.gov.pagopa.pu.citizen.exception.InvalidParamException;
import it.gov.pagopa.pu.citizen.mapper.DebtPositionDTOMapper;
import it.gov.pagopa.pu.citizen.mapper.DebtPositionResponseDTOMapper;
import it.gov.pagopa.pu.citizen.service.ZipFileService;
import it.gov.pagopa.pu.citizen.service.organization.OrganizationRetrieverService;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionOrigin;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentStatus;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import jakarta.validation.ValidationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class DebtPositionFacadeServiceImpl implements DebtPositionFacadeService {

  private final DebtPositionService debtPositionService;
  private final DebtPositionDTOMapper debtPositionDTOMapper;
  private final Integer expirationDays;
  private final DebtPositionResponseDTOMapper debtPositionResponseDTOMapper;
  private final PrintPaymentNoticeService printPaymentNoticeService;
  private final ZipFileService zipFileService;
  private final OrganizationRetrieverService organizationRetrieverService;

  private static final List<DebtPositionOrigin> debtPositionOrigins = List.of(
    DebtPositionOrigin.ORDINARY,
    DebtPositionOrigin.ORDINARY_SIL,
    DebtPositionOrigin.SPONTANEOUS,
    DebtPositionOrigin.SPONTANEOUS_SIL
  );

  public DebtPositionFacadeServiceImpl(DebtPositionService debtPositionService,
                                       DebtPositionDTOMapper debtPositionDTOMapper,
                                       @Value("${spontaneous.expiration-days}")Integer expirationDays,
                                       DebtPositionResponseDTOMapper debtPositionResponseDTOMapper,
                                       PrintPaymentNoticeService printPaymentNoticeService, ZipFileService zipFileService, OrganizationRetrieverService organizationRetrieverService
  ) {
    this.debtPositionDTOMapper = debtPositionDTOMapper;
    this.debtPositionService = debtPositionService;
    this.expirationDays = expirationDays;
    this.debtPositionResponseDTOMapper = debtPositionResponseDTOMapper;
    this.printPaymentNoticeService = printPaymentNoticeService;
    this.zipFileService = zipFileService;
    this.organizationRetrieverService = organizationRetrieverService;
  }

  @Override
  public DebtPositionResponseDTO createSpontaneousDebtPosition(Long brokerId, DebtPositionRequestDTO debtPositionRequestDTO, String accessToken) {
    DebtPositionDTO debtPosition = debtPositionService.createDebtPosition(debtPositionDTOMapper.mapSpontaneousDebtPositionDTO(debtPositionRequestDTO, expirationDays), false, accessToken);
    Organization organization = organizationRetrieverService.getValidOrganization(debtPositionRequestDTO.getOrganizationId(),brokerId,accessToken);
    return debtPositionResponseDTOMapper.map(debtPosition, organization);
  }

  @Override
  public Resource getDebtPositionNoticesZip(Long brokerId, String fiscalCode, Long debtPositionId, String accessToken) {
    DebtPositionDTO debtPosition = getDebtPositionDetail(brokerId, fiscalCode, debtPositionId, accessToken);
    if (debtPosition == null){
      return null;
    }

    List<FileResourceDTO> pdfResources = getFileResources(debtPosition, accessToken);

    if (pdfResources.isEmpty()) {
      return null;
    }

    return zipFileService.zipper(pdfResources);
  }

  private List<FileResourceDTO> getFileResources(DebtPositionDTO debtPosition, String accessToken){
    return getFileResources(debtPosition, null, null, null, accessToken);
  }

  private List<FileResourceDTO> getFileResources(DebtPositionDTO debtPosition, String iuv, String iud, Long installmentId, String accessToken) {
    return debtPosition
      .getPaymentOptions()
      .stream()
      .flatMap(po ->
        po.getInstallments()
          .stream()
          .filter(
            i ->
              (InstallmentStatus.UNPAID.equals(i.getStatus()) ||
                InstallmentStatus.UNPAYABLE.equals(i.getStatus()))
                && (StringUtils.isBlank(iuv) || iuv.equals(i.getIuv()))
                && (StringUtils.isBlank(iud) || iud.equals(i.getIud()))
                && (installmentId==null || installmentId.equals(i.getInstallmentId())))
      )
      .map(i ->
        printPaymentNoticeService.generateNotice(i.getIuv(), debtPosition, accessToken))
      .toList();
  }

  @Override
  public DebtPositionDTO getDebtPositionDetail(Long brokerId, String fiscalCode, Long debtPositionId, String accessToken) {
    DebtPositionDTO debtPosition = debtPositionService.getDebtPosition(debtPositionId, accessToken);
    if (debtPosition == null){
      return null;
    }
    organizationRetrieverService.validateOrganization(debtPosition.getOrganizationId(), brokerId, accessToken);
    validateDebtPositionDebtor(fiscalCode, debtPosition);
    return debtPosition;
  }

  private static void validateDebtPositionDebtor(String fiscalCode, DebtPositionDTO debtPosition) {
    Objects.requireNonNull(debtPosition).getPaymentOptions().stream().flatMap(po->po.getInstallments().stream())
        .filter(i-> fiscalCode.equals(i.getDebtor().getFiscalCode())).findAny()
        .orElseThrow(() -> new AuthorizationDeniedException("User cannot access DebtPosition having id "+ debtPosition.getDebtPositionId()));
  }

  @Override
  public FileResourceDTO getUnpaidPaymentNotice(String fiscalCode, Long brokerId, Long organizationId, Long installmentId, String iuv, String iud, String accessToken) {
    organizationRetrieverService.validateOrganization(organizationId,brokerId,accessToken);
    DebtPositionDTO debtPosition = retrieveDebtPosition(organizationId,iuv,iud,installmentId, accessToken);
    if (debtPosition == null) {
      return null;
    }
    validateDebtPosition(organizationId, fiscalCode, debtPosition);
    List<FileResourceDTO> pdfResources = getFileResources(debtPosition, iuv, iud, installmentId, accessToken);
    if (pdfResources.isEmpty()) {
      return null;
    }
    return pdfResources.getFirst();
  }

  private static void validateDebtPosition(Long organizationId, String fiscalCode, DebtPositionDTO debtPosition) {
    if(!debtPosition.getOrganizationId().equals(organizationId)){
      throw new ConflictException("DebtPosition's organizationId does not match the given organizationId "+ organizationId);
    }
    if(!debtPositionOrigins.contains(debtPosition.getDebtPositionOrigin())){
      throw new ValidationException("Invalid debtPositionOrigin "+debtPosition.getDebtPositionOrigin());
    }
    validateDebtPositionDebtor(fiscalCode, debtPosition);
  }

  private DebtPositionDTO retrieveDebtPosition(Long organizationId, String iuv, String iud, Long installmentId, String accessToken) {
    int filterCount = (StringUtils.isNotBlank(iuv)?1:0) + (StringUtils.isNotBlank(iud)?1:0) + (installmentId!=null?1:0);
    if(filterCount!=1){
      throw new InvalidParamException("Exactly one of the following parameters must be provided: iuv, iud, or installmentId");
    }

    if(StringUtils.isNotBlank(iuv)){
      return debtPositionService.getDebtPositionsByOrganizationIdAndIuv(organizationId, iuv, debtPositionOrigins, accessToken).stream().findFirst().orElse(null);
    }else if(StringUtils.isNotBlank(iud)){
      return debtPositionService.getDebtPositionsByOrganizationIdAndIud(organizationId, iud, debtPositionOrigins, accessToken).stream().findFirst().orElse(null);
    }else{
      return debtPositionService.getDebtPositionByInstallmentId(installmentId, accessToken);
    }
  }
}
