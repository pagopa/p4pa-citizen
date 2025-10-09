package it.gov.pagopa.pu.citizen.controller;

import it.gov.pagopa.pu.citizen.controller.generated.DebtPositionApi;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.security.SecurityUtils;
import it.gov.pagopa.pu.citizen.service.debtposition.DebtPositionRetrieverService;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import lombok.extern.slf4j.Slf4j;
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
  public ResponseEntity<DebtPositionDTO> createSpontaneousDebtPosition(DebtPositionRequestDTO debtPositionRequestDTO, Boolean massive) {
    log.info("Requested createSpontaneousDebtPositionType having organizationId {} ", debtPositionRequestDTO.getOrganizationId());
    return ResponseEntity.ok(debtPositionRetrieverService.createDebtPositionDTO(debtPositionRequestDTO, SecurityUtils.getAccessToken()));
  }
}
