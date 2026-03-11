package it.gov.pagopa.pu.citizen.connector.cie.client;

import it.gov.pagopa.pu.cie.controller.generated.DebtPositionCieApi;
import it.gov.pagopa.pu.cie.dto.generated.DebtPositionCieRequestDTO;
import it.gov.pagopa.pu.citizen.connector.cie.config.CieApisHolder;
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

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CieDebtPositionClientTest {

  @Mock
  private CieApisHolder cieApisHolderMock;
  @Mock
  private DebtPositionCieApi debtPositionCieApiMock;

  private CieDebtPositionClient cieDebtPositionClient;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @BeforeEach
  void setUp() {
    cieDebtPositionClient = new CieDebtPositionClient(cieApisHolderMock);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      cieApisHolderMock,
      debtPositionCieApiMock
    );
  }

  @Test
  void whenCreateDebtPositionThenInvokeWithAccessToken() {
    DebtPositionCieRequestDTO debtPositionCieRequestDTO = podamFactory.manufacturePojo(DebtPositionCieRequestDTO.class);
    String accessToken = "ACCESSTOKEN";
    DebtPositionDTO expectedResult = podamFactory.manufacturePojo(DebtPositionDTO.class);

    when(cieApisHolderMock.getDebtPositionCieApi(accessToken))
      .thenReturn(debtPositionCieApiMock);
    when(debtPositionCieApiMock.createDebtPositionCie(debtPositionCieRequestDTO))
      .thenReturn(expectedResult);

    DebtPositionDTO result = cieDebtPositionClient.createDebtPositionCie(debtPositionCieRequestDTO, accessToken);

    Assertions.assertSame(expectedResult, result);
  }
}
