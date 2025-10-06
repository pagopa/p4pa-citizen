package it.gov.pagopa.pu.citizen.connector.organization;

import it.gov.pagopa.pu.citizen.connector.organization.client.OrganizationSearchClient;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationStatus;
import it.gov.pagopa.pu.organization.dto.generated.PagedModelOrganization;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrganizationServiceImpl implements OrganizationService {

  private final OrganizationSearchClient organizationSearchClient;

  public OrganizationServiceImpl(OrganizationSearchClient organizationSearchClient) {
    this.organizationSearchClient = organizationSearchClient;
  }

  @Override
  public PagedModelOrganization getPagedOrganizationsByBrokerIdAndStatus(Long brokerId, OrganizationStatus status, Pageable pageable, String accessToken) {
    return organizationSearchClient.getPagedOrganizationsByBrokerIdAndStatus(brokerId, status, pageable, accessToken);
  }
}
