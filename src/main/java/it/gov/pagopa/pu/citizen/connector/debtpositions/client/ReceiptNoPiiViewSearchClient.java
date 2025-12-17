package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import it.gov.pagopa.pu.citizen.utils.PageUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.PagedModelReceiptNoPIIView;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptOriginType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;


@Slf4j
@Service
public class ReceiptNoPiiViewSearchClient {

  private final DebtPositionsApisHolder debtPositionsApisHolder;

  public ReceiptNoPiiViewSearchClient(DebtPositionsApisHolder debtPositionsApisHolder) {
    this.debtPositionsApisHolder = debtPositionsApisHolder;
  }

  public PagedModelReceiptNoPIIView getPagedModelReceiptNoPIIView(String debtorFiscalCode, List<String> organizationsFiscalCode, List<ReceiptOriginType> receiptOrigins, String noticeNumberOrIuv, OffsetDateTime paymentDateTimeFrom, OffsetDateTime paymentDateTimeTo, Pageable pageable, String accessToken){
    return debtPositionsApisHolder.getReceiptNoPiiViewSearchControllerApi(accessToken)
      .crudReceiptNoPiiViewGetPagedPrimaryReceiptByFilters(
        debtorFiscalCode,
        organizationsFiscalCode,
        receiptOrigins,
        noticeNumberOrIuv,
        paymentDateTimeFrom,
        paymentDateTimeTo,
        PageUtils.getPageNumber(pageable),
        PageUtils.getPageSize(pageable),
        PageUtils.getSortList(pageable));
  }
}
