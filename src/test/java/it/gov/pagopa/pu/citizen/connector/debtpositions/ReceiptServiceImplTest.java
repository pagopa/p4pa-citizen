package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.citizen.connector.debtpositions.client.ReceiptNoPiiViewSearchClient;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.PagedModelReceiptNoPIIView;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptOriginType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.List;

import static it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptOriginType.RECEIPT_PAGOPA;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReceiptServiceImplTest {

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @Mock
  private ReceiptNoPiiViewSearchClient receiptNoPiiViewSearchClientMock;

  ReceiptService receiptService;

  @BeforeEach
  void setUp() {
    receiptService = new ReceiptServiceImpl(receiptNoPiiViewSearchClientMock);
  }

  @AfterEach
  void mockitoVerify() {
    Mockito.verifyNoMoreInteractions(receiptNoPiiViewSearchClientMock);
  }

  @Test
  void givenFiltersWhenGetPagedModelReceiptNoPIIViewThenReturnPagedModelReceiptNoPIIView() {
    //given
    List<String> orgsFiscalCode = List.of("orgFiscalCode");
    String accessToken = "accessToken";
    String fiscalCode = "fiscalCode";
    List<ReceiptOriginType> receipts = List.of(RECEIPT_PAGOPA);

    PageRequest pageRequest = PageRequest.of(1, 10);
    PagedModelReceiptNoPIIView expectedResult = podamFactory.manufacturePojo(PagedModelReceiptNoPIIView.class);
    Mockito.when(receiptNoPiiViewSearchClientMock.getPagedModelReceiptNoPIIView(fiscalCode, orgsFiscalCode, receipts, pageRequest, accessToken)).thenReturn(expectedResult);
    //when
    PagedModelReceiptNoPIIView result = receiptService.getPagedModelReceiptNoPIIView(fiscalCode, orgsFiscalCode, receipts, pageRequest, accessToken);
    //then
    assertNotNull(result);
    assertEquals(expectedResult, result);
  }
}
