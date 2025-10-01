package it.gov.pagopa.pu.citizen.service.organization;

import it.gov.pagopa.pu.citizen.connector.debtpositions.DebtPositionTypeOrgService;
import it.gov.pagopa.pu.citizen.connector.organization.OrganizationService;
import it.gov.pagopa.pu.citizen.dto.generated.OrganizationsWithSpontaneousDTO;
import it.gov.pagopa.pu.citizen.exception.ResourceNotFoundException;
import it.gov.pagopa.pu.citizen.mapper.OrganizationsWithSpontaneousDTOMapper;
import it.gov.pagopa.pu.debtpositions.dto.generated.CollectionModelDebtPositionTypeOrgWithActiveSpontaneousCount;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.organization.dto.generated.PagedModelOrganization;
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
  public List<OrganizationsWithSpontaneousDTO> getOrganizationsListWithSpontaneous(Long brokerId, String accessToken) {

    Pageable maxPageable = PageRequest.of(0, pageMaxSize);
    PagedModelOrganization organizations = organizationService.getOrganizationsByBrokerIdAndFilters(brokerId, null, null, null, maxPageable, accessToken);

    if (organizations == null ||
      organizations.getEmbedded() == null
      || organizations.getEmbedded().getOrganizations() == null){
      throw new ResourceNotFoundException("No organizations found for broker with id %d".formatted(brokerId));
    }

    List<Long> organizationsIds = organizations.getEmbedded().getOrganizations().stream().map(Organization::getOrganizationId).toList();
    CollectionModelDebtPositionTypeOrgWithActiveSpontaneousCount debtPositionTypeOrgWithActiveSpontaneousCount = debtPositionTypeOrgService.getDebtPositionTypeOrgWithActiveSpontaneousCount(organizationsIds, accessToken);

    return organizationsWithSpontaneousDTOMapper.map(organizations, debtPositionTypeOrgWithActiveSpontaneousCount);
  }
}
