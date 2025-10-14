package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.citizen.connector.debtpositions.client.DebtPositionClient;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import org.springframework.stereotype.Service;

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
}
