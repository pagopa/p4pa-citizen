package it.gov.pagopa.pu.citizen.service.organization;

import it.gov.pagopa.pu.citizen.dto.generated.OrganizationsWithSpontaneousDTO;

import java.util.List;

public interface OrganizationRetrieverService {
  List<OrganizationsWithSpontaneousDTO> getOrganizationsListWithSpontaneous(Long brokerId, String accessToken);
}
