package it.gov.pagopa.pu.citizen.connector.cie.client;

import it.gov.pagopa.pu.cie.dto.generated.DebtPositionCieRequestDTO;
import it.gov.pagopa.pu.citizen.connector.cie.config.CieApisHolder;
import it.gov.pagopa.pu.citizen.dto.FileResourceDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Service
public class CieDebtPositionClient {

  private final CieApisHolder cieApisHolder;

  public CieDebtPositionClient(CieApisHolder cieApisHolder) {
    this.cieApisHolder = cieApisHolder;
  }

  public DebtPositionDTO createDebtPositionCie(DebtPositionCieRequestDTO debtPositionCieRequestDTO, String accessToken) {
    return cieApisHolder.getDebtPositionCieApi(accessToken).createDebtPositionCie(debtPositionCieRequestDTO);
  }

  public FileResourceDTO generateNoticeCie(String nav, String debtorFiscalCode, String accessToken) {
    try {
      ResponseEntity<Resource> resourceResponseEntity = cieApisHolder.getDebtPositionCieApi(accessToken)
        .generateNoticeCieWithHttpInfo(nav, debtorFiscalCode);
      return FileResourceDTO.builder()
        .resource(resourceResponseEntity.getBody())
        .fileName(resourceResponseEntity.getHeaders().getContentDisposition().getFilename())
        .build();
    } catch (HttpClientErrorException.NotFound e) {
      log.warn("Payment notice with nav {} not found", nav);
      return null;
    }
  }
}
