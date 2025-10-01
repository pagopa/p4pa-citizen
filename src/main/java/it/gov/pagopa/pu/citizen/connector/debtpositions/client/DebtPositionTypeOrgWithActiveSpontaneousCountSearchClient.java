package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import it.gov.pagopa.pu.debtpositions.dto.generated.CollectionModelDebtPositionTypeOrgWithActiveSpontaneousCount;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DebtPositionTypeOrgWithActiveSpontaneousCountSearchClient {

  private final DebtPositionsApisHolder debtPositionsApisHolder;

  public DebtPositionTypeOrgWithActiveSpontaneousCountSearchClient(DebtPositionsApisHolder debtPositionsApisHolder) {
    this.debtPositionsApisHolder = debtPositionsApisHolder;
  }

  public CollectionModelDebtPositionTypeOrgWithActiveSpontaneousCount getDebtPositionTypeOrgWithActiveSpontaneousCount(List<Long> organizationIds, String accessToken){
    return debtPositionsApisHolder.getDebtPositionTypeOrgWithActiveSpontaneousCountSearchControllerApi(accessToken)
      .crudDebtPositionTypeOrgWithActiveSpontaneousCountCountByOrganizationIds(organizationIds);
  }

}
