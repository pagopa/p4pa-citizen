package it.gov.pagopa.pu.citizen.service.debtposition;

import it.gov.pagopa.pu.citizen.connector.debtpositions.DebtPositionService;
import it.gov.pagopa.pu.citizen.connector.organization.OrganizationService;
import it.gov.pagopa.pu.citizen.connector.pagopapayments.PrintPaymentNoticeService;
import it.gov.pagopa.pu.citizen.dto.FileResourceDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionResponseDTO;
import it.gov.pagopa.pu.citizen.exception.ResourceNotFoundException;
import it.gov.pagopa.pu.citizen.mapper.DebtPositionDTOMapper;
import it.gov.pagopa.pu.citizen.mapper.DebtPositionResponseDTOMapper;
import it.gov.pagopa.pu.citizen.service.ZipFileService;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentStatus;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;

@Service
public class DebtPositionRetrieverServiceImpl implements DebtPositionRetrieverService{

  private final DebtPositionService debtPositionService;
  private final DebtPositionDTOMapper debtPositionDTOMapper;
  private final Integer expirationDays;
  private final OrganizationService organizationService;
  private final DebtPositionResponseDTOMapper debtPositionResponseDTOMapper;
  private final PrintPaymentNoticeService printPaymentNoticeService;
  private final ZipFileService zipFileService;

  public DebtPositionRetrieverServiceImpl(DebtPositionService debtPositionService,
                                          DebtPositionDTOMapper debtPositionDTOMapper,
                                          @Value("${spontaneous.expiration-days}")Integer expirationDays,
                                          OrganizationService organizationService, DebtPositionResponseDTOMapper debtPositionResponseDTOMapper,
      PrintPaymentNoticeService printPaymentNoticeService, ZipFileService zipFileService
  ) {
    this.debtPositionDTOMapper = debtPositionDTOMapper;
    this.debtPositionService = debtPositionService;
    this.expirationDays = expirationDays;
    this.organizationService = organizationService;
    this.debtPositionResponseDTOMapper = debtPositionResponseDTOMapper;
    this.printPaymentNoticeService = printPaymentNoticeService;
    this.zipFileService = zipFileService;
  }

  @Override
  public DebtPositionResponseDTO createSpontaneousDebtPosition(DebtPositionRequestDTO debtPositionRequestDTO, String accessToken) {
    DebtPositionDTO debtPosition = debtPositionService.createDebtPosition(debtPositionDTOMapper.mapSpontaneousDebtPositionDTO(debtPositionRequestDTO, expirationDays), false, accessToken);
    Organization organization = getOrganization(debtPositionRequestDTO.getOrganizationId(), accessToken);
    return debtPositionResponseDTOMapper.map(debtPosition, organization);
  }

  private Organization getOrganization(Long organizationId, String accessToken) {
    Organization organization = organizationService.getOrganizationByOrganizationId(organizationId, accessToken);
    if (organization == null){
      throw new ResourceNotFoundException("Organization with id %d not found".formatted(organizationId));
    }
    return organization;
  }

  @Override
  public Resource getDebtPositionNoticesZip(String fiscalCode, Long debtPositionId, String accessToken) {
    DebtPositionDTO debtPosition = debtPositionService.getDebtPosition(debtPositionId, accessToken);
    if (debtPosition == null) {
      return null;
    }
    validateDebtPositionDebtor(fiscalCode, debtPosition);

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

  private static void validateDebtPositionDebtor(String fiscalCode, DebtPositionDTO debtPosition) {
    Objects.requireNonNull(debtPosition).getPaymentOptions().stream().flatMap(po->po.getInstallments().stream())
        .filter(i-> fiscalCode.equals(i.getDebtor().getFiscalCode())).findAny()
        .orElseThrow(() -> new AuthorizationDeniedException("User cannot access DebtPosition having id "+ debtPosition.getDebtPositionId()));
  }
}
