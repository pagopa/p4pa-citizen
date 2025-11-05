package it.gov.pagopa.pu.citizen.controller;

import it.gov.pagopa.pu.auth.dto.generated.UserInfo;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorReceiptsDTO;
import it.gov.pagopa.pu.citizen.security.SecurityUtilsTest;
import it.gov.pagopa.pu.citizen.service.receipt.ReceiptFacadeService;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.jemos.podam.api.PodamFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ReceiptControllerTest {

  @Mock
  private ReceiptFacadeService receiptFacadeServiceMock;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();
  private final String accessToken = "fakeAccessToken";
  private final UserInfo loggedUser = podamFactory.manufacturePojo(UserInfo.class);

  ReceiptController receiptController;

  @BeforeEach
  void setUp() {
    receiptController = new ReceiptController(receiptFacadeServiceMock);
  }

  @AfterEach
  void mockitoVerify() {
    SecurityUtilsTest.configureSecurityContext(accessToken, loggedUser);
    Mockito.verifyNoMoreInteractions(receiptFacadeServiceMock);
  }

  @Test
  void givenFiltersWhenGetPagedDebtorReceiptsThenOk() {
    //given
    Long brokerId = 1L;
    String fiscalCode = "fiscalCode";
    String orgName = "orgName";
    PageRequest pageRequest = PageRequest.of(1, 10);
    PagedDebtorReceiptsDTO expectedResult = podamFactory.manufacturePojo(PagedDebtorReceiptsDTO.class);

    Mockito.when(receiptFacadeServiceMock.getPagedDebtorReceipts(brokerId, orgName, fiscalCode, accessToken, pageRequest)).thenReturn(expectedResult);
    //when
    ResponseEntity<PagedDebtorReceiptsDTO> result = receiptController.getPagedDebtorReceipts(brokerId, fiscalCode, orgName, pageRequest);
    //then
    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertNotNull(result);
    assertEquals(expectedResult, result.getBody());
  }
}
