package it.gov.pagopa.pu.citizen.service.debtposition;

import it.gov.pagopa.pu.citizen.connector.debtpositions.DebtPositionService;
import it.gov.pagopa.pu.citizen.connector.organization.OrganizationService;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionResponseDTO;
import it.gov.pagopa.pu.citizen.exception.ResourceNotFoundException;
import it.gov.pagopa.pu.citizen.mapper.DebtPositionDTOMapper;
import it.gov.pagopa.pu.citizen.mapper.DebtPositionResponseDTOMapper;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DebtPositionRetrieverServiceImpl implements DebtPositionRetrieverService{

  private final DebtPositionService debtPositionService;
  private final DebtPositionDTOMapper debtPositionDTOMapper;
  private final Integer expirationDays;
  private final OrganizationService organizationService;
  private final DebtPositionResponseDTOMapper debtPositionResponseDTOMapper;

  public DebtPositionRetrieverServiceImpl(DebtPositionService debtPositionService,
                                          DebtPositionDTOMapper debtPositionDTOMapper,
                                          @Value("${spontaneous.expiration-days}")Integer expirationDays,
                                          OrganizationService organizationService, DebtPositionResponseDTOMapper debtPositionResponseDTOMapper
  ) {
    this.debtPositionDTOMapper = debtPositionDTOMapper;
    this.debtPositionService = debtPositionService;
    this.expirationDays = expirationDays;
    this.organizationService = organizationService;
    this.debtPositionResponseDTOMapper = debtPositionResponseDTOMapper;
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

}
