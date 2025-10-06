package it.gov.pagopa.pu.citizen.service.organization;

import it.gov.pagopa.pu.citizen.connector.debtpositions.DebtPositionTypeOrgService;
import it.gov.pagopa.pu.citizen.dto.generated.OrganizationsWithSpontaneousDTO;
import it.gov.pagopa.pu.citizen.mapper.OrganizationsWithSpontaneousDTOMapper;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionTypeOrgWithActiveSpontaneousCount;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrganizationRetrieverServiceImpl implements OrganizationRetrieverService{

  private final BrokerOrganizationsRetrieverService brokerOrganizationsRetrieverService;
  private final DebtPositionTypeOrgService debtPositionTypeOrgService;
  private final OrganizationsWithSpontaneousDTOMapper organizationsWithSpontaneousDTOMapper;



  public OrganizationRetrieverServiceImpl(BrokerOrganizationsRetrieverService brokerOrganizationsRetrieverService, DebtPositionTypeOrgService debtPositionTypeOrgService,
                                          OrganizationsWithSpontaneousDTOMapper organizationsWithSpontaneousDTOMapper
                                          ) {
    this.brokerOrganizationsRetrieverService = brokerOrganizationsRetrieverService;
    this.debtPositionTypeOrgService = debtPositionTypeOrgService;
    this.organizationsWithSpontaneousDTOMapper = organizationsWithSpontaneousDTOMapper;
 }

  @Override
  public List<OrganizationsWithSpontaneousDTO> getOrganizationsWithSpontaneous(Long brokerId, String accessToken) {

    List<Organization> organizations = brokerOrganizationsRetrieverService.getAllActiveOrganizationsByBrokerId(brokerId, accessToken);

    if (organizations.isEmpty()) {
      return Collections.emptyList();
    }

    List<DebtPositionTypeOrgWithActiveSpontaneousCount> debtPositionTypeOrgWithActiveSpontaneousCount = getDebtPositionTypeOrgWithActiveSpontaneousCounts(accessToken, organizations);

    return organizationsWithSpontaneousDTOMapper.map(getOrganizationsFiltered(organizations, debtPositionTypeOrgWithActiveSpontaneousCount));
  }

  private List<DebtPositionTypeOrgWithActiveSpontaneousCount> getDebtPositionTypeOrgWithActiveSpontaneousCounts(String accessToken, List<Organization> organizations) {
    List<Long> organizationsIds = organizations.stream().map(Organization::getOrganizationId).toList();
    return debtPositionTypeOrgService.getDebtPositionTypeOrgWithActiveSpontaneousCount(organizationsIds, accessToken);
  }

  private List<Organization> getOrganizationsFiltered( List<Organization> organizationList, List<DebtPositionTypeOrgWithActiveSpontaneousCount> debtPositionTypeOrgWithActiveSpontaneousCounts) {

    Set<Long> validIds = debtPositionTypeOrgWithActiveSpontaneousCounts
      .stream()
      .map(DebtPositionTypeOrgWithActiveSpontaneousCount::getOrganizationId)
      .collect(Collectors.toSet());

    return organizationList.stream()
      .filter(organization -> validIds.contains(organization.getOrganizationId()))
      .toList();
  }
}
