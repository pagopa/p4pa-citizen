package it.gov.pagopa.pu.citizen.service.debtposition;

import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionResponseDTO;

public interface DebtPositionRetrieverService {
  DebtPositionResponseDTO createSpontaneousDebtPosition(DebtPositionRequestDTO debtPositionRequestDTO, String accessToken);
}
