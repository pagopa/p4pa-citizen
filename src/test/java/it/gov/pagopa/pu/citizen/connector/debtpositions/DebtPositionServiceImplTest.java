package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.citizen.connector.debtpositions.client.DebtPositionClient;
import it.gov.pagopa.pu.citizen.connector.debtpositions.client.DebtPositionViewSearchClient;
import it.gov.pagopa.pu.citizen.connector.debtpositions.client.InstallmentNoPiiSearchClient;
import it.gov.pagopa.pu.citizen.connector.debtpositions.client.PaymentOptionSearchClient;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class DebtPositionServiceImplTest {

  @Mock
  private DebtPositionClient debtPositionClientMock;
  @Mock
  private DebtPositionViewSearchClient debtPositionViewSearchClientMock;
  @Mock
  private PaymentOptionSearchClient paymentOptionSearchClientMock;
  @Mock
  private InstallmentNoPiiSearchClient installmentNoPiiSearchClientMock;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  DebtPositionService debtPositionService;

  @BeforeEach
  void setUp() {
    debtPositionService = new DebtPositionServiceImpl(debtPositionClientMock, debtPositionViewSearchClientMock, paymentOptionSearchClientMock, installmentNoPiiSearchClientMock);
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

  @Test
  void whenGetPagedModelDebtPositionViewThenInvokeClient() {
    // given
    String accessToken = "ACCESS_TOKEN";
    List<Long> organizationIds = List.of(1L, 2L);
    String debtorFiscalCode = "debtorFiscalCode";
    Pageable pageable = PageRequest.of(0, 10);

    PagedModelDebtPositionView expectedResult = podamFactory.manufacturePojo(PagedModelDebtPositionView.class);

    Mockito.when(debtPositionViewSearchClientMock.getPagedModelDebtPositionView(
      organizationIds, debtorFiscalCode, accessToken, pageable
    )).thenReturn(expectedResult);

    // when
    PagedModelDebtPositionView result =
      debtPositionService.getPagedModelDebtPositionView(organizationIds, debtorFiscalCode, accessToken, pageable);

    // then
    Assertions.assertNotNull(result);
    Assertions.assertSame(expectedResult, result);
  }

  @Test
  void whenGetPaymentOptionsThenInvokeClient() {
    // given
    String accessToken = "ACCESS_TOKEN";
    Long debtPositionId = 100L;

    List<PaymentOption> expectedResult = podamFactory.manufacturePojo(List.class, PaymentOption.class);

    Mockito.when(paymentOptionSearchClientMock.getPaymentOptions(debtPositionId, accessToken))
      .thenReturn(expectedResult);

    // when
    List<PaymentOption> result =
      debtPositionService.getPaymentOptions(debtPositionId, accessToken);

    // then
    Assertions.assertNotNull(result);
    Assertions.assertSame(expectedResult, result);
  }

  @Test
  void whenGetInstallmentsThenInvokeClient() {
    // given
    String accessToken = "ACCESS_TOKEN";
    Long debtPositionId = 100L;
    String installmentStatuses = "PAID,UNPAID";

    List<InstallmentNoPII> expectedResult = podamFactory.manufacturePojo(List.class, InstallmentNoPII.class);

    Mockito.when(installmentNoPiiSearchClientMock.getInstallments(debtPositionId, installmentStatuses, accessToken))
      .thenReturn(expectedResult);

    // when
    List<InstallmentNoPII> result =
      debtPositionService.getInstallments(debtPositionId, installmentStatuses, accessToken);

    // then
    Assertions.assertNotNull(result);
    Assertions.assertSame(expectedResult, result);
  }


}
