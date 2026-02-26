package it.gov.pagopa.pu.citizen.connector.cie;

import it.gov.pagopa.pu.cie.dto.generated.DebtPositionCieRequestDTO;
import it.gov.pagopa.pu.citizen.connector.cie.client.CieDebtPositionClient;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import org.springframework.stereotype.Service;

@Service
public class CieDebtPositionServiceImpl implements CieDebtPositionService {
  private final CieDebtPositionClient cieDebtPositionClient;

  public CieDebtPositionServiceImpl(CieDebtPositionClient cieDebtPositionClient) {
    this.cieDebtPositionClient = cieDebtPositionClient;
  }

  @Override
  public DebtPositionDTO createDebtPositionCie(DebtPositionCieRequestDTO debtPositionCieRequestDTO, String accessToken) {
    return cieDebtPositionClient.createDebtPositionCie(debtPositionCieRequestDTO, accessToken);
  }
}
