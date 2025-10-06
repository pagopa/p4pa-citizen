package it.gov.pagopa.pu.citizen.connector.organization;

import it.gov.pagopa.pu.organization.dto.generated.OrganizationStatus;
import it.gov.pagopa.pu.organization.dto.generated.PagedModelOrganization;
import org.springframework.data.domain.Pageable;

public interface OrganizationService {
  PagedModelOrganization getPagedOrganizationsByBrokerIdAndStatus(Long brokerId, OrganizationStatus status, Pageable pageable, String accessToken);
}
