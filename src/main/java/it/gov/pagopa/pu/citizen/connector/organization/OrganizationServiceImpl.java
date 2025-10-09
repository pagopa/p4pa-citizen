package it.gov.pagopa.pu.citizen.connector.organization;

import it.gov.pagopa.pu.citizen.connector.organization.client.OrganizationEntityClient;
import it.gov.pagopa.pu.citizen.connector.organization.client.OrganizationSearchClient;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationStatus;
import it.gov.pagopa.pu.organization.dto.generated.PagedModelOrganization;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrganizationServiceImpl implements OrganizationService {

  private final OrganizationSearchClient organizationSearchClient;
  private final OrganizationEntityClient organizationEntityClient;

  public OrganizationServiceImpl(OrganizationSearchClient organizationSearchClient, OrganizationEntityClient organizationEntityClient) {
    this.organizationSearchClient = organizationSearchClient;
    this.organizationEntityClient = organizationEntityClient;
  }

  @Override
  public PagedModelOrganization getPagedOrganizationsByBrokerIdAndStatus(Long brokerId, OrganizationStatus status, Pageable pageable, String accessToken) {
    return organizationSearchClient.getPagedOrganizationsByBrokerIdAndStatus(brokerId, status, pageable, accessToken);
  }

  @Override
  @Cacheable(key = "#organizationId", unless="#result == null")
  public Organization getOrganizationByOrganizationId(Long organizationId, String accessToken){
    return organizationEntityClient.getOrganizationByOrganizationId(organizationId, accessToken);
  }
}
