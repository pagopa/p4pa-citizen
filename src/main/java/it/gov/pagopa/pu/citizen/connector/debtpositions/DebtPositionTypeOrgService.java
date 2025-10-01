package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.debtpositions.dto.generated.CollectionModelDebtPositionTypeOrgWithActiveSpontaneousCount;

import java.util.List;

public interface DebtPositionTypeOrgService {
  CollectionModelDebtPositionTypeOrgWithActiveSpontaneousCount getDebtPositionTypeOrgWithActiveSpontaneousCount(List<Long> organizationIds, String accessToken);
}
