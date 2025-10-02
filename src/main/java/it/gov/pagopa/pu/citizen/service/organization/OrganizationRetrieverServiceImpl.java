package it.gov.pagopa.pu.citizen.service.organization;

import it.gov.pagopa.pu.citizen.connector.debtpositions.DebtPositionTypeOrgService;
import it.gov.pagopa.pu.citizen.connector.organization.OrganizationService;
import it.gov.pagopa.pu.citizen.dto.generated.OrganizationsWithSpontaneousDTO;
import it.gov.pagopa.pu.citizen.mapper.OrganizationsWithSpontaneousDTOMapper;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionTypeOrgWithActiveSpontaneousCount;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class OrganizationRetrieverServiceImpl implements OrganizationRetrieverService{

  private final OrganizationService organizationService;
  private final DebtPositionTypeOrgService debtPositionTypeOrgService;
  private final OrganizationsWithSpontaneousDTOMapper organizationsWithSpontaneousDTOMapper;
  private final Integer pageMaxSize;

  public OrganizationRetrieverServiceImpl(OrganizationService organizationService,
                                          DebtPositionTypeOrgService debtPositionTypeOrgService,
                                          OrganizationsWithSpontaneousDTOMapper organizationsWithSpontaneousDTOMapper,
                                          @Value("${rest.page.request-max-page-size}") Integer pageMaxSize) {
    this.organizationService = organizationService;
    this.debtPositionTypeOrgService = debtPositionTypeOrgService;
    this.organizationsWithSpontaneousDTOMapper = organizationsWithSpontaneousDTOMapper;
    this.pageMaxSize = pageMaxSize;
  }

  @Override
  public List<OrganizationsWithSpontaneousDTO> getOrganizationsWithSpontaneous(Long brokerId, String accessToken) {

    Pageable maxPageable = PageRequest.of(0, pageMaxSize);
    List<Organization> organizations = organizationService.getOrganizationsByBrokerIdAndFilters(brokerId, null, null, null, maxPageable, accessToken);

    List<DebtPositionTypeOrgWithActiveSpontaneousCount> debtPositionTypeOrgWithActiveSpontaneousCount = getDebtPositionTypeOrgWithActiveSpontaneousCounts(accessToken, organizations);

    return organizationsWithSpontaneousDTOMapper.map(getOrganizationsFiltered(organizations, debtPositionTypeOrgWithActiveSpontaneousCount));
  }

  private List<DebtPositionTypeOrgWithActiveSpontaneousCount> getDebtPositionTypeOrgWithActiveSpontaneousCounts(String accessToken, List<Organization> organizations) {
    List<Long> organizationsIds = organizations.stream().map(Organization::getOrganizationId).toList();
    return debtPositionTypeOrgService.getDebtPositionTypeOrgWithActiveSpontaneousCount(organizationsIds, accessToken);
  }

  private List<Organization> getOrganizationsFiltered( List<Organization> organizationList, List<DebtPositionTypeOrgWithActiveSpontaneousCount> debtPositionTypeOrgWithActiveSpontaneousCounts) {

    List<Long> validIds = debtPositionTypeOrgWithActiveSpontaneousCounts
      .stream()
      .map(DebtPositionTypeOrgWithActiveSpontaneousCount::getOrganizationId)
      .toList();

    return organizationList.stream()
      .filter(organization -> validIds.contains(organization.getOrganizationId()))
      .toList();
  }
}
