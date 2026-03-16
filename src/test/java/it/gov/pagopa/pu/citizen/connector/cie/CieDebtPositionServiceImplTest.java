package it.gov.pagopa.pu.citizen.connector.cie;

import it.gov.pagopa.pu.cie.dto.generated.DebtPositionCieRequestDTO;
import it.gov.pagopa.pu.citizen.connector.auth.AuthnService;
import it.gov.pagopa.pu.citizen.connector.cie.client.CieDebtPositionClient;
import it.gov.pagopa.pu.citizen.dto.FileResourceDTO;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

@ExtendWith(MockitoExtension.class)
class CieDebtPositionServiceImplTest {

  @Mock
  private CieDebtPositionClient cieDebtPositionClientMock;
  @Mock
  private AuthnService authnServiceMock;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  CieDebtPositionService cieDebtPositionService;

  @BeforeEach
  void setUp() {
    cieDebtPositionService = new CieDebtPositionServiceImpl(cieDebtPositionClientMock,authnServiceMock);
  }

  @AfterEach
  void mockitoVerify(){
    Mockito.verifyNoMoreInteractions(cieDebtPositionClientMock);
  }

  @Test
  void whenCreateDebtPositionCieThenInvokeClient() {
    String accessToken = "ACCESS_TOKEN";
    String cieOrgIpaCode = "cieOrgIpaCode";
    DebtPositionCieRequestDTO debtPositionCieRequestDTO = podamFactory.manufacturePojo(DebtPositionCieRequestDTO.class);
    DebtPositionDTO expectedResult = podamFactory.manufacturePojo(DebtPositionDTO.class);

    Mockito.when(authnServiceMock.getAccessToken(cieOrgIpaCode)).thenReturn(accessToken);
    Mockito.when(cieDebtPositionClientMock.createDebtPositionCie(debtPositionCieRequestDTO,accessToken)).thenReturn(expectedResult);

    DebtPositionDTO result = cieDebtPositionService.createDebtPositionCie(debtPositionCieRequestDTO, cieOrgIpaCode);

    Assertions.assertNotNull(result);
    Assertions.assertSame(expectedResult, result);
  }

  @Test
  void whenGenerateNoticeCieThenInvokeClient() {
    String nav = "nav";
    String debtorFiscalCode = "fiscalCode";
    String accessToken = "ACCESS_TOKEN";
    String cieOrgIpaCode = "cieOrgIpaCode";
    FileResourceDTO expectedResult = podamFactory.manufacturePojo(FileResourceDTO.class);

    Mockito.when(authnServiceMock.getAccessToken(cieOrgIpaCode)).thenReturn(accessToken);
    Mockito.when(cieDebtPositionClientMock.generateNoticeCie(nav, debtorFiscalCode, accessToken)).thenReturn(expectedResult);

    FileResourceDTO result = cieDebtPositionService.generateNoticeCie(nav, debtorFiscalCode, cieOrgIpaCode);

    Assertions.assertNotNull(result);
    Assertions.assertSame(expectedResult, result);
  }
}
