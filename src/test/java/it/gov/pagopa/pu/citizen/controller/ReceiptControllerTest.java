package it.gov.pagopa.pu.citizen.controller;

import it.gov.pagopa.pu.auth.dto.generated.UserInfo;
import it.gov.pagopa.pu.citizen.dto.DebtorReceiptsFiltersDTO;
import it.gov.pagopa.pu.citizen.dto.FileResourceDTO;
import it.gov.pagopa.pu.citizen.dto.ReceiptDetailExtendedDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtorReceiptDTO;
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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.jemos.podam.api.PodamFactory;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    SecurityUtilsTest.configureSecurityContext(accessToken, loggedUser);
    receiptController = new ReceiptController(receiptFacadeServiceMock);
  }

  @AfterEach
  void mockitoVerify() {
    Mockito.verifyNoMoreInteractions(receiptFacadeServiceMock);
  }

  @Test
  void givenFiltersWhenGetPagedDebtorReceiptsThenOk() {
    //given
    Long brokerId = 1L;
    String fiscalCode = "fiscalCode";
    String orgName = "orgName";
    String noticeNumberOrIuv = "noticeNumberOrIuv";
    OffsetDateTime paymentDateTimeFrom = OffsetDateTime.now().minusDays(1);
    OffsetDateTime paymentDateTimeTo = OffsetDateTime.now();
    PageRequest pageRequest = PageRequest.of(1, 10);
    DebtorReceiptsFiltersDTO debtorReceiptsFiltersDTO = DebtorReceiptsFiltersDTO.builder()
      .debtorFiscalCode(fiscalCode)
      .noticeNumberOrIuv(noticeNumberOrIuv)
      .paymentDateTimeTo(paymentDateTimeTo)
      .paymentDateTimeFrom(paymentDateTimeFrom)
      .build();
    PagedDebtorReceiptsDTO expectedResult = podamFactory.manufacturePojo(PagedDebtorReceiptsDTO.class);

    Mockito.when(receiptFacadeServiceMock.getPagedDebtorReceipts(brokerId, orgName, debtorReceiptsFiltersDTO,  accessToken, pageRequest)).thenReturn(expectedResult);
    //when
    ResponseEntity<PagedDebtorReceiptsDTO> result = receiptController.getPagedDebtorReceipts(brokerId, fiscalCode, orgName, noticeNumberOrIuv, paymentDateTimeFrom, paymentDateTimeTo, pageRequest);
    //then
    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertNotNull(result);
    assertEquals(expectedResult, result.getBody());
  }

  @Test
  void whenGetReceiptDetailThenOk() {
    //given
    String fiscalCode = "fiscalCode";
    Long brokerId = 1L;
    Long organizationId = 2L;
    Long receiptId = 3L;
    ReceiptDetailExtendedDTO expectedResult = podamFactory.manufacturePojo(ReceiptDetailExtendedDTO.class);

    Mockito.when(receiptFacadeServiceMock.getReceiptDetail(fiscalCode, brokerId, organizationId, receiptId, accessToken)).thenReturn(expectedResult);
    //when
    ResponseEntity<ReceiptDetailExtendedDTO> result = receiptController.getReceiptDetail(fiscalCode, brokerId, organizationId, receiptId);
    //then
    assertNotNull(result);
    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertEquals(expectedResult, result.getBody());
  }

  @Test
  void givenNoReceiptWhenGetReceiptDetailThenNotFound() {
    //given
    String fiscalCode = "fiscalCode";
    Long brokerId = 1L;
    Long organizationId = 2L;
    Long receiptId = 3L;

    Mockito.when(receiptFacadeServiceMock.getReceiptDetail(fiscalCode, brokerId, organizationId, receiptId, accessToken)).thenReturn(null);
    //when
    ResponseEntity<ReceiptDetailExtendedDTO> result = receiptController.getReceiptDetail(fiscalCode, brokerId, organizationId, receiptId);
    //then
    assertNotNull(result);
    assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    assertNull(result.getBody());
  }

  @Test
  void whenGetReceiptPdfThenOk() {
    Long brokerId = 1L;
    Long organizationId = 2L;
    Long receiptId = 3L;
    String fiscalCode = "fiscalCode";

    String fileName = "fileName";
    Resource resource = new ByteArrayResource("PDF-DATA".getBytes());
    FileResourceDTO fileResourceDTO = new FileResourceDTO(resource, fileName);

    Mockito.when(receiptFacadeServiceMock.getReceiptPdf(fiscalCode, brokerId, organizationId, receiptId, accessToken))
      .thenReturn(fileResourceDTO);

    ResponseEntity<Resource> response = receiptController.getReceiptPdf(fiscalCode, brokerId, organizationId, receiptId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(resource, response.getBody());
    assertEquals(fileName, response.getHeaders().getContentDisposition().getFilename());
  }

  @Test
  void givenNullResourceWhenGetReceiptPdfThenNoContent() {
    Long brokerId = 1L;
    Long organizationId = 2L;
    Long receiptId = 3L;
    String fiscalCode = "fiscalCode";

    Mockito.when(receiptFacadeServiceMock.getReceiptPdf(fiscalCode, brokerId, organizationId, receiptId, accessToken))
      .thenReturn(null);

    ResponseEntity<Resource> response = receiptController.getReceiptPdf(fiscalCode, brokerId, organizationId, receiptId);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertNull(response.getBody());
  }

  @Test
  void whenGetDebtorReceiptsThenOk() {
    //given
    String debtorFiscalCode = "debtorFiscalCode";
    Long brokerId = 1L;
    Long organizationId = 2L;
    Long paymentOptionId = 3L;
    Long debtPositionId = 4L;
    List<DebtorReceiptDTO> expectedResult = podamFactory.manufacturePojo(List.class, DebtorReceiptDTO.class);

    Mockito.when(receiptFacadeServiceMock.getDebtorReceipts(debtorFiscalCode,brokerId,organizationId,
      debtPositionId,paymentOptionId,accessToken)).thenReturn(expectedResult);

    ResponseEntity<List<DebtorReceiptDTO>> result = receiptController.getDebtorReceipts(brokerId,organizationId,
      debtPositionId,paymentOptionId,debtorFiscalCode);

    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertNotNull(result);
    assertEquals(expectedResult, result.getBody());
  }
}
