package it.gov.pagopa.pu.citizen.service.debtpositiontypeorg;

import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDTO;

import java.util.List;

public interface DebtPositionTypeOrgRetrieverService {
  List<DebtPositionTypeOrgsWithSpontaneousDTO> getDebtPositionTypeOrgsWithSpontaneous(Long brokerId, Long organizationId, String accessToken);
}
