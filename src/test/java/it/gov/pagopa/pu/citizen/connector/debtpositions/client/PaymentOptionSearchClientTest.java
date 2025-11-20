package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.controller.generated.PaymentOptionSearchControllerApi;
import it.gov.pagopa.pu.debtpositions.dto.generated.CollectionModelPaymentOption;
import it.gov.pagopa.pu.debtpositions.dto.generated.PaymentOption;
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
class PaymentOptionSearchClientTest {

  @Mock
  private DebtPositionsApisHolder debtPositionsApisHolderMock;
  @Mock
  private PaymentOptionSearchControllerApi paymentOptionSearchControllerApiMock;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  PaymentOptionSearchClient paymentOptionSearchClient;

  @BeforeEach
  void setUp() {
    paymentOptionSearchClient = new PaymentOptionSearchClient(debtPositionsApisHolderMock);
  }

  @AfterEach
  void mockitoVerify() {
    Mockito.verifyNoMoreInteractions(
      debtPositionsApisHolderMock,
      paymentOptionSearchControllerApiMock
    );
  }

  @Test
  void givenDebtPositionIdWhenGetPaymentOptionsThenReturnPaymentOptionList() {
    // given
    String accessToken = "ACCESS_TOKEN";
    Long debtPositionId = 55L;

    CollectionModelPaymentOption expectedResult =
      podamFactory.manufacturePojo(CollectionModelPaymentOption.class);

    Mockito.when(debtPositionsApisHolderMock.getPaymentOptionSearchControllerApi(accessToken))
      .thenReturn(paymentOptionSearchControllerApiMock);

    Mockito.when(paymentOptionSearchControllerApiMock
        .crudPaymentOptionsFindPayablePaymentOptionsByDebtPositionId(debtPositionId)
      )
      .thenReturn(expectedResult);

    // when
    List<PaymentOption> result =
      paymentOptionSearchClient.getPaymentOptions(debtPositionId, accessToken);

    // then
    Assertions.assertNotNull(result);
    Assertions.assertEquals(
      expectedResult.getEmbedded().getPaymentOptions(),
      result
    );
  }

  @Test
  void givenNullCollectionWhenGetPaymentOptionsThenReturnEmptyList() {
    String accessToken = "ACCESS_TOKEN";
    Long debtPositionId = 55L;

    Mockito.when(debtPositionsApisHolderMock.getPaymentOptionSearchControllerApi(accessToken))
      .thenReturn(paymentOptionSearchControllerApiMock);

    Mockito.when(paymentOptionSearchControllerApiMock
        .crudPaymentOptionsFindPayablePaymentOptionsByDebtPositionId(debtPositionId)
      )
      .thenReturn(null);

    List<PaymentOption> result =
      paymentOptionSearchClient.getPaymentOptions(debtPositionId, accessToken);

    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  void givenNullEmbeddedWhenGetPaymentOptionsThenReturnEmptyList() {
    String accessToken = "ACCESS_TOKEN";
    Long debtPositionId = 55L;

    CollectionModelPaymentOption collection =
      podamFactory.manufacturePojo(CollectionModelPaymentOption.class);
    collection.setEmbedded(null);

    Mockito.when(debtPositionsApisHolderMock.getPaymentOptionSearchControllerApi(accessToken))
      .thenReturn(paymentOptionSearchControllerApiMock);

    Mockito.when(paymentOptionSearchControllerApiMock
        .crudPaymentOptionsFindPayablePaymentOptionsByDebtPositionId(debtPositionId)
      )
      .thenReturn(collection);

    List<PaymentOption> result =
      paymentOptionSearchClient.getPaymentOptions(debtPositionId, accessToken);

    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isEmpty());
  }
}
