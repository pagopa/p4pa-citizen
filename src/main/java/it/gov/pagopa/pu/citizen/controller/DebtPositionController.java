package it.gov.pagopa.pu.citizen.controller;

import it.gov.pagopa.pu.citizen.controller.generated.DebtPositionApi;
import it.gov.pagopa.pu.citizen.dto.DebtPositionDTOEnriched;
import it.gov.pagopa.pu.citizen.dto.FileResourceDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionResponseDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtorUnpaidDebtPositionOverviewDTO;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorDebtPositionDTO;
import it.gov.pagopa.pu.citizen.security.SecurityUtils;
import it.gov.pagopa.pu.citizen.service.debtposition.DebtPositionFacadeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
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
    log.info("Requested createSpontaneousDebtPosition having brokerId {} and organizationId {} ", brokerId, debtPositionRequestDTO.getOrganizationId());
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
  public ResponseEntity<DebtPositionDTOEnriched> getDebtPositionDetail(Long brokerId, Long debtPositionId, String xFiscalCode) {
    log.info("User requested getDebtPositionDetail having brokerId {} and debtPositionId {} ", brokerId, debtPositionId);
    return ResponseEntity.ofNullable(debtPositionFacadeService.getDebtPositionDetail(brokerId, xFiscalCode, debtPositionId, SecurityUtils.getAccessToken()));
  }

  @Override
  public ResponseEntity<Resource> getPaymentNotice(String fiscalCode, Long brokerId, Long organizationId, String nav) {
    log.info("User requested getPaymentNotice having brokerId {}, organizationId {} and nav {}", brokerId, organizationId, nav);

    FileResourceDTO fileResourceDTO = debtPositionFacadeService.getPaymentNotice(
      fiscalCode, brokerId, organizationId, nav, SecurityUtils.getAccessToken());
    if(fileResourceDTO!=null) {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentDisposition(ContentDisposition.attachment()
        .filename(fileResourceDTO.getFileName())
        .build());

      return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_PDF)
        .headers(headers)
        .body(fileResourceDTO.getResource());
    } else {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
  }

  @Override
  public ResponseEntity<PagedDebtorDebtPositionDTO> getPagedUnpaidDebtPositions(String xFiscalCode, Long brokerId, String orgName, String orgFiscalCode, Pageable pageable) {
    log.info("User requested getPagedUnpaidDebtPositions having brokerId {} orgName {} and orgFiscalCode {}", brokerId, orgName, orgFiscalCode);
    return ResponseEntity.ok(debtPositionFacadeService.getPagedUnpaidDebtPositions(xFiscalCode, brokerId, orgName, orgFiscalCode, pageable, SecurityUtils.getAccessToken()));
  }

  @Override
  public ResponseEntity<DebtorUnpaidDebtPositionOverviewDTO> getDebtorUnpaidDebtPositionOverview(Long brokerId, Long debtPositionId, String xFiscalCode, Long organizationId) {
    log.info("User requested getDebtorUnpaidDebtPositionOverview having brokerId {} debtPositionId {} and organizationId {}", brokerId, debtPositionId, organizationId);
    return ResponseEntity.ofNullable(debtPositionFacadeService.getDebtorUnpaidDebtPositionOverview(brokerId, debtPositionId, xFiscalCode, organizationId,  SecurityUtils.getAccessToken()));
  }
}
