package it.gov.pagopa.pu.citizen.controller;

import it.gov.pagopa.pu.citizen.controller.generated.DebtPositionApi;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionResponseDTO;
import it.gov.pagopa.pu.citizen.security.SecurityUtils;
import it.gov.pagopa.pu.citizen.service.debtposition.DebtPositionFacadeService;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
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

  private final DebtPositionFacadeService debtPositionFacadeService;

  public DebtPositionController(DebtPositionFacadeService debtPositionFacadeService) {
    this.debtPositionFacadeService = debtPositionFacadeService;
  }

  @Override
  public ResponseEntity<DebtPositionResponseDTO> createSpontaneousDebtPosition(Long brokerId, DebtPositionRequestDTO debtPositionRequestDTO) {
    log.info("Requested createSpontaneousDebtPosition having brokerId{} and organizationId {} ", brokerId, debtPositionRequestDTO.getOrganizationId());
    return ResponseEntity.ok(debtPositionFacadeService.createSpontaneousDebtPosition(brokerId,debtPositionRequestDTO, SecurityUtils.getAccessToken()));
  }

  @Override
  public ResponseEntity<Resource> getUnpaidPaymentNoticeZip(Long brokerId, String fiscalCode, Long debtPositionId) {
    log.info("User requested getUnpaidPaymentNoticeZip having brokerId {} and debtPositionId {} ", brokerId, debtPositionId);

    Resource debtPositionPaymentNoticesZipped = debtPositionFacadeService.getDebtPositionNoticesZip(brokerId, fiscalCode, debtPositionId, SecurityUtils.getAccessToken());
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

  @Override
  public ResponseEntity<DebtPositionDTO> getDebtPositionDetail(Long brokerId, Long debtPositionId, String xFiscalCode) {
    log.info("User requested getDebtPositionDetail having brokerId {} and debtPositionId {} ", brokerId, debtPositionId);
    return ResponseEntity.ofNullable(debtPositionFacadeService.getDebtPositionDetail(brokerId, xFiscalCode, debtPositionId, SecurityUtils.getAccessToken()));
  }
}
