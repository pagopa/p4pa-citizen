package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.citizen.connector.debtpositions.client.ReceiptClient;
import it.gov.pagopa.pu.citizen.connector.debtpositions.client.ReceiptNoPiiSearchClient;
import it.gov.pagopa.pu.citizen.connector.debtpositions.client.ReceiptNoPiiViewSearchClient;
import it.gov.pagopa.pu.citizen.dto.DebtorReceiptsFiltersDTO;
import it.gov.pagopa.pu.citizen.dto.FileResourceDTO;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.List;
import java.util.Set;

import static it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptOriginType.RECEIPT_PAGOPA;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReceiptServiceImplTest {

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @Mock
  private ReceiptNoPiiViewSearchClient receiptNoPiiViewSearchClientMock;
  @Mock
  private ReceiptClient clientMock;
  @Mock
  private ReceiptNoPiiSearchClient receiptNoPiiSearchClientMock;

  ReceiptService receiptService;

  @BeforeEach
  void setUp() {
    receiptService = new ReceiptServiceImpl(receiptNoPiiViewSearchClientMock, clientMock, receiptNoPiiSearchClientMock);
  }

  @AfterEach
  void mockitoVerify() {
    Mockito.verifyNoMoreInteractions(receiptNoPiiViewSearchClientMock);
  }

  @Test
  void givenFiltersWhenGetPagedModelReceiptNoPIIViewThenReturnPagedModelReceiptNoPIIView() {
    //given
    String accessToken = "accessToken";
    DebtorReceiptsFiltersDTO debtorReceiptsFiltersDTO = podamFactory.manufacturePojo(DebtorReceiptsFiltersDTO.class);

    PageRequest pageRequest = PageRequest.of(1, 10);
    PagedModelReceiptNoPIIView expectedResult = podamFactory.manufacturePojo(PagedModelReceiptNoPIIView.class);
    Mockito.when(receiptNoPiiViewSearchClientMock.getPagedModelReceiptNoPIIView(debtorReceiptsFiltersDTO, pageRequest, accessToken)).thenReturn(expectedResult);
    //when
    PagedModelReceiptNoPIIView result = receiptService.getPagedModelReceiptNoPIIView(debtorReceiptsFiltersDTO, pageRequest, accessToken);
    //then
    assertNotNull(result);
    assertEquals(expectedResult, result);
  }

  @Test
  void whenGetReceiptDetailThenInvokeClient() {
    String accessToken = "ACCESSTOKEN";
    Long receiptId = 1L;
    Long organizationId = 2L;
    ReceiptDetailDTO expectedResult = new ReceiptDetailDTO();

    when(clientMock.getReceiptDetail(receiptId, organizationId, accessToken))
      .thenReturn(expectedResult);

    ReceiptDetailDTO result = receiptService.getReceiptDetail(receiptId, organizationId, accessToken);

    assertSame(expectedResult, result);
  }

  @ParameterizedTest
  @ValueSource(longs = {1L,2L})
  void givenValidReceiptDebtorWhenIsReceiptDebtorValidThenTrue(Long receiptCount) {
    String accessToken = "ACCESSTOKEN";
    Long receiptId = 1L;
    Long organizationId = 2L;
    String debtorFiscalCode = "debtorFiscalCode";

    when(receiptNoPiiSearchClientMock.validateReceiptDebtor(receiptId, organizationId, debtorFiscalCode, accessToken))
      .thenReturn(receiptCount);

    boolean result = receiptService.isReceiptDebtorValid(receiptId, organizationId, debtorFiscalCode, accessToken);

    assertTrue(result);
  }

  @Test
  void givenInvalidReceiptDebtorWhenIsReceiptDebtorValidThenFalse() {
    String accessToken = "ACCESSTOKEN";
    Long receiptId = 1L;
    Long organizationId = 2L;
    String debtorFiscalCode = "debtorFiscalCode";

    when(receiptNoPiiSearchClientMock.validateReceiptDebtor(receiptId, organizationId, debtorFiscalCode, accessToken))
      .thenReturn(0L);

    boolean result = receiptService.isReceiptDebtorValid(receiptId, organizationId, debtorFiscalCode, accessToken);

    assertFalse(result);
  }

  @Test
  void whenGetReceiptPdfThenInvokeClient() {
    String accessToken = "ACCESSTOKEN";
    Long receiptId = 1L;
    Long organizationId = 1L;
    FileResourceDTO expectedResult = new FileResourceDTO();

    when(clientMock.getReceiptPdf(receiptId, organizationId, accessToken))
      .thenReturn(expectedResult);

    FileResourceDTO result = receiptService.getReceiptPdf(receiptId, organizationId, accessToken);

    assertSame(expectedResult, result);
  }

  @Test
  void whenGetDebtorReceiptsThenInvokeClient() {
    String accessToken = "ACCESSTOKEN";
    String debtorFiscalCode = "debtorFiscalCode";
    Long organizationId = 1L;
    Long paymentOptionId = 2L;
    Long debtPositionId = 3L;
    List<ReceiptOriginType> receiptOrigins = List.of(RECEIPT_PAGOPA);
    List<InstallmentStatus> installmentStatuses = List.of(InstallmentStatus.PAID);
    List<ReceiptNoPIIView> expectedResult = podamFactory.manufacturePojo(List.class, ReceiptNoPIIView.class);

    when(receiptNoPiiViewSearchClientMock.getDebtorReceipts(debtorFiscalCode, organizationId,
      debtPositionId,paymentOptionId,receiptOrigins,installmentStatuses, accessToken))
      .thenReturn(expectedResult);

    List<ReceiptNoPIIView> result = receiptService.getDebtorReceipts(debtorFiscalCode, organizationId,
      debtPositionId,paymentOptionId,receiptOrigins,installmentStatuses, accessToken);

    assertSame(expectedResult, result);
  }

  @Test
  void whenGetReceiptNoPiiListThenInvokeClient() {
    String accessToken = "ACCESSTOKEN";
    Set<Long> receiptIds = Set.of(1L);

    List<ReceiptNoPII> expectedResult = podamFactory.manufacturePojo(List.class, ReceiptNoPII.class);

    when(receiptNoPiiSearchClientMock.getReceiptNoPiiList(receiptIds, accessToken))
      .thenReturn(expectedResult);

    List<ReceiptNoPII> result = receiptService.getReceiptNoPiiList(receiptIds, accessToken);

    assertSame(expectedResult, result);
  }
}
