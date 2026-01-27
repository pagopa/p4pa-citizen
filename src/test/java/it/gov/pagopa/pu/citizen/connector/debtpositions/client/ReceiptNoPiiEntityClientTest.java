package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.controller.generated.ReceiptNoPiiEntityControllerApi;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptNoPII;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import uk.co.jemos.podam.api.PodamFactory;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReceiptNoPiiEntityClientTest {

  @Mock
  private DebtPositionsApisHolder debtPositionApisHolderMock;
  @Mock
  private ReceiptNoPiiEntityControllerApi receiptNoPiiEntityControllerApiMock;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  private ReceiptNoPiiEntityClient receiptNoPiiEntityClient;

  @BeforeEach
  void setUp() {
    receiptNoPiiEntityClient = new ReceiptNoPiiEntityClient(debtPositionApisHolderMock);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      debtPositionApisHolderMock,
      receiptNoPiiEntityControllerApiMock
    );
  }

  @Test
  void whenGetReceiptNoPIIThenInvokeWithAccessToken() {
    //given
    String accessToken = "ACCESSTOKEN";
    Long receiptId = 1L;

    ReceiptNoPII expectedResult = podamFactory.manufacturePojo(ReceiptNoPII.class);

    Mockito.when(debtPositionApisHolderMock.getReceiptNoPiiEntityControllerApi(accessToken)).thenReturn(receiptNoPiiEntityControllerApiMock);
    Mockito.when(receiptNoPiiEntityControllerApiMock.crudGetReceiptnopii(String.valueOf(receiptId))).thenReturn(expectedResult);
    //when
    ReceiptNoPII result = receiptNoPiiEntityClient.getReceiptNoPII(receiptId, accessToken);
    //then
    assertSame(expectedResult, result);
  }

  @Test
  void whenGetReceiptNoPIIThenReturnNull() {
    //given
    String accessToken = "ACCESSTOKEN";
    Long receiptId = 1L;

    Mockito.when(debtPositionApisHolderMock.getReceiptNoPiiEntityControllerApi(accessToken)).thenReturn(receiptNoPiiEntityControllerApiMock);
    Mockito.when(receiptNoPiiEntityControllerApiMock.crudGetReceiptnopii(String.valueOf(receiptId))).thenThrow(
      HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));
    //when
    ReceiptNoPII result = receiptNoPiiEntityClient.getReceiptNoPII(receiptId, accessToken);
    //then
    assertNull(result);
  }
}
