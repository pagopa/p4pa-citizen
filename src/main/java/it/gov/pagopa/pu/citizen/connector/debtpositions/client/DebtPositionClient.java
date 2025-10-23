package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Service
public class DebtPositionClient {

  private final DebtPositionsApisHolder debtPositionsApisHolder;

  public DebtPositionClient(DebtPositionsApisHolder debtPositionsApisHolder) {
    this.debtPositionsApisHolder = debtPositionsApisHolder;
  }

  public DebtPositionDTO createDebtPosition(DebtPositionDTO debtPositionDTO, Boolean massive, String accessToken){
    return debtPositionsApisHolder.getDebtPositionApi(accessToken).createDebtPosition(debtPositionDTO, massive);
  }

  public DebtPositionDTO getDebtPosition(Long debtPositionId,
      String accessToken) {
    try {
      return debtPositionsApisHolder.getDebtPositionApi(accessToken)
          .getDebtPosition(debtPositionId);
    } catch (HttpClientErrorException.NotFound e) {
      log.warn("DebtPosition with debtPositionId {} not found", debtPositionId);
      return null;
    }
  }
}
