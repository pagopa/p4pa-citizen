package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptNoPII;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Service
public class ReceiptNoPiiEntityClient {

  private final DebtPositionsApisHolder debtPositionsApisHolder;

  public ReceiptNoPiiEntityClient(DebtPositionsApisHolder debtPositionsApisHolder) {
    this.debtPositionsApisHolder = debtPositionsApisHolder;
  }

  public ReceiptNoPII getReceiptNoPII(Long receiptId, String accessToken){
    try{
      return debtPositionsApisHolder.getReceiptNoPiiEntityControllerApi(accessToken)
        .crudGetReceiptnopii(String.valueOf(receiptId));
    } catch (HttpClientErrorException.NotFound e) {
      log.warn("ReceiptNoPII with receiptId {}", receiptId);
      return null;
    }
  }

}
