package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.citizen.connector.debtpositions.client.DebtPositionClient;
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
class DebtPositionServiceImplTest {

  @Mock
  private DebtPositionClient debtPositionClientMock;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  DebtPositionService debtPositionService;

  @BeforeEach
  void setUp() {
    debtPositionService = new DebtPositionServiceImpl(debtPositionClientMock);
  }

  @AfterEach
  void mockitoVerify(){
    Mockito.verifyNoMoreInteractions(debtPositionClientMock);
  }

  @Test
  void givenDebtPositionDTOAndMassiveWhenCreateDebtPositionThenReturnDebtPositionDTO() {
    //given
    String accessToken = "ACCESS_TOKEN";
    DebtPositionDTO debtPositionDTO = podamFactory.manufacturePojo(DebtPositionDTO.class);

    Mockito.when(debtPositionClientMock.createDebtPosition(debtPositionDTO, false, accessToken)).thenReturn(debtPositionDTO);
    //when
    DebtPositionDTO result = debtPositionService.createDebtPosition(debtPositionDTO, false, accessToken);
    //then
    Assertions.assertNotNull(result);
    Assertions.assertSame(debtPositionDTO, result);
  }
}
