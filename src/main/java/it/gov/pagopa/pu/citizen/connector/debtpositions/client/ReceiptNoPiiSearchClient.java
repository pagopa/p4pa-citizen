package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import org.springframework.stereotype.Service;

@Service
public class ReceiptNoPiiSearchClient {

  private final DebtPositionsApisHolder debtPositionsApisHolder;

  public ReceiptNoPiiSearchClient(DebtPositionsApisHolder debtPositionsApisHolder) {
    this.debtPositionsApisHolder = debtPositionsApisHolder;
  }

  public Long validateReceiptDebtor(Long receiptId, Long organizationId, String debtorFiscalCode, String accessToken) {
    return debtPositionsApisHolder.getReceiptNoPiiSearchControllerApi(accessToken)
      .crudReceiptsValidateReceiptDebtor(receiptId,organizationId,debtorFiscalCode);
  }
}
