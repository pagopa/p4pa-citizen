package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import it.gov.pagopa.pu.debtpositions.controller.generated.ReceiptNoPiiSearchControllerApi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReceiptNoPiiSearchClientTest {

  @Mock
  private DebtPositionsApisHolder debtPositionApisHolderMock;
  @Mock
  private ReceiptNoPiiSearchControllerApi receiptNoPiiSearchControllerApiMock;

  private ReceiptNoPiiSearchClient receiptNoPiiSearchClient;

  @BeforeEach
  void setUp() {
    receiptNoPiiSearchClient = new ReceiptNoPiiSearchClient(debtPositionApisHolderMock);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      debtPositionApisHolderMock,
      receiptNoPiiSearchControllerApiMock
    );
  }

  @Test
  void whenGetReceiptDetailThenInvokeWithAccessToken() {
    String accessToken = "ACCESSTOKEN";
    Long receiptId = 1L;
    Long organizationId = 2L;
    String debtorFiscalCode = "debtorFiscalCode";
    long expectedResult = 3L;

    when(debtPositionApisHolderMock.getReceiptNoPiiSearchControllerApi(accessToken))
      .thenReturn(receiptNoPiiSearchControllerApiMock);
    when(receiptNoPiiSearchControllerApiMock.crudReceiptsValidateReceiptDebtor(receiptId, organizationId, debtorFiscalCode))
      .thenReturn(expectedResult);

    Long result = receiptNoPiiSearchClient.validateReceiptDebtor(receiptId, organizationId, debtorFiscalCode, accessToken);

    assertSame(expectedResult, result);
  }
}
