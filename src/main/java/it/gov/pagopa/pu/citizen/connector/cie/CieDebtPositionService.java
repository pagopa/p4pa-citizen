package it.gov.pagopa.pu.citizen.connector.cie;

import it.gov.pagopa.pu.cie.dto.generated.DebtPositionCieRequestDTO;
import it.gov.pagopa.pu.citizen.dto.FileResourceDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;

public interface CieDebtPositionService {
  DebtPositionDTO createDebtPositionCie(DebtPositionCieRequestDTO debtPositionCieRequestDTO, String orgIpaCode);
  FileResourceDTO generateNoticeCie(String nav, String debtorFiscalCode, String orgIpaCode);
}
