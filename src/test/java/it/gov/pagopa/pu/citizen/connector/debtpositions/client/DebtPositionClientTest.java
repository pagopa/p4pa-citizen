package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.controller.generated.DebtPositionApi;
import it.gov.pagopa.pu.debtpositions.controller.generated.DebtPositionViewSearchControllerApi;
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
class DebtPositionClientTest {

  @Mock
  private DebtPositionsApisHolder debtPositionApisHolderMock;
  @Mock
  private DebtPositionViewSearchControllerApi debtPositionViewSearchControllerApiMock;
  @Mock
  private DebtPositionApi debtPositionApiMock;

  private DebtPositionClient debtPositionClient;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @BeforeEach
  void setUp() {
    debtPositionClient = new DebtPositionClient(debtPositionApisHolderMock);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      debtPositionApisHolderMock,
      debtPositionViewSearchControllerApiMock,
      debtPositionApiMock
    );
  }

  @Test
  void whenCreateDebtPositionThenInvokeWithAccessToken() {
    DebtPositionDTO debtPositionDTO = podamFactory.manufacturePojo(DebtPositionDTO.class);
    Boolean massive = true;
    String accessToken = "ACCESSTOKEN";
    DebtPositionDTO expectedResult = podamFactory.manufacturePojo(DebtPositionDTO.class);

    when(debtPositionApisHolderMock.getDebtPositionApi(accessToken))
      .thenReturn(debtPositionApiMock);
    when(debtPositionApiMock.createDebtPosition(debtPositionDTO, massive))
      .thenReturn(expectedResult);

    DebtPositionDTO result = debtPositionClient.createDebtPosition(debtPositionDTO, massive, accessToken);

    Assertions.assertSame(expectedResult, result);
  }
}
