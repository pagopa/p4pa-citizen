package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import it.gov.pagopa.pu.debtpositions.dto.generated.CollectionModelPaymentOption;
import it.gov.pagopa.pu.debtpositions.dto.generated.PaymentOption;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class PaymentOptionSearchClient {

  private final DebtPositionsApisHolder debtPositionsApisHolder;

  public PaymentOptionSearchClient(DebtPositionsApisHolder debtPositionsApisHolder) {
    this.debtPositionsApisHolder = debtPositionsApisHolder;
  }

  public List<PaymentOption> getPaymentOptions(Long debtPositionId, String accessToken){
    CollectionModelPaymentOption collectionModelPaymentOption = debtPositionsApisHolder.getPaymentOptionSearchControllerApi(accessToken)
      .crudPaymentOptionsFindPayablePaymentOptionsByDebtPositionId(debtPositionId);
    return collectionModelPaymentOption != null && collectionModelPaymentOption.getEmbedded() != null ?
      collectionModelPaymentOption.getEmbedded().getPaymentOptions() : Collections.emptyList();
  }
}
