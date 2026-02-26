package it.gov.pagopa.pu.citizen.connector.cie;

import it.gov.pagopa.pu.cie.dto.generated.DebtPositionCieRequestDTO;
import it.gov.pagopa.pu.citizen.connector.auth.AuthnService;
import it.gov.pagopa.pu.citizen.connector.cie.client.CieDebtPositionClient;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CieDebtPositionServiceImpl implements CieDebtPositionService {
  private final CieDebtPositionClient cieDebtPositionClient;
  private final AuthnService authnService;
  private final String cieOrgIpaCode;

  public CieDebtPositionServiceImpl(CieDebtPositionClient cieDebtPositionClient, AuthnService authnService, @Value("${cie.organization.ipa-code}") String cieOrgIpaCode) {
    this.cieDebtPositionClient = cieDebtPositionClient;
    this.authnService = authnService;
    this.cieOrgIpaCode = cieOrgIpaCode;
  }

  @Override
  public DebtPositionDTO createDebtPositionCie(DebtPositionCieRequestDTO debtPositionCieRequestDTO) {
    return cieDebtPositionClient.createDebtPositionCie(debtPositionCieRequestDTO, authnService.getAccessToken(cieOrgIpaCode));
  }
}
