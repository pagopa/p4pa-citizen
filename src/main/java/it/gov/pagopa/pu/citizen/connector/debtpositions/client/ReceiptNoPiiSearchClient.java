package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import it.gov.pagopa.pu.debtpositions.dto.generated.CollectionModelReceiptNoPII;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptNoPII;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

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

  public List<ReceiptNoPII> getReceiptNoPiiList(Set<Long> receiptIds, String accessToken){
    CollectionModelReceiptNoPII collectionModelReceiptNoPII = debtPositionsApisHolder.getReceiptNoPiiSearchControllerApi(accessToken)
      .crudReceiptsFindAllByReceiptIdIn(receiptIds);
    return collectionModelReceiptNoPII != null && collectionModelReceiptNoPII.getEmbedded() != null ?
      collectionModelReceiptNoPII.getEmbedded().getReceiptNoPIIs() : Collections.emptyList();
  }
}
