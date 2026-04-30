package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.controller.generated.ReceiptNoPiiSearchControllerApi;
import it.gov.pagopa.pu.debtpositions.dto.generated.CollectionModelReceiptNoPII;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptNoPII;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReceiptNoPiiSearchClientTest {

  @Mock
  private DebtPositionsApisHolder debtPositionApisHolderMock;
  @Mock
  private ReceiptNoPiiSearchControllerApi receiptNoPiiSearchControllerApiMock;

  private ReceiptNoPiiSearchClient receiptNoPiiSearchClient;
  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

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

  @Test
  void whenGetReceiptNoPiiListThenReturnList() {
    // given
    String accessToken = "accessToken";
    Set<Long> receiptIds = Set.of(1L, 2L, 3L);

    CollectionModelReceiptNoPII collectionModelReceiptNoPII = podamFactory.manufacturePojo(CollectionModelReceiptNoPII.class);

    Mockito.when(debtPositionApisHolderMock.getReceiptNoPiiSearchControllerApi(accessToken))
      .thenReturn(receiptNoPiiSearchControllerApiMock);
    Mockito.when(receiptNoPiiSearchControllerApiMock.crudReceiptsFindAllByReceiptIdIn(receiptIds))
      .thenReturn(collectionModelReceiptNoPII);

    // when
    List<ReceiptNoPII> result = receiptNoPiiSearchClient.getReceiptNoPiiList(receiptIds, accessToken);

    // then
    assertNotNull(result);
    assertEquals(collectionModelReceiptNoPII.getEmbedded().getReceiptNoPIIs(), result);
  }

  @Test
  void givenNoEmbeddedWhenGetReceiptNoPiiListThenReturnEmptyList() {
    // given
    String accessToken = "accessToken";
    Set<Long> receiptIds = Set.of(1L, 2L, 3L);

    CollectionModelReceiptNoPII collectionModelReceiptNoPII = podamFactory.manufacturePojo(CollectionModelReceiptNoPII.class);
    collectionModelReceiptNoPII.setEmbedded(null);

    Mockito.when(debtPositionApisHolderMock.getReceiptNoPiiSearchControllerApi(accessToken))
      .thenReturn(receiptNoPiiSearchControllerApiMock);
    Mockito.when(receiptNoPiiSearchControllerApiMock.crudReceiptsFindAllByReceiptIdIn(receiptIds))
      .thenReturn(collectionModelReceiptNoPII);

    // when
    List<ReceiptNoPII> result = receiptNoPiiSearchClient.getReceiptNoPiiList(receiptIds, accessToken);

    // then
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void givenNullCollectionModelWhenGetReceiptNoPiiListThenReturnEmptyList() {
    // given
    String accessToken = "accessToken";
    Set<Long> receiptIds = Set.of(1L, 2L, 3L);

    Mockito.when(debtPositionApisHolderMock.getReceiptNoPiiSearchControllerApi(accessToken))
      .thenReturn(receiptNoPiiSearchControllerApiMock);
    Mockito.when(receiptNoPiiSearchControllerApiMock.crudReceiptsFindAllByReceiptIdIn(receiptIds))
      .thenReturn(null);

    // when
    List<ReceiptNoPII> result = receiptNoPiiSearchClient.getReceiptNoPiiList(receiptIds, accessToken);

    // then
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

}
