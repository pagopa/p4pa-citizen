package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionOrigin;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtorDebtPositionDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.PagedDebtorUnpaidDebtPositionDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DebtPositionService {
  DebtPositionDTO createDebtPosition(DebtPositionDTO debtPositionDTO, Boolean massive, String accessToken);
  DebtPositionDTO getDebtPosition(Long debtPositionId, String accessToken);
  DebtPositionDTO getDebtPositionByInstallmentId(Long installmentId, String accessToken);
  List<DebtPositionDTO> getDebtPositionsByOrganizationIdAndIuv(Long organizationId, String iuv, List<DebtPositionOrigin> debtPositionOrigins, String accessToken);
  List<DebtPositionDTO> getDebtPositionsByOrganizationIdAndIud(Long organizationId, String iud, List<DebtPositionOrigin> debtPositionOrigins, String accessToken);
  PagedDebtorUnpaidDebtPositionDTO getPagedDebtorUnpaidDebtPosition(String debtorFiscalCode, List<Long> organizationIds, Pageable pageable, String accessToken);
  DebtorDebtPositionDTO getDebtorDebtPositionOverview(Long debtPositionId, String debtorFiscalCode, Long organizationId, String accessToken);
}
