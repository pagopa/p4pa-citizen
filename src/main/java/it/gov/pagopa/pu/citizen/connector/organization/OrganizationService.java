package it.gov.pagopa.pu.citizen.connector.organization;

import it.gov.pagopa.pu.organization.dto.generated.PagedModelOrganization;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface OrganizationService {
  PagedModelOrganization getOrganizationsByBrokerIdAndFilters(Long brokerId, String orgName, String ipaCode, Set<Long> organizationIds, Pageable pageable, String accessToken);
}
