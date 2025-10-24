package it.gov.pagopa.pu.citizen.service.organization;

import it.gov.pagopa.pu.citizen.dto.generated.OrganizationsWithSpontaneousDTO;
import it.gov.pagopa.pu.organization.dto.generated.Organization;

import java.util.List;

public interface OrganizationRetrieverService {
  List<OrganizationsWithSpontaneousDTO> getOrganizationsWithSpontaneous(Long brokerId, String accessToken);
  Organization validateOrganization(Long organizationId, Long brokerId, String accessToken);
}
