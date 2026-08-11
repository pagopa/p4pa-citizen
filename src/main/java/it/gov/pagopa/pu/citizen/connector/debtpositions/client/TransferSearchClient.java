package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import it.gov.pagopa.pu.citizen.exception.common.RestInvokeNotFoundException;
import it.gov.pagopa.pu.debtpositions.dto.generated.PostalIbanVerifyResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TransferSearchClient {

  private final DebtPositionsApisHolder debtPositionsApisHolder;

  public TransferSearchClient(DebtPositionsApisHolder debtPositionsApisHolder) {
    this.debtPositionsApisHolder = debtPositionsApisHolder;
  }

  public PostalIbanVerifyResponse verifyPostalIban(List<Long> installmentIds, String accessToken) {
    try {
      return debtPositionsApisHolder.getTransferApi(accessToken).verifyPostalIban(installmentIds);
    } catch (RestInvokeNotFoundException e) {
      log.warn("Transfers with following installmentIds {} were not found", installmentIds);
      return null;
    }
  }
}
