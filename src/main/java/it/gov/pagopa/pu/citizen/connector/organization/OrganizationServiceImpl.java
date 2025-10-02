package it.gov.pagopa.pu.citizen.connector.organization;

import it.gov.pagopa.pu.citizen.connector.organization.client.OrganizationSearchClient;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class OrganizationServiceImpl implements OrganizationService {

  private final OrganizationSearchClient organizationSearchClient;

  public OrganizationServiceImpl(OrganizationSearchClient organizationSearchClient) {
    this.organizationSearchClient = organizationSearchClient;
  }

  @Override
  public List<Organization> getOrganizationsByBrokerIdAndFilters(Long brokerId, String orgName, String ipaCode, Set<Long> organizationIds, Pageable pageable, String accessToken) {
    return organizationSearchClient.getOrganizationsByBrokerIdAndFilters(brokerId, orgName, ipaCode, organizationIds, pageable, accessToken);
  }
}
