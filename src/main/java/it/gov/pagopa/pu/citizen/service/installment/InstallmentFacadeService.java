package it.gov.pagopa.pu.citizen.service.installment;

import it.gov.pagopa.pu.citizen.dto.InstallmentDebtorExtendedDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtorUnpaidDebtPositionInstallmentsDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentStatus;
import it.gov.pagopa.pu.debtpositions.dto.generated.PostalIbanVerifyResponse;

import java.util.List;
import java.util.function.Function;

public interface InstallmentFacadeService {
  List<InstallmentDebtorExtendedDTO> getInstallmentByIuvOrNav(Long brokerId, String iuvOrNav, String debtorFiscalCode, String orgFiscalCode, List<InstallmentStatus> statuses, String accessToken);
  List<DebtorUnpaidDebtPositionInstallmentsDTO> getDebtorInstallmentNoPII(Long brokerId, Long debtPositionId, Long paymentOptionId, String xFiscalCode, Long organizationId, String accessToken);
  <T> PostalIbanVerifyResponse extractPostalIbanVerifyResponse(List<T> installments, Function<T, Long> idExtractor, String accessToken);

}
