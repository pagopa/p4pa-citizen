package it.gov.pagopa.pu.citizen.controller;

import it.gov.pagopa.pu.citizen.controller.generated.DebtPositionApi;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionResponseDTO;
import it.gov.pagopa.pu.citizen.security.SecurityUtils;
import it.gov.pagopa.pu.citizen.service.debtposition.DebtPositionRetrieverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class DebtPositionController implements DebtPositionApi {

  private final DebtPositionRetrieverService debtPositionRetrieverService;

  public DebtPositionController(DebtPositionRetrieverService debtPositionRetrieverService) {
    this.debtPositionRetrieverService = debtPositionRetrieverService;
  }

  @Override
  public ResponseEntity<DebtPositionResponseDTO> createSpontaneousDebtPosition(DebtPositionRequestDTO debtPositionRequestDTO) {
    log.info("Requested createSpontaneousDebtPosition having organizationId {} ", debtPositionRequestDTO.getOrganizationId());
    return ResponseEntity.ok(debtPositionRetrieverService.createSpontaneousDebtPosition(debtPositionRequestDTO, SecurityUtils.getAccessToken()));
  }

  @Override
  public ResponseEntity<Resource> getUnpaidPaymentNoticeZip(String fiscalCode, Long debtPositionId) {
    log.info("User requested getUnpaidPaymentNoticeZip having debtPositionId {} ", debtPositionId);

    Resource debtPositionPaymentNoticesZipped = debtPositionRetrieverService.getDebtPositionNoticesZip(fiscalCode, debtPositionId, SecurityUtils.getAccessToken());
    if (debtPositionPaymentNoticesZipped != null){
      HttpHeaders headers = new HttpHeaders();
      headers.setContentDisposition(ContentDisposition.attachment()
          .filename(debtPositionId + "_NOTICES_PDF.zip")
          .build());

      return ResponseEntity.ok()
          .headers(headers)
          .contentType(MediaType.APPLICATION_OCTET_STREAM)
          .body(debtPositionPaymentNoticesZipped);
    } else {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
  }
}
