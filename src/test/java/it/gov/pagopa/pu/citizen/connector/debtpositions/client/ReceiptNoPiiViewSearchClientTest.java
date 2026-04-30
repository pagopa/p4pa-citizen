package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import it.gov.pagopa.pu.citizen.dto.DebtorReceiptsFiltersDTO;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.controller.generated.ReceiptNoPiiViewSearchControllerApi;
import it.gov.pagopa.pu.debtpositions.dto.generated.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import uk.co.jemos.podam.api.PodamFactory;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptOriginType.RECEIPT_PAGOPA;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReceiptNoPiiViewSearchClientTest {

  public static final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @Mock
  private DebtPositionsApisHolder debtPositionApisHolderMock;
  @Mock
  private ReceiptNoPiiViewSearchControllerApi receiptNoPiiViewSearchControllerApiMock;

  ReceiptNoPiiViewSearchClient receiptNoPiiViewSearchClient;

  @BeforeEach
  void setUp() {
    receiptNoPiiViewSearchClient = new ReceiptNoPiiViewSearchClient(debtPositionApisHolderMock);
  }

  @AfterEach
  void mockitoVerify() {
    Mockito.verifyNoMoreInteractions(debtPositionApisHolderMock, receiptNoPiiViewSearchControllerApiMock);
  }

  @Test
  void givenFiltersWhenGetPagedModelReceiptNoPIIViewThenReturnPagedModelReceiptNoPIIView() {
    //given
    List<String> orgsFiscalCode = List.of("orgFiscalCode");
    String accessToken = "accessToken";
    String fiscalCode = "fiscalCode";
    List<ReceiptOriginType> receipts = List.of(RECEIPT_PAGOPA);
    String noticeNumberOrIuv = "noticeNumberOrIuv";
    OffsetDateTime paymentDateTimeFrom = OffsetDateTime.now().minusDays(1);
    OffsetDateTime paymentDateTimeTo = OffsetDateTime.now();
    DebtorReceiptsFiltersDTO debtorReceiptsFiltersDTO = DebtorReceiptsFiltersDTO.builder()
      .debtorFiscalCode(fiscalCode)
      .noticeNumberOrIuv(noticeNumberOrIuv)
      .paymentDateTimeTo(paymentDateTimeTo)
      .paymentDateTimeFrom(paymentDateTimeFrom)
      .organizationsFiscalCode(orgsFiscalCode)
      .receiptOrigins(receipts)
      .build();

    PageRequest pageRequest = PageRequest.of(1, 10);

    PagedModelReceiptNoPIIView expectedResult = podamFactory.manufacturePojo(PagedModelReceiptNoPIIView.class);
    Mockito.when(debtPositionApisHolderMock.getReceiptNoPiiViewSearchControllerApi(accessToken)).thenReturn(receiptNoPiiViewSearchControllerApiMock);
    Mockito.when(receiptNoPiiViewSearchControllerApiMock.crudReceiptNoPiiViewGetPagedPrimaryReceiptByFilters(fiscalCode, orgsFiscalCode, receipts, noticeNumberOrIuv, paymentDateTimeFrom, paymentDateTimeTo, pageRequest.getPageNumber(), pageRequest.getPageSize(), Collections.emptyList() )).thenReturn(expectedResult);
    //when
    PagedModelReceiptNoPIIView result = receiptNoPiiViewSearchClient.getPagedModelReceiptNoPIIView(debtorReceiptsFiltersDTO, pageRequest, accessToken);
    //then
    assertNotNull(result);
    assertEquals(expectedResult, result);
  }

  @Test
  void whenGetDebtorReceiptsThenOk() {
    String accessToken = "accessToken";
    String debtorFiscalCode = "debtorFiscalCode";
    Long organizationId = 1L;
    Long paymentOptionId = 2L;
    Long debtPositionId = 3L;
    List<ReceiptOriginType> receiptOrigins = List.of(RECEIPT_PAGOPA);
    List< InstallmentStatus > installmentStatuses = List.of(InstallmentStatus.PAID);

    CollectionModelReceiptNoPIIView collectionModelReceiptNoPIIView = podamFactory.manufacturePojo(CollectionModelReceiptNoPIIView.class);
    Mockito.when(debtPositionApisHolderMock.getReceiptNoPiiViewSearchControllerApi(accessToken)).thenReturn(receiptNoPiiViewSearchControllerApiMock);
    Mockito.when(receiptNoPiiViewSearchControllerApiMock.crudReceiptNoPiiViewGetDebtorReceipts(
      debtorFiscalCode, organizationId, paymentOptionId, debtPositionId, receiptOrigins, installmentStatuses)).thenReturn(collectionModelReceiptNoPIIView);

    List<ReceiptNoPIIView> result = receiptNoPiiViewSearchClient.getDebtorReceipts(debtorFiscalCode,
      organizationId, paymentOptionId, debtPositionId, receiptOrigins, installmentStatuses, accessToken);

    assertNotNull(result);
    assertEquals(collectionModelReceiptNoPIIView.getEmbedded().getReceiptNoPIIViews(), result);
  }

  @Test
  void givenNoEmbeddedWhenGetDebtorReceiptsThenEmptyList() {
    String accessToken = "accessToken";
    String debtorFiscalCode = "debtorFiscalCode";
    Long organizationId = 1L;
    Long paymentOptionId = 2L;
    Long debtPositionId = 3L;
    List<ReceiptOriginType> receiptOrigins = List.of(RECEIPT_PAGOPA);
    List< InstallmentStatus > installmentStatuses = List.of(InstallmentStatus.PAID);
    CollectionModelReceiptNoPIIView collectionModelReceiptNoPIIView = podamFactory.manufacturePojo(CollectionModelReceiptNoPIIView.class);
    collectionModelReceiptNoPIIView.setEmbedded(null);

    Mockito.when(debtPositionApisHolderMock.getReceiptNoPiiViewSearchControllerApi(accessToken)).thenReturn(receiptNoPiiViewSearchControllerApiMock);
    Mockito.when(receiptNoPiiViewSearchControllerApiMock.crudReceiptNoPiiViewGetDebtorReceipts(
      debtorFiscalCode, organizationId, debtPositionId, paymentOptionId, receiptOrigins, installmentStatuses)).thenReturn(collectionModelReceiptNoPIIView);

    List<ReceiptNoPIIView> result = receiptNoPiiViewSearchClient.getDebtorReceipts(debtorFiscalCode,
      organizationId, debtPositionId, paymentOptionId, receiptOrigins, installmentStatuses, accessToken);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void givenNoCollectionModelReceiptNoPIIViewWhenGetDebtorReceiptsThenEmptyList() {
    String accessToken = "accessToken";
    String debtorFiscalCode = "debtorFiscalCode";
    Long organizationId = 1L;
    Long paymentOptionId = 2L;
    Long debtPositionId = 3L;
    List<ReceiptOriginType> receiptOrigins = List.of(RECEIPT_PAGOPA);
    List< InstallmentStatus > installmentStatuses = List.of(InstallmentStatus.PAID);

    Mockito.when(debtPositionApisHolderMock.getReceiptNoPiiViewSearchControllerApi(accessToken)).thenReturn(receiptNoPiiViewSearchControllerApiMock);
    Mockito.when(receiptNoPiiViewSearchControllerApiMock.crudReceiptNoPiiViewGetDebtorReceipts(
      debtorFiscalCode, organizationId, debtPositionId, paymentOptionId, receiptOrigins, installmentStatuses)).thenReturn(null);

    List<ReceiptNoPIIView> result = receiptNoPiiViewSearchClient.getDebtorReceipts(debtorFiscalCode,
      organizationId, debtPositionId, paymentOptionId, receiptOrigins, installmentStatuses, accessToken);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
}
