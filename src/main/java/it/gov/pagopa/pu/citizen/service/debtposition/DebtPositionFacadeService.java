package it.gov.pagopa.pu.citizen.service.debtposition;

import it.gov.pagopa.pu.citizen.dto.FileResourceDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionResponseDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtorUnpaidDebtPositionOverviewDTO;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorDebtPositionDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;

public interface DebtPositionFacadeService {
  DebtPositionResponseDTO createSpontaneousDebtPosition(Long brokerId, DebtPositionRequestDTO debtPositionRequestDTO, String accessToken);
  Resource getDebtPositionNoticesZip(Long brokerId, String fiscalCode, Long debtPositionId, String accessToken);
  DebtPositionDTO getDebtPositionDetail(Long brokerId, String fiscalCode, Long debtPositionId, String accessToken);
  FileResourceDTO getPaymentNotice(String fiscalCode, Long brokerId, Long organizationId, Long installmentId, String iuv, String iud, String accessToken);
  PagedDebtorDebtPositionDTO getPagedUnpaidDebtPositions(String xFiscalCode, Long brokerId, String orgName, String orgFiscalCode, Pageable pageable, String accessToken);
  DebtorUnpaidDebtPositionOverviewDTO getDebtorUnpaidDebtPositionOverview(Long brokerId, Long debtPositionId, String debtorFiscalCode, Long organizationId, String accessToken);
}
