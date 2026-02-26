package it.gov.pagopa.pu.citizen.service.debtposition.cie;

import it.gov.pagopa.pu.citizen.connector.auth.AuthnService;
import it.gov.pagopa.pu.citizen.connector.cie.CieDebtPositionService;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionResponseDTO;
import it.gov.pagopa.pu.citizen.mapper.DebtPositionResponseDTOMapper;
import it.gov.pagopa.pu.citizen.mapper.cie.DebtPositionCieRequestDTOMapper;
import it.gov.pagopa.pu.citizen.service.debtpositiontypeorg.DebtPositionTypeOrgRetrieverService;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CieDebtPositionFacadeServiceImpl implements CieDebtPositionFacadeService {
  private final CieDebtPositionService cieDebtPositionService;
  private final DebtPositionCieRequestDTOMapper debtPositionCieRequestDTOMapper;
  private final DebtPositionTypeOrgRetrieverService debtPositionTypeOrgRetrieverService;
  private final DebtPositionResponseDTOMapper debtPositionResponseDTOMapper;
  private final AuthnService authnService;
  private final String cieOrgIpaCode;

  public CieDebtPositionFacadeServiceImpl(CieDebtPositionService cieDebtPositionService, DebtPositionCieRequestDTOMapper debtPositionCieRequestDTOMapper,
                                          DebtPositionTypeOrgRetrieverService debtPositionTypeOrgRetrieverService, DebtPositionResponseDTOMapper debtPositionResponseDTOMapper,
                                          AuthnService authnService, @Value("${cie.organization.ipa-code}") String cieOrgIpaCode) {
    this.cieDebtPositionService = cieDebtPositionService;
    this.debtPositionCieRequestDTOMapper = debtPositionCieRequestDTOMapper;
    this.debtPositionTypeOrgRetrieverService = debtPositionTypeOrgRetrieverService;
    this.debtPositionResponseDTOMapper = debtPositionResponseDTOMapper;
    this.authnService = authnService;
    this.cieOrgIpaCode = cieOrgIpaCode;
  }

  @Override
  public DebtPositionResponseDTO createSpontaneousDebtPosition(DebtPositionRequestDTO debtPositionRequestDTO, String accessToken) {
    DebtPositionDTO debtPosition = cieDebtPositionService.createDebtPositionCie(
      debtPositionCieRequestDTOMapper.map(
        debtPositionRequestDTO,
        debtPositionTypeOrgRetrieverService.getDebtPositionTypeOrgCode(debtPositionRequestDTO.getDebtPositionTypeOrgId(), debtPositionRequestDTO.getOrganizationId(), accessToken)),
      authnService.getAccessToken(cieOrgIpaCode)
    );
    return debtPositionResponseDTOMapper.map(debtPosition,null,true);
  }
}
