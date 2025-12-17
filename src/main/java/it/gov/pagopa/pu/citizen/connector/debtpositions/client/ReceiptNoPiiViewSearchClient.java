package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import it.gov.pagopa.pu.citizen.dto.DebtorReceiptsFiltersDTO;
import it.gov.pagopa.pu.citizen.utils.PageUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.PagedModelReceiptNoPIIView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class ReceiptNoPiiViewSearchClient {

  private final DebtPositionsApisHolder debtPositionsApisHolder;

  public ReceiptNoPiiViewSearchClient(DebtPositionsApisHolder debtPositionsApisHolder) {
    this.debtPositionsApisHolder = debtPositionsApisHolder;
  }

  public PagedModelReceiptNoPIIView getPagedModelReceiptNoPIIView(DebtorReceiptsFiltersDTO debtorReceiptsFiltersDTO, Pageable pageable, String accessToken){
    return debtPositionsApisHolder.getReceiptNoPiiViewSearchControllerApi(accessToken)
      .crudReceiptNoPiiViewGetPagedPrimaryReceiptByFilters(
        debtorReceiptsFiltersDTO.getDebtorFiscalCode(),
        debtorReceiptsFiltersDTO.getOrganizationsFiscalCode(),
        debtorReceiptsFiltersDTO.getReceiptOrigins(),
        debtorReceiptsFiltersDTO.getNoticeNumberOrIuv(),
        debtorReceiptsFiltersDTO.getPaymentDateTimeFrom(),
        debtorReceiptsFiltersDTO.getPaymentDateTimeTo(),
        PageUtils.getPageNumber(pageable),
        PageUtils.getPageSize(pageable),
        PageUtils.getSortList(pageable));
  }
}
