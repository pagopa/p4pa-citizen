package it.gov.pagopa.pu.citizen.service.organization;

import it.gov.pagopa.pu.citizen.dto.generated.OrganizationsWithSpontaneousDTO;
import java.util.List;

public interface OrganizationRetrieverService {
  List<OrganizationsWithSpontaneousDTO> getOrganizationsWithSpontaneous(Long brokerId, String accessToken);
  void validateOrganization(Long organizationId, Long brokerId, String accessToken);
}
