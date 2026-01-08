package it.gov.pagopa.pu.citizen.controller;

import it.gov.pagopa.pu.citizen.controller.generated.DebtPositionTypeOrgApi;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDetailsDTO;
import it.gov.pagopa.pu.citizen.exception.InvalidParamException;
import it.gov.pagopa.pu.citizen.security.SecurityUtils;
import it.gov.pagopa.pu.citizen.service.debtpositiontypeorg.DebtPositionTypeOrgRetrieverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class DebtPositionTypeOrgController implements DebtPositionTypeOrgApi {

  private final int pageMaxSize;
  private final DebtPositionTypeOrgRetrieverService debtPositionTypeOrgRetrieverService;

  public DebtPositionTypeOrgController(DebtPositionTypeOrgRetrieverService debtPositionTypeOrgRetrieverService,
                                       @Value("${rest.page.request-max-page-size}") int pageMaxSize) {
    this.debtPositionTypeOrgRetrieverService = debtPositionTypeOrgRetrieverService;
    this.pageMaxSize = pageMaxSize;
  }

  @Override
  public ResponseEntity<List<DebtPositionTypeOrgsWithSpontaneousDTO>> getDebtPositionTypeOrgsWithSpontaneous(Long brokerId, Long organizationId) {
    log.info("Requested getDebtPositionTypeOrgsWithSpontaneous on brokerId {} and organizationId {}", brokerId, organizationId);
    return ResponseEntity.ok(debtPositionTypeOrgRetrieverService.getDebtPositionTypeOrgsWithSpontaneous(brokerId, organizationId, SecurityUtils.getAccessToken()));
  }

  @Override
  public ResponseEntity<DebtPositionTypeOrgsWithSpontaneousDetailsDTO> getDebtPositionTypeOrgsWithSpontaneousDetail(Long brokerId, Long organizationId, Long debtPositionTypeOrgId) {
    log.info("Requested getDebtPositionTypeOrgsWithSpontaneousDetail on brokerId {} and organizationId {} with debtPositionTypeOrgId {}", brokerId, organizationId, debtPositionTypeOrgId);
    return ResponseEntity.ok(debtPositionTypeOrgRetrieverService.getDebtPositionTypeOrgsWithSpontaneousDetailsDTO(brokerId, organizationId, debtPositionTypeOrgId, SecurityUtils.getAccessToken()));
  }

  @Override
  public ResponseEntity<List<DebtPositionTypeOrgsWithSpontaneousDTO>> getMostUsedSpontaneousDebtPositionTypeOrgsForCurrentYear(Long brokerId, Long organizationId, Pageable pageable) {
    log.info("Requested getMostUsedSpontaneousDebtPositionTypeOrgsForCurrentYear on brokerId {} and organizationId {}", brokerId, organizationId);
    if (pageable != null && pageable.getPageSize() > pageMaxSize) {
      throw new InvalidParamException(
        "The size query parameter must not exceed " + pageMaxSize
      );
    }

    return ResponseEntity.ok(debtPositionTypeOrgRetrieverService.getMostUsedSpontaneousDebtPositionTypeOrgsForCurrentYear(brokerId, organizationId, pageable, SecurityUtils.getAccessToken()));
  }
}
