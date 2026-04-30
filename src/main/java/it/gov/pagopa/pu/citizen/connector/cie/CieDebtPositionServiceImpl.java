package it.gov.pagopa.pu.citizen.connector.cie;

import it.gov.pagopa.pu.cie.dto.generated.DebtPositionCieRequestDTO;
import it.gov.pagopa.pu.citizen.connector.auth.AuthnService;
import it.gov.pagopa.pu.citizen.connector.cie.client.CieDebtPositionClient;
import it.gov.pagopa.pu.citizen.dto.FileResourceDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import org.springframework.stereotype.Service;

@Service
public class CieDebtPositionServiceImpl implements CieDebtPositionService {
  private final CieDebtPositionClient cieDebtPositionClient;
  private final AuthnService authnService;

  public CieDebtPositionServiceImpl(CieDebtPositionClient cieDebtPositionClient, AuthnService authnService) {
    this.cieDebtPositionClient = cieDebtPositionClient;
    this.authnService = authnService;
  }

  @Override
  public DebtPositionDTO createDebtPositionCie(DebtPositionCieRequestDTO debtPositionCieRequestDTO, String orgIpaCode) {
    return cieDebtPositionClient.createDebtPositionCie(debtPositionCieRequestDTO, authnService.getAccessToken(orgIpaCode));
  }

  @Override
  public FileResourceDTO generateNoticeCie(String nav, String debtorFiscalCode, String orgIpaCode) {
    return cieDebtPositionClient.generateNoticeCie(nav, debtorFiscalCode, authnService.getAccessToken(orgIpaCode));
  }

}
