package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentDebtorDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentNoPII;

import java.util.List;

public interface InstallmentService {
  List<InstallmentDebtorDTO> getInstallmentByIuvOrNav(String iuvOrNav, String debtorFiscalCode, Long organizationId, String accessToken);
  List<InstallmentNoPII> getDebtorInstallmentNoPII(String accessToken, Long debtPositionId, Long paymentOptionId, String debtorFiscalCode, Long organizationId);
}
