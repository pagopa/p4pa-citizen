package it.gov.pagopa.pu.citizen.service.debtposition;

import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionResponseDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import org.springframework.core.io.Resource;

public interface DebtPositionFacadeService {
  DebtPositionResponseDTO createSpontaneousDebtPosition(Long brokerId, DebtPositionRequestDTO debtPositionRequestDTO, String accessToken);
  Resource getDebtPositionNoticesZip(Long brokerId, String fiscalCode, Long debtPositionId, String accessToken);
  DebtPositionDTO getDebtPositionDetail(Long brokerId, String fiscalCode, Long debtPositionId, String accessToken);
}
