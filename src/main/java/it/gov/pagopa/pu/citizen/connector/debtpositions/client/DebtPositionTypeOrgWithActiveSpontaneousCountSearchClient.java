package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import it.gov.pagopa.pu.debtpositions.dto.generated.CollectionModelDebtPositionTypeOrgWithActiveSpontaneousCount;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionTypeOrgWithActiveSpontaneousCount;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class DebtPositionTypeOrgWithActiveSpontaneousCountSearchClient {

  private final DebtPositionsApisHolder debtPositionsApisHolder;

  public DebtPositionTypeOrgWithActiveSpontaneousCountSearchClient(DebtPositionsApisHolder debtPositionsApisHolder) {
    this.debtPositionsApisHolder = debtPositionsApisHolder;
  }

  public List<DebtPositionTypeOrgWithActiveSpontaneousCount> getDebtPositionTypeOrgWithActiveSpontaneousCount(List<Long> organizationIds, String accessToken){
    CollectionModelDebtPositionTypeOrgWithActiveSpontaneousCount collectionModelDebtPositionTypeOrgWithActiveSpontaneousCount = debtPositionsApisHolder.getDebtPositionTypeOrgWithActiveSpontaneousCountSearchControllerApi(accessToken)
      .crudDebtPositionTypeOrgWithActiveSpontaneousCountCountByOrganizationIds(organizationIds);
    return collectionModelDebtPositionTypeOrgWithActiveSpontaneousCount != null && collectionModelDebtPositionTypeOrgWithActiveSpontaneousCount.getEmbedded() != null?
      collectionModelDebtPositionTypeOrgWithActiveSpontaneousCount.getEmbedded().getDebtPositionTypeOrgWithActiveSpontaneousCounts() : Collections.emptyList();
  }

}
