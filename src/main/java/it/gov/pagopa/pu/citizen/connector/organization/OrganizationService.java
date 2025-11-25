package it.gov.pagopa.pu.citizen.connector.organization;

import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationStatus;
import it.gov.pagopa.pu.organization.dto.generated.PagedModelOrganization;
import org.springframework.data.domain.Pageable;

public interface OrganizationService {
  Organization getOrganizationByOrganizationId(Long organizationId, String accessToken);
  PagedModelOrganization getPagedOrganizationsByBrokerIdAndStatus(Long brokerId, OrganizationStatus status, Pageable pageable, String accessToken);
  PagedModelOrganization getOrganizationsListByBrokerIdAndOrgName(Long brokerId, String orgName, Pageable pageable, String accessToken);
  PagedModelOrganization getOrganizationsByBrokerIdAndOrgNameAndOrgFiscalCode(Long brokerId, String orgName, String orgFiscalCode, Pageable pageable, String accessToken);
  Organization getBrokerOrganization(Long brokerId, String accessToken);
}
