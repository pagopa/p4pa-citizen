package it.gov.pagopa.pu.citizen.controller;

import it.gov.pagopa.pu.citizen.controller.generated.DebtPositionTypeOrgApi;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDetailsDTO;
import it.gov.pagopa.pu.citizen.security.SecurityUtils;
import it.gov.pagopa.pu.citizen.service.debtpositiontypeorg.DebtPositionTypeOrgRetrieverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class DebtPositionTypeOrgController implements DebtPositionTypeOrgApi {

  private final DebtPositionTypeOrgRetrieverService debtPositionTypeOrgRetrieverService;

  public DebtPositionTypeOrgController(DebtPositionTypeOrgRetrieverService debtPositionTypeOrgRetrieverService) {
    this.debtPositionTypeOrgRetrieverService = debtPositionTypeOrgRetrieverService;
  }

  @Override
  public ResponseEntity<List<DebtPositionTypeOrgsWithSpontaneousDTO>> getDebtPositionTypeOrgsWithSpontaneous(Long organizationId) {
    log.info("Requested getDebtPositionTypeOrgsWithSpontaneous on organizationId {}", organizationId);
    return ResponseEntity.ok(debtPositionTypeOrgRetrieverService.getDebtPositionTypeOrgsWithSpontaneous(organizationId, SecurityUtils.getAccessToken()));
  }

  @Override
  public ResponseEntity<DebtPositionTypeOrgsWithSpontaneousDetailsDTO> getDebtPositionTypeOrgsWithSpontaneousDetail(Long brokerId, Long organizationId, Long debtPositionTypeOrgId) {
    log.info("Requested getDebtPositionTypeOrgsWithSpontaneousDetail on brokerId {} and organizationId {} with debtPositionTypeOrgId {}", brokerId, organizationId, debtPositionTypeOrgId);
    return ResponseEntity.ok(debtPositionTypeOrgRetrieverService.getDebtPositionTypeOrgsWithSpontaneousDetailsDTO(brokerId, organizationId, debtPositionTypeOrgId, SecurityUtils.getAccessToken()));
  }
}
