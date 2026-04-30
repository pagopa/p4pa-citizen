package it.gov.pagopa.pu.citizen.service.debtposition.cie;

import it.gov.pagopa.pu.citizen.dto.FileResourceDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionResponseDTO;

public interface CieDebtPositionFacadeService {
  DebtPositionResponseDTO createSpontaneousDebtPosition(DebtPositionRequestDTO debtPositionRequestDTO, String accessToken);

  FileResourceDTO generateNoticeCie(String nav, String fiscalCode, String accessToken);
}
