package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import it.gov.pagopa.pu.debtpositions.dto.generated.CollectionModelDebtPositionTypeOrg;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionTypeOrg;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class DebtPositionTypeOrgSearchClient {

  private final DebtPositionsApisHolder debtPositionsApisHolder;

  public DebtPositionTypeOrgSearchClient(DebtPositionsApisHolder debtPositionsApisHolder) {
    this.debtPositionsApisHolder = debtPositionsApisHolder;
  }

  public List<DebtPositionTypeOrg> getDebtPositionTypeOrgsFindActiveDebtPositionTypeOrg(Long organizationId, String accessToken){
    CollectionModelDebtPositionTypeOrg collectionModelDebtPositionTypeOrg = debtPositionsApisHolder.getDebtPositionTypeOrgSearchControllerApi(accessToken)
      .crudDebtPositionTypeOrgsFindActiveDebtPositionTypeOrg(organizationId);
    return collectionModelDebtPositionTypeOrg != null && collectionModelDebtPositionTypeOrg.getEmbedded() != null?
      collectionModelDebtPositionTypeOrg.getEmbedded().getDebtPositionTypeOrgs() : Collections.emptyList();
  }

  public List<DebtPositionTypeOrg> getCurrentYearTopTenSpontaneousDebtPositionTypeOrgByOrganizationId(Long organizationId, String accessToken){
    CollectionModelDebtPositionTypeOrg collectionModelDebtPositionTypeOrg = debtPositionsApisHolder.getDebtPositionTypeOrgSearchControllerApi(accessToken)
      .crudDebtPositionTypeOrgsGetCurrentYearTopTenSpontaneousDebtPositionTypeOrgByOrganizationId(organizationId);
    return collectionModelDebtPositionTypeOrg != null && collectionModelDebtPositionTypeOrg.getEmbedded() != null?
      collectionModelDebtPositionTypeOrg.getEmbedded().getDebtPositionTypeOrgs() : Collections.emptyList();
  }
}
