package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionTypeOrgWithActiveSpontaneousCount;

import java.util.List;

public interface DebtPositionTypeOrgService {
  List<DebtPositionTypeOrgWithActiveSpontaneousCount> getDebtPositionTypeOrgWithActiveSpontaneousCount(List<Long> organizationIds, String accessToken);
}
