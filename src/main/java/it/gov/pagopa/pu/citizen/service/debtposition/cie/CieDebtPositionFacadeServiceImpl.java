package it.gov.pagopa.pu.citizen.service.debtposition.cie;

import it.gov.pagopa.pu.citizen.connector.cie.CieDebtPositionService;
import it.gov.pagopa.pu.citizen.dto.FileResourceDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionResponseDTO;
import it.gov.pagopa.pu.citizen.mapper.DebtPositionResponseDTOMapper;
import it.gov.pagopa.pu.citizen.mapper.cie.DebtPositionCieRequestDTOMapper;
import it.gov.pagopa.pu.citizen.service.debtpositiontypeorg.DebtPositionTypeOrgRetrieverService;
import it.gov.pagopa.pu.citizen.service.organization.OrganizationRetrieverService;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.springframework.stereotype.Service;

@Service
public class CieDebtPositionFacadeServiceImpl implements CieDebtPositionFacadeService {
  private final CieDebtPositionService cieDebtPositionService;
  private final DebtPositionCieRequestDTOMapper debtPositionCieRequestDTOMapper;
  private final DebtPositionTypeOrgRetrieverService debtPositionTypeOrgRetrieverService;
  private final DebtPositionResponseDTOMapper debtPositionResponseDTOMapper;
  private final OrganizationRetrieverService organizationRetrieverService;

  public CieDebtPositionFacadeServiceImpl(CieDebtPositionService cieDebtPositionService, DebtPositionCieRequestDTOMapper debtPositionCieRequestDTOMapper,
                                          DebtPositionTypeOrgRetrieverService debtPositionTypeOrgRetrieverService, DebtPositionResponseDTOMapper debtPositionResponseDTOMapper, OrganizationRetrieverService organizationRetrieverService) {
    this.cieDebtPositionService = cieDebtPositionService;
    this.debtPositionCieRequestDTOMapper = debtPositionCieRequestDTOMapper;
    this.debtPositionTypeOrgRetrieverService = debtPositionTypeOrgRetrieverService;
    this.debtPositionResponseDTOMapper = debtPositionResponseDTOMapper;
    this.organizationRetrieverService = organizationRetrieverService;
  }

  @Override
  public DebtPositionResponseDTO createSpontaneousDebtPosition(DebtPositionRequestDTO debtPositionRequestDTO, String accessToken) {
    Organization cieOrganization = organizationRetrieverService.getCieOrganization(accessToken);
    DebtPositionDTO debtPosition = cieDebtPositionService.createDebtPositionCie(
      debtPositionCieRequestDTOMapper.map(
        debtPositionRequestDTO,
        debtPositionTypeOrgRetrieverService.getDebtPositionTypeOrgCode(debtPositionRequestDTO.getDebtPositionTypeOrgId(), debtPositionRequestDTO.getOrganizationId(), accessToken)),
      cieOrganization.getIpaCode()
    );
    return debtPositionResponseDTOMapper.map(debtPosition,cieOrganization,true);
  }

  @Override
  public FileResourceDTO generateNoticeCie(String nav, String fiscalCode, String accessToken) {
    return cieDebtPositionService.generateNoticeCie(nav, fiscalCode, accessToken);
  }
}
