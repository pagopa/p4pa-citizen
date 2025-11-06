package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptDetailDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Service
public class ReceiptClient {

  private final DebtPositionsApisHolder debtPositionsApisHolder;

  public ReceiptClient(DebtPositionsApisHolder debtPositionsApisHolder) {
    this.debtPositionsApisHolder = debtPositionsApisHolder;
  }

  public ReceiptDetailDTO getReceiptDetail(Long receiptId, Long organizationId, String accessToken) {
    try {
      return debtPositionsApisHolder.getReceiptApi(accessToken)
        .getReceiptDetail(receiptId,organizationId,null);
    } catch (HttpClientErrorException.NotFound e) {
      log.warn("ReceiptDetail with receiptId {} and organizationId {} not found", receiptId, organizationId);
      return null;
    }
  }
}
