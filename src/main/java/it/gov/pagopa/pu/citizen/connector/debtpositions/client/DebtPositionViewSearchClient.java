package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import it.gov.pagopa.pu.citizen.utils.PageUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.PagedModelDebtPositionView;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DebtPositionViewSearchClient {

  private final DebtPositionsApisHolder debtPositionsApisHolder;

  public DebtPositionViewSearchClient(DebtPositionsApisHolder debtPositionsApisHolder) {
    this.debtPositionsApisHolder = debtPositionsApisHolder;
  }

  public PagedModelDebtPositionView getPagedModelDebtPositionView(List<Long> organizationIds, String debtorFiscalCode, String accessToken, Pageable pageable){
    return debtPositionsApisHolder.getDebtPositionViewSearchControllerApi(accessToken)
      .crudDebtPositionsViewFindPagedPrimaryDebtPositionViewByFilters(
        organizationIds,
        debtorFiscalCode,
        PageUtils.getPageNumber(pageable),
        PageUtils.getPageSize(pageable),
        PageUtils.getSortList(pageable)
        );
  }
}
