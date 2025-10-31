package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.citizen.connector.debtpositions.client.DebtPositionClient;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionOrigin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.List;

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

  @Test
  void whenGetDebtPositionThenInvokeClient() {
    //given
    String accessToken = "ACCESS_TOKEN";
    DebtPositionDTO expectedResult = podamFactory.manufacturePojo(DebtPositionDTO.class);

    Mockito.when(debtPositionClientMock.getDebtPosition(expectedResult.getDebtPositionId(), accessToken)).thenReturn(expectedResult);
    //when
    DebtPositionDTO result = debtPositionService.getDebtPosition(expectedResult.getDebtPositionId(), accessToken);
    //then
    Assertions.assertNotNull(result);
    Assertions.assertSame(expectedResult, result);
  }

  @Test
  void whenGetDebtPositionByInstallmentIdThenInvokeClient() {
    //given
    String accessToken = "ACCESS_TOKEN";
    Long installmentId = 1L;
    DebtPositionDTO expectedResult = podamFactory.manufacturePojo(DebtPositionDTO.class);

    Mockito.when(debtPositionClientMock.getDebtPositionByInstallmentId(installmentId,accessToken)).thenReturn(expectedResult);
    //when
    DebtPositionDTO result = debtPositionService.getDebtPositionByInstallmentId(installmentId,accessToken);
    //then
    Assertions.assertNotNull(result);
    Assertions.assertSame(expectedResult, result);
  }

  @Test
  void whenGetDebtPositionByOrganizationIdAndIuvThenInvokeClient() {
    //given
    String accessToken = "ACCESS_TOKEN";
    Long organizationId = 1L;
    String iuv = "iuv";
    List<DebtPositionOrigin> debtPositionOrigins = List.of(DebtPositionOrigin.ORDINARY);
    List<DebtPositionDTO> expectedResult = podamFactory.manufacturePojo(List.class,DebtPositionDTO.class);

    Mockito.when(debtPositionClientMock.getDebtPositionsByOrganizationIdAndIuv(organizationId, iuv, debtPositionOrigins, accessToken)).thenReturn(expectedResult);
    //when
    List<DebtPositionDTO> result = debtPositionService.getDebtPositionsByOrganizationIdAndIuv(organizationId, iuv, debtPositionOrigins,accessToken);
    //then
    Assertions.assertNotNull(result);
    Assertions.assertSame(expectedResult, result);
  }

  @Test
  void whenGetDebtPositionByOrganizationIdAndIudThenInvokeClient() {
    //given
    String accessToken = "ACCESS_TOKEN";
    Long organizationId = 1L;
    String iud = "iud";
    List<DebtPositionOrigin> debtPositionOrigins = List.of(DebtPositionOrigin.ORDINARY);
    List<DebtPositionDTO> expectedResult = podamFactory.manufacturePojo(List.class,DebtPositionDTO.class);

    Mockito.when(debtPositionClientMock.getDebtPositionsByOrganizationIdAndIud(organizationId, iud, debtPositionOrigins, accessToken)).thenReturn(expectedResult);
    //when
    List<DebtPositionDTO> result = debtPositionService.getDebtPositionsByOrganizationIdAndIud(organizationId, iud, debtPositionOrigins,accessToken);
    //then
    Assertions.assertNotNull(result);
    Assertions.assertSame(expectedResult, result);
  }
}
