package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionTypeOrgWithActiveSpontaneousCount;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;

public interface DebtPositionTypeOrgService {
  List<DebtPositionTypeOrgWithActiveSpontaneousCount> getDebtPositionTypeOrgWithActiveSpontaneousCount(List<Long> organizationIds, String accessToken);
  List <DebtPositionTypeOrg> getDebtPositionTypeOrgsFindActiveDebtPositionTypeOrg(Long organizationId, String accessToken);
  DebtPositionTypeOrg getDebtPositionTypeOrg(Long debtPositionTypeOrgId, String accessToken);
  List <DebtPositionTypeOrg> getMostUsedSpontaneousDebtPositionTypesForOrganizationByOrganizationIdAndDate(Long organizationId, OffsetDateTime creationDateFrom, OffsetDateTime creationDateTo, Pageable pageable, String accessToken);
}
