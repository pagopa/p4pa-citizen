package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import it.gov.pagopa.pu.citizen.dto.DebtorReceiptsFiltersDTO;
import it.gov.pagopa.pu.citizen.utils.PageUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;


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

  public List<ReceiptNoPIIView> getDebtorReceipts(String debtorFiscalCode, Long organizationId, Long debtPositionId, Long paymentOptionId, List<ReceiptOriginType> receiptOrigins, List<InstallmentStatus> installmentStatuses, String accessToken) {
    CollectionModelReceiptNoPIIView collectionModelReceiptNoPIIView = debtPositionsApisHolder.getReceiptNoPiiViewSearchControllerApi(accessToken)
      .crudReceiptNoPiiViewGetDebtorReceipts(
        debtorFiscalCode,
        organizationId,
        debtPositionId,
        paymentOptionId,
        receiptOrigins,
        installmentStatuses);
    return collectionModelReceiptNoPIIView != null && collectionModelReceiptNoPIIView.getEmbedded() != null?
      collectionModelReceiptNoPIIView.getEmbedded().getReceiptNoPIIViews() : Collections.emptyList();
  }
}
