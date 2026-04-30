package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionTypeOrg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Service
public class DebtPositionTypeOrgEntityClient {

  private final DebtPositionsApisHolder debtPositionsApisHolder;

  public DebtPositionTypeOrgEntityClient(DebtPositionsApisHolder debtPositionsApisHolder) {
    this.debtPositionsApisHolder = debtPositionsApisHolder;
  }

  public DebtPositionTypeOrg getDebtPositionTypeOrg(Long debtPositionTypeOrgId, String accessToken) {
    try {
      return debtPositionsApisHolder.getDebtPositionTypeOrgEntityControllerApi(accessToken)
        .crudGetDebtpositiontypeorg(String.valueOf(debtPositionTypeOrgId));
    } catch (HttpClientErrorException.NotFound e) {
      log.warn("DebtPositionTypeOrg with debtPositionTypeOrgId {} not found", debtPositionTypeOrgId);
      return null;
    }
  }
}
