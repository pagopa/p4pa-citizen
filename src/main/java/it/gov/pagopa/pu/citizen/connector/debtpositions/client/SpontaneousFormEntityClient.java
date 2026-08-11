package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import it.gov.pagopa.pu.citizen.exception.common.RestInvokeNotFoundException;
import it.gov.pagopa.pu.debtpositions.dto.generated.SpontaneousForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SpontaneousFormEntityClient {
  private final DebtPositionsApisHolder debtPositionApisHolder;

  public SpontaneousFormEntityClient(DebtPositionsApisHolder debtPositionApisHolder) {
    this.debtPositionApisHolder = debtPositionApisHolder;
  }

  public SpontaneousForm getSpontaneousForm(Long spontaneousFormId, String accessToken){
    try {
      return debtPositionApisHolder.getSpontaneousFormEntityControllerApi(accessToken).crudGetSpontaneousform(String.valueOf(spontaneousFormId));
    }catch (RestInvokeNotFoundException e) {
      log.warn("SpontaneousForm with spontaneousFormId {} not found", spontaneousFormId);
      return null;
    }
  }
}
