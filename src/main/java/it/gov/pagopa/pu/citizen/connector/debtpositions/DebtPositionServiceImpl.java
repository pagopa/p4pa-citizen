package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.citizen.connector.debtpositions.client.DebtPositionClient;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionOrigin;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtorDebtPositionDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.PagedDebtorUnpaidDebtPositionDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DebtPositionServiceImpl implements DebtPositionService{

  private final DebtPositionClient debtPositionClient;

  public DebtPositionServiceImpl(DebtPositionClient debtPositionClient) {
    this.debtPositionClient = debtPositionClient;
  }

  @Override
  public DebtPositionDTO createDebtPosition(DebtPositionDTO debtPositionDTO, Boolean massive, String accessToken){
    return debtPositionClient.createDebtPosition(debtPositionDTO, massive, accessToken);
  }

  @Override
  public DebtPositionDTO getDebtPosition(Long debtPositionId, String accessToken) {
    return debtPositionClient.getDebtPosition(debtPositionId, accessToken);
  }

  @Override
  public List<DebtPositionDTO> getDebtPositionsByOrganizationIdAndNav(Long organizationId, String nav, List<DebtPositionOrigin> debtPositionOrigins, String accessToken) {
    return debtPositionClient.getDebtPositionsByOrganizationIdAndNav(organizationId, nav, debtPositionOrigins, accessToken);
  }

  @Override
  public PagedDebtorUnpaidDebtPositionDTO getPagedDebtorUnpaidDebtPosition(String debtorFiscalCode, List<Long> organizationIds, Pageable pageable, String accessToken) {
    return debtPositionClient.getPagedDebtorUnpaidDebtPosition(debtorFiscalCode, organizationIds, pageable, accessToken);
  }

  @Override
  public DebtorDebtPositionDTO getDebtorDebtPositionOverview(Long debtPositionId, String debtorFiscalCode, Long organizationId, String accessToken) {
    return debtPositionClient.getDebtorDebtPositionOverview(debtPositionId, debtorFiscalCode, organizationId, accessToken);
  }
}
