package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import it.gov.pagopa.pu.debtpositions.dto.generated.CollectionModelInstallmentNoPII;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentNoPII;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class InstallmentNoPIISearchClient {

  private final DebtPositionsApisHolder debtPositionsApisHolder;

  public InstallmentNoPIISearchClient(DebtPositionsApisHolder debtPositionsApisHolder) {
    this.debtPositionsApisHolder = debtPositionsApisHolder;
  }

  public List<InstallmentNoPII> getDebtorInstallmentNoPII(String accessToken, Long debtPositionId, Long paymentOptionId, String debtorFiscalCode, Long organizationId){
    CollectionModelInstallmentNoPII collectionModelInstallmentNoPII = debtPositionsApisHolder.getInstallmentNoPiiSearchControllerApi(accessToken)
      .crudInstallmentsFindDebtorUnpaidOrPaidByDebtPositionIdAndPaymentOptionId(debtPositionId, paymentOptionId, debtorFiscalCode, organizationId);
    return collectionModelInstallmentNoPII != null && collectionModelInstallmentNoPII.getEmbedded() != null ?
      collectionModelInstallmentNoPII.getEmbedded().getInstallmentNoPIIs() :
      Collections.emptyList();
  }
}
