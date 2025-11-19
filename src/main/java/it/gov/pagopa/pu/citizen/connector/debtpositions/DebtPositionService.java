package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.debtpositions.dto.generated.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DebtPositionService {
  DebtPositionDTO createDebtPosition(DebtPositionDTO debtPositionDTO, Boolean massive, String accessToken);
  DebtPositionDTO getDebtPosition(Long debtPositionId, String accessToken);
  DebtPositionDTO getDebtPositionByInstallmentId(Long installmentId, String accessToken);
  List<DebtPositionDTO> getDebtPositionsByOrganizationIdAndIuv(Long organizationId, String iuv, List<DebtPositionOrigin> debtPositionOrigins, String accessToken);
  List<DebtPositionDTO> getDebtPositionsByOrganizationIdAndIud(Long organizationId, String iud, List<DebtPositionOrigin> debtPositionOrigins, String accessToken);
  PagedModelDebtPositionView getPagedModelDebtPositionView(List<Long> organizationIds, String debtorFiscalCode, String accessToken, Pageable pageable);
  List<PaymentOption> getPaymentOptions(Long debtPositionId, String accessToken);
  List<InstallmentNoPII> getInstallments(Long debtPositionId, String installmentStatuses, String accessToken);
}
