package it.gov.pagopa.pu.citizen.service.installment;

import it.gov.pagopa.pu.citizen.dto.InstallmentDebtorExtendedDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtorUnpaidDebtPositionInstallmentsDTO;

import java.util.List;

public interface InstallmentFacadeService {
  List<InstallmentDebtorExtendedDTO> getInstallmentByIuvOrNav(Long brokerId, String iuvOrNav, String debtorFiscalCode, String orgFiscalCode, String accessToken);
  List<DebtorUnpaidDebtPositionInstallmentsDTO> getDebtorInstallmentNoPII(Long brokerId, Long debtPositionId, Long paymentOptionId, String xFiscalCode, Long organizationId, String accessToken);

}
