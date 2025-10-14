package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;

public interface DebtPositionService {
  DebtPositionDTO createDebtPosition(DebtPositionDTO debtPositionDTO, Boolean massive, String accessToken);
}
