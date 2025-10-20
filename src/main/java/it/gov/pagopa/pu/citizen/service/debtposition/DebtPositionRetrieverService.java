package it.gov.pagopa.pu.citizen.service.debtposition;

import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionResponseDTO;
import org.springframework.core.io.Resource;

public interface DebtPositionRetrieverService {
  DebtPositionResponseDTO createSpontaneousDebtPosition(DebtPositionRequestDTO debtPositionRequestDTO, String accessToken);
  Resource getDebtPositionNoticesZip(String fiscalCode, Long debtPositionId, String accessToken);
}
