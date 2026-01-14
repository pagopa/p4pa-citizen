package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.citizen.dto.DebtorReceiptsFiltersDTO;
import it.gov.pagopa.pu.citizen.dto.FileResourceDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReceiptService {
  PagedModelReceiptNoPIIView getPagedModelReceiptNoPIIView(DebtorReceiptsFiltersDTO debtorReceiptsFiltersDTO, Pageable pageable, String accessToken);
  ReceiptDetailDTO getReceiptDetail(Long receiptId, Long organizationId, String accessToken);
  boolean isReceiptDebtorValid(Long receiptId, Long organizationId, String debtorFiscalCode, String accessToken);
  FileResourceDTO getReceiptPdf(Long receiptId, Long organizationId, String accessToken);
  List<ReceiptNoPIIView> getDebtorReceipts(String debtorFiscalCode, Long organizationId, Long debtPositionId, Long paymentOptionId, List<ReceiptOriginType> receiptOrigins, List<InstallmentStatus> installmentStatuses, String accessToken);
}
