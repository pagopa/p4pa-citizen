package it.gov.pagopa.pu.citizen.service.debtposition;

import it.gov.pagopa.pu.citizen.connector.debtpositions.DebtPositionService;
import it.gov.pagopa.pu.citizen.connector.pagopapayments.PrintPaymentNoticeService;
import it.gov.pagopa.pu.citizen.dto.FileResourceDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionResponseDTO;
import it.gov.pagopa.pu.citizen.mapper.DebtPositionDTOMapper;
import it.gov.pagopa.pu.citizen.mapper.DebtPositionResponseDTOMapper;
import it.gov.pagopa.pu.citizen.service.ZipFileService;
import it.gov.pagopa.pu.citizen.service.organization.OrganizationRetrieverService;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentStatus;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
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

    List<FileResourceDTO> pdfResources = debtPosition
        .getPaymentOptions()
        .stream()
        .flatMap(po ->
            po.getInstallments()
                .stream()
                .filter(
                    i ->
                        (InstallmentStatus.UNPAID.equals(i.getStatus()) ||
                            InstallmentStatus.UNPAYABLE.equals(i.getStatus())))
        )
        .map(i ->
            printPaymentNoticeService.generateNotice(i.getIuv(), debtPosition, accessToken))
        .toList();

    if (pdfResources.isEmpty()) {
      return null;
    }

    return zipFileService.zipper(pdfResources);
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
}
