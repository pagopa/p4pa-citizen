package it.gov.pagopa.pu.citizen.controller;

import it.gov.pagopa.pu.citizen.controller.generated.InstallmentsApi;
import it.gov.pagopa.pu.citizen.dto.generated.DebtorUnpaidDebtPositionInstallmentsDTO;
import it.gov.pagopa.pu.citizen.security.SecurityUtils;
import it.gov.pagopa.pu.citizen.service.installment.InstallmentRetrieverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class InstallmentsController implements InstallmentsApi {

  private final InstallmentRetrieverService installmentRetrieverService;

  public InstallmentsController(InstallmentRetrieverService installmentRetrieverService) {
    this.installmentRetrieverService = installmentRetrieverService;
  }

  @Override
  public ResponseEntity<List<DebtorUnpaidDebtPositionInstallmentsDTO>> getDebtorUnpaidDebtPositionInstallments(Long brokerId, Long debtPositionId, Long paymentOptionId, String xFiscalCode, Long organizationId) {
    log.info("User requested getDebtorUnpaidDebtPositionInstallments having debtPositionId {} and paymentOptionId {} with  brokerId {} organizationId {}", debtPositionId, paymentOptionId, brokerId, organizationId);
    return ResponseEntity.ok(installmentRetrieverService.getDebtorInstallmentNoPII(brokerId, debtPositionId,paymentOptionId, xFiscalCode, organizationId, SecurityUtils.getAccessToken()));
  }
}
