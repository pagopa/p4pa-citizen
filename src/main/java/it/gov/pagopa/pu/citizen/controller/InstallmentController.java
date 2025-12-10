package it.gov.pagopa.pu.citizen.controller;

import it.gov.pagopa.pu.citizen.controller.generated.InstallmentApi;
import it.gov.pagopa.pu.citizen.dto.InstallmentDebtorExtendedDTO;
import it.gov.pagopa.pu.citizen.security.SecurityUtils;
import it.gov.pagopa.pu.citizen.service.installment.InstallmentFacadeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class InstallmentController implements InstallmentApi {

  private final InstallmentFacadeService installmentFacadeService;

  public InstallmentController(InstallmentFacadeService installmentFacadeService) {
    this.installmentFacadeService = installmentFacadeService;
  }

  @Override
  public ResponseEntity<List<InstallmentDebtorExtendedDTO>> getInstallmentsByIuvOrNav(Long brokerId, String iuvOrNav, String debtorFiscalCode, String orgFiscalCode) {
    log.info("Requested getInstallmentsByIuvOrNav having brokerId {} and iuvOrNav {} ", brokerId, iuvOrNav);
    return ResponseEntity.ok(installmentFacadeService.getInstallmentByIuvOrNav(brokerId,iuvOrNav,debtorFiscalCode,orgFiscalCode,SecurityUtils.getAccessToken()));
  }
}
