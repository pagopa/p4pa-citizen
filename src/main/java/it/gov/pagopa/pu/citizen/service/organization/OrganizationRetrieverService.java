package it.gov.pagopa.pu.citizen.service.organization;

import it.gov.pagopa.pu.citizen.dto.generated.OrganizationsWithSpontaneousDTO;
import it.gov.pagopa.pu.organization.dto.generated.Organization;

import java.util.List;

public interface OrganizationRetrieverService {
  List<OrganizationsWithSpontaneousDTO> getOrganizationsWithSpontaneous(Long brokerId, String accessToken);
  void validateOrganization(Long organizationId, Long brokerId, String accessToken);
  Organization getValidOrganization(Long organizationId, Long brokerId, String accessToken);
  Organization getValidOrganization(String orgFiscalCode, Long brokerId, String accessToken);
  boolean isCieBroker(Long brokerId, String accessToken);
  Organization getCieOrganization(String accessToken);
  boolean isDelegateBroker(Long brokerId, String accessToken);
}
