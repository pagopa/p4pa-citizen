package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.citizen.connector.debtpositions.client.DebtPositionTypeOrgEntityClient;
import it.gov.pagopa.pu.citizen.connector.debtpositions.client.DebtPositionTypeOrgSearchClient;
import it.gov.pagopa.pu.citizen.connector.debtpositions.client.DebtPositionTypeOrgWithActiveSpontaneousCountSearchClient;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionTypeOrgWithActiveSpontaneousCount;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class DebtPositionTypeOrgServiceImpl implements DebtPositionTypeOrgService{

  private final DebtPositionTypeOrgWithActiveSpontaneousCountSearchClient debtPositionTypeOrgWithActiveSpontaneousCountSearchClient;
  private final DebtPositionTypeOrgSearchClient debtPositionTypeOrgSearchClient;
  private final DebtPositionTypeOrgEntityClient debtPositionTypeOrgEntityClient;

  public DebtPositionTypeOrgServiceImpl(DebtPositionTypeOrgWithActiveSpontaneousCountSearchClient debtPositionTypeOrgWithActiveSpontaneousCountSearchClient, DebtPositionTypeOrgSearchClient debtPositionTypeOrgSearchClient, DebtPositionTypeOrgEntityClient debtPositionTypeOrgEntityClient) {
    this.debtPositionTypeOrgWithActiveSpontaneousCountSearchClient = debtPositionTypeOrgWithActiveSpontaneousCountSearchClient;
    this.debtPositionTypeOrgSearchClient = debtPositionTypeOrgSearchClient;
    this.debtPositionTypeOrgEntityClient = debtPositionTypeOrgEntityClient;
  }

  @Override
  public List<DebtPositionTypeOrgWithActiveSpontaneousCount> getDebtPositionTypeOrgWithActiveSpontaneousCount(List<Long> organizationIds, String accessToken) {
    return debtPositionTypeOrgWithActiveSpontaneousCountSearchClient.getDebtPositionTypeOrgWithActiveSpontaneousCount(organizationIds, accessToken);
  }

  @Override
  public List<DebtPositionTypeOrg> getDebtPositionTypeOrgsFindActiveDebtPositionTypeOrg(Long organizationId, String accessToken) {
    return debtPositionTypeOrgSearchClient.getDebtPositionTypeOrgsFindActiveDebtPositionTypeOrg(organizationId, accessToken);
  }

  @Override
  public DebtPositionTypeOrg getDebtPositionTypeOrg(Long debtPositionTypeOrgId, String accessToken) {
      return debtPositionTypeOrgEntityClient.getDebtPositionTypeOrg(debtPositionTypeOrgId, accessToken);
  }

  @Override
  public List<DebtPositionTypeOrg> getMostUsedSpontaneousDebtPositionTypesForOrganizationByOrganizationIdAndDate(Long organizationId, OffsetDateTime creationDateFrom, OffsetDateTime creationDateTo, Pageable pageable, String accessToken) {
    return debtPositionTypeOrgSearchClient.getMostUsedSpontaneousDebtPositionTypesForOrganizationByOrganizationIdAndDate(organizationId, creationDateFrom, creationDateTo, pageable, accessToken);
  }
}
