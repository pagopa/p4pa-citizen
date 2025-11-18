package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.citizen.dto.FileResourceDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.PagedModelReceiptNoPIIView;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptDetailDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptOriginType;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReceiptService {
  PagedModelReceiptNoPIIView getPagedModelReceiptNoPIIView(String debtorFiscalCode, List<String> organizationsFiscalCode, List<ReceiptOriginType> receiptOrigins, Pageable pageable, String accessToken);
  ReceiptDetailDTO getReceiptDetail(Long receiptId, Long organizationId, String accessToken);
  boolean isReceiptDebtorValid(Long receiptId, Long organizationId, String debtorFiscalCode, String accessToken);
  FileResourceDTO getReceiptPdf(Long receiptId, Long organizationId, String accessToken);
}
