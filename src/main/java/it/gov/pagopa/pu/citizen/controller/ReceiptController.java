package it.gov.pagopa.pu.citizen.controller;

import it.gov.pagopa.pu.citizen.controller.generated.ReceiptApi;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorReceiptsDTO;
import it.gov.pagopa.pu.citizen.security.SecurityUtils;
import it.gov.pagopa.pu.citizen.service.receipt.ReceiptFacadeService;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptDetailDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ReceiptController implements ReceiptApi {

  private final ReceiptFacadeService receiptFacadeService;

  public ReceiptController(ReceiptFacadeService receiptFacadeService) {
    this.receiptFacadeService = receiptFacadeService;
  }

  @Override
  public ResponseEntity<PagedDebtorReceiptsDTO> getPagedDebtorReceipts(Long brokerId, String xFiscalCode, String orgName, Pageable pageable) {
    log.info("Requested getPagedDebtorReceipts on brokerId {} and orgName {}", brokerId, orgName);
    return ResponseEntity.ok(receiptFacadeService.getPagedDebtorReceipts(brokerId, orgName, xFiscalCode, SecurityUtils.getAccessToken(), pageable));
  }

  @Override
  public ResponseEntity<ReceiptDetailDTO> getReceiptDetail(String fiscalCode, Long brokerId, Long organizationId, Long receiptId) {
    log.info("User requested getReceiptDetail having brokerId {} organizationId {} and receiptId {} ", brokerId, organizationId, receiptId);
    return ResponseEntity.ofNullable(receiptFacadeService.getReceiptDetail(fiscalCode, brokerId, organizationId, receiptId, SecurityUtils.getAccessToken()));
  }
}
