package it.gov.pagopa.pu.citizen.service.receipt;

import it.gov.pagopa.pu.citizen.dto.DebtorReceiptsFiltersDTO;
import it.gov.pagopa.pu.citizen.dto.FileResourceDTO;
import it.gov.pagopa.pu.citizen.dto.ReceiptDetailExtendedDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtorReceiptDTO;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorReceiptsDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReceiptFacadeService {
  PagedDebtorReceiptsDTO getPagedDebtorReceipts(Long brokerId, String orgName, DebtorReceiptsFiltersDTO debtorReceiptsFiltersDTO, String accessToken, Pageable pageable);
  ReceiptDetailExtendedDTO getReceiptDetail(String fiscalCode, Long brokerId, Long organizationId, Long receiptId, String accessToken);
  FileResourceDTO getReceiptPdf(String fiscalCode, Long brokerId, Long organizationId, Long receiptId, String accessToken);
  List<DebtorReceiptDTO> getDebtorReceipts(String debtorFiscalCode, Long brokerId, Long organizationId, Long debtPositionId, Long paymentOptionId, String accessToken);
}
