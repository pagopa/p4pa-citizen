package it.gov.pagopa.pu.citizen.service.receipt;

import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorReceiptsDTO;
import org.springframework.data.domain.Pageable;

public interface ReceiptFacadeService {
  PagedDebtorReceiptsDTO getPagedDebtorReceipts(Long brokerId, String orgName, String debtorFiscalCode, String accessToken, Pageable pageable);
}
