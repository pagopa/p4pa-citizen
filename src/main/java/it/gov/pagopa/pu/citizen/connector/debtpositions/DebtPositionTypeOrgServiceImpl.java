package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.citizen.connector.debtpositions.client.DebtPositionTypeOrgSearchClient;
import it.gov.pagopa.pu.citizen.connector.debtpositions.client.DebtPositionTypeOrgWithActiveSpontaneousCountSearchClient;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionTypeOrgWithActiveSpontaneousCount;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DebtPositionTypeOrgServiceImpl implements DebtPositionTypeOrgService{

  private final DebtPositionTypeOrgWithActiveSpontaneousCountSearchClient debtPositionTypeOrgWithActiveSpontaneousCountSearchClient;
  private final DebtPositionTypeOrgSearchClient debtPositionTypeOrgSearchClient;

  public DebtPositionTypeOrgServiceImpl(DebtPositionTypeOrgWithActiveSpontaneousCountSearchClient debtPositionTypeOrgWithActiveSpontaneousCountSearchClient, DebtPositionTypeOrgSearchClient debtPositionTypeOrgSearchClient) {
    this.debtPositionTypeOrgWithActiveSpontaneousCountSearchClient = debtPositionTypeOrgWithActiveSpontaneousCountSearchClient;
    this.debtPositionTypeOrgSearchClient = debtPositionTypeOrgSearchClient;
  }

  @Override
  public List<DebtPositionTypeOrgWithActiveSpontaneousCount> getDebtPositionTypeOrgWithActiveSpontaneousCount(List<Long> organizationIds, String accessToken) {
    return debtPositionTypeOrgWithActiveSpontaneousCountSearchClient.getDebtPositionTypeOrgWithActiveSpontaneousCount(organizationIds, accessToken);
  }

  @Override
  public List<DebtPositionTypeOrg> getDebtPositionTypeOrgsFindActiveDebtPositionTypeOrg(Long organizationId, String accessToken) {
    return debtPositionTypeOrgSearchClient.getDebtPositionTypeOrgsFindActiveDebtPositionTypeOrg(organizationId, accessToken);
  }
}
