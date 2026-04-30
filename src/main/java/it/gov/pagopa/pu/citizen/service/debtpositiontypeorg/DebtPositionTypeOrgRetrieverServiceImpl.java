package it.gov.pagopa.pu.citizen.service.debtpositiontypeorg;

import it.gov.pagopa.pu.citizen.connector.debtpositions.DebtPositionTypeOrgService;
import it.gov.pagopa.pu.citizen.connector.debtpositions.SpontaneousFormService;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDetailsDTO;
import it.gov.pagopa.pu.citizen.exception.ResourceNotFoundException;
import it.gov.pagopa.pu.citizen.mapper.DebtPositionTypeOrgsWithSpontaneousDTOMapper;
import it.gov.pagopa.pu.citizen.mapper.DebtPositionTypeOrgsWithSpontaneousDetailsDTOMapper;
import it.gov.pagopa.pu.citizen.service.organization.OrganizationRetrieverService;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtpositions.dto.generated.SpontaneousForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class DebtPositionTypeOrgRetrieverServiceImpl implements DebtPositionTypeOrgRetrieverService{

  private final DebtPositionTypeOrgService debtPositionTypeOrgService;
  private final SpontaneousFormService spontaneousFormService;
  private final DebtPositionTypeOrgsWithSpontaneousDTOMapper debtPositionTypeOrgsListWithSpontaneousDTOMapper;
  private final DebtPositionTypeOrgsWithSpontaneousDetailsDTOMapper debtPositionTypeOrgsWithSpontaneousDetailsDTOMapper;
  private final OrganizationRetrieverService organizationRetrieverService;

  public DebtPositionTypeOrgRetrieverServiceImpl(DebtPositionTypeOrgService debtPositionTypeOrgService, SpontaneousFormService spontaneousFormService, DebtPositionTypeOrgsWithSpontaneousDTOMapper debtPositionTypeOrgsListWithSpontaneousDTOMapper, DebtPositionTypeOrgsWithSpontaneousDetailsDTOMapper debtPositionTypeOrgsWithSpontaneousDetailsDTOMapper, OrganizationRetrieverService organizationRetrieverService) {
    this.debtPositionTypeOrgService = debtPositionTypeOrgService;
    this.spontaneousFormService = spontaneousFormService;
    this.debtPositionTypeOrgsListWithSpontaneousDTOMapper = debtPositionTypeOrgsListWithSpontaneousDTOMapper;
    this.debtPositionTypeOrgsWithSpontaneousDetailsDTOMapper = debtPositionTypeOrgsWithSpontaneousDetailsDTOMapper;
    this.organizationRetrieverService = organizationRetrieverService;
  }

  @Override
  public List<DebtPositionTypeOrgsWithSpontaneousDTO> getDebtPositionTypeOrgsWithSpontaneous(Long brokerId, Long organizationId, String accessToken) {
    organizationRetrieverService.validateOrganization(organizationId, brokerId, accessToken);
    return debtPositionTypeOrgsListWithSpontaneousDTOMapper.map(debtPositionTypeOrgService.getDebtPositionTypeOrgsFindActiveDebtPositionTypeOrg(organizationId, accessToken));
  }

  @Override
  public DebtPositionTypeOrgsWithSpontaneousDetailsDTO getDebtPositionTypeOrgsWithSpontaneousDetailsDTO(Long brokerId, Long organizationId, Long debtPositionTypeOrgId, String accessToken) {
    organizationRetrieverService.validateOrganization(organizationId, brokerId, accessToken);
    DebtPositionTypeOrg debtPositionTypeOrg = debtPositionTypeOrgService.getDebtPositionTypeOrg(debtPositionTypeOrgId, accessToken);

    if (debtPositionTypeOrg == null || !Objects.equals(organizationId, debtPositionTypeOrg.getOrganizationId())){
      throw new ResourceNotFoundException("DEBT_POSITION_TYPE_ORG_NOT_FOUND","DebtPositionTypeOrg with deptPositionTypeOrgId %d and organizationId %d not found".formatted(debtPositionTypeOrgId, organizationId));
    }

    return debtPositionTypeOrgsWithSpontaneousDetailsDTOMapper.map(debtPositionTypeOrg, getSpontaneousForm(accessToken, debtPositionTypeOrg));
  }

  @Override
  public List<DebtPositionTypeOrgsWithSpontaneousDTO> getMostUsedSpontaneousDebtPositionTypeOrgs(Long brokerId, Long organizationId, OffsetDateTime creationDateFrom, OffsetDateTime creationDateTo, Pageable pageable, String accessToken) {
    organizationRetrieverService.validateOrganization(organizationId, brokerId, accessToken);
    return debtPositionTypeOrgsListWithSpontaneousDTOMapper.map(debtPositionTypeOrgService.getMostUsedSpontaneousDebtPositionTypesForOrganizationByOrganizationIdAndDate(organizationId, creationDateFrom, creationDateTo, pageable, accessToken));
  }

  private SpontaneousForm getSpontaneousForm(String accessToken, DebtPositionTypeOrg debtPositionTypeOrg) {
    if (debtPositionTypeOrg.getSpontaneousFormId() != null){
      return spontaneousFormService.getSpontaneousForm(debtPositionTypeOrg.getSpontaneousFormId(), accessToken);
    }
    return null;
  }

  @Override
  public String getDebtPositionTypeOrgCode(Long debtPositionTypeOrgId, Long organizationId, String accessToken) {
    DebtPositionTypeOrg debtPositionTypeOrg = debtPositionTypeOrgService.getDebtPositionTypeOrg(debtPositionTypeOrgId, accessToken);
    if(debtPositionTypeOrg==null || !debtPositionTypeOrg.getOrganizationId().equals(organizationId)){
      throw new ResourceNotFoundException("DEBT_POSITION_TYPE_ORG_NOT_FOUND","DebtPositionTypeOrg with deptPositionTypeOrgId %d and organizationId %d not found".formatted(debtPositionTypeOrgId, organizationId));
    }
    return debtPositionTypeOrg.getCode();
  }
}
