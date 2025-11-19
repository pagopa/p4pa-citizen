package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import it.gov.pagopa.pu.debtpositions.dto.generated.CollectionModelInstallmentNoPII;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentNoPII;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class InstallmentNoPiiSearchClient {

  private final DebtPositionsApisHolder debtPositionsApisHolder;

  public InstallmentNoPiiSearchClient(DebtPositionsApisHolder debtPositionsApisHolder) {
    this.debtPositionsApisHolder = debtPositionsApisHolder;
  }

  public List<InstallmentNoPII> getInstallments(Long debtPositionId, String installmentStatuses, String accessToken){
    CollectionModelInstallmentNoPII collectionModelInstallmentNoPII = debtPositionsApisHolder.getInstallmentNoPiiSearchControllerApi(accessToken)
      .crudInstallmentsFindByDebtPositionIdAndStatuses(debtPositionId.toString(), installmentStatuses);
    return collectionModelInstallmentNoPII != null && collectionModelInstallmentNoPII.getEmbedded() != null?
      collectionModelInstallmentNoPII.getEmbedded().getInstallmentNoPIIs() : Collections.emptyList();
  }
}
