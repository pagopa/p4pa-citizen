package it.gov.pagopa.pu.citizen.service.debtposition;

import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;

public interface DebtPositionRetrieverService {
  DebtPositionDTO createDebtPositionDTO(DebtPositionRequestDTO debtPositionRequestDTO, String accessToken);
}
