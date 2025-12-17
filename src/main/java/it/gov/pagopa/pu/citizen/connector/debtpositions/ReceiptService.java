package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.citizen.dto.DebtorReceiptsFiltersDTO;
import it.gov.pagopa.pu.citizen.dto.FileResourceDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.PagedModelReceiptNoPIIView;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptDetailDTO;
import org.springframework.data.domain.Pageable;

public interface ReceiptService {
  PagedModelReceiptNoPIIView getPagedModelReceiptNoPIIView(DebtorReceiptsFiltersDTO debtorReceiptsFiltersDTO, Pageable pageable, String accessToken);
  ReceiptDetailDTO getReceiptDetail(Long receiptId, Long organizationId, String accessToken);
  boolean isReceiptDebtorValid(Long receiptId, Long organizationId, String debtorFiscalCode, String accessToken);
  FileResourceDTO getReceiptPdf(Long receiptId, Long organizationId, String accessToken);
}
