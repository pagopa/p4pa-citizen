package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import it.gov.pagopa.pu.citizen.dto.FileResourceDTO;
import it.gov.pagopa.pu.debtpositions.controller.generated.ReceiptApi;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptDetailDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReceiptClientTest {

  @Mock
  private DebtPositionsApisHolder debtPositionApisHolderMock;
  @Mock
  private ReceiptApi receiptApiMock;

  private ReceiptClient receiptClient;

  @BeforeEach
  void setUp() {
    receiptClient = new ReceiptClient(debtPositionApisHolderMock);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      debtPositionApisHolderMock
    );
  }

  @Test
  void whenGetReceiptDetailThenInvokeWithAccessToken() {
    Long receiptId = 123L;
    String accessToken = "ACCESSTOKEN";
    Long organizationId = 1L;
    ReceiptDetailDTO expectedResult = new ReceiptDetailDTO();

    when(debtPositionApisHolderMock.getReceiptApi(accessToken))
      .thenReturn(receiptApiMock);
    when(receiptApiMock.getReceiptDetail(receiptId, organizationId, null, null))
      .thenReturn(expectedResult);

    ReceiptDetailDTO result = receiptClient.getReceiptDetail(receiptId, organizationId, accessToken);

    assertSame(expectedResult, result);
  }

  @Test
  void givenNotFoundWhenGetReceiptDetailThenReturnNull() {
    Long receiptId = 123L;
    Long organizationId = 1L;
    String accessToken = "ACCESSTOKEN";

    when(debtPositionApisHolderMock.getReceiptApi(accessToken))
      .thenReturn(receiptApiMock);
    when(receiptApiMock.getReceiptDetail(receiptId, organizationId, null, null))
      .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

    ReceiptDetailDTO result = receiptClient.getReceiptDetail(receiptId, organizationId, accessToken);

    Assertions.assertNull(result);
  }

  @Test
  void whenReceiptPdfThenInvokeWithAccessToken() {
    Long receiptId = 123L;
    Long organizationId = 1L;
    String accessToken = "ACCESSTOKEN";

    ByteArrayResource expectedResource = new ByteArrayResource("PDF-DATA".getBytes());
    String expectedFileName = "filename";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentDisposition(
      ContentDisposition.attachment().filename(expectedFileName).build());
    ResponseEntity<Resource> responseEntity = new ResponseEntity<>(expectedResource, headers, HttpStatus.OK);

    when(debtPositionApisHolderMock.getReceiptApi(accessToken))
      .thenReturn(receiptApiMock);
    when(receiptApiMock.getReceiptPdfWithHttpInfo(receiptId,organizationId)).thenReturn(responseEntity);

    FileResourceDTO response = receiptClient.getReceiptPdf(receiptId, organizationId, accessToken);

    Assertions.assertNotNull(response);
    Assertions.assertEquals(expectedResource,response.getResource());
    Assertions.assertEquals(expectedFileName,response.getFileName());
  }

  @Test
  void givenNotFoundWhenReceiptPdfThenNull() {
    Long receiptId = 123L;
    Long organizationId = 1L;
    String accessToken = "ACCESSTOKEN";

    when(debtPositionApisHolderMock.getReceiptApi(accessToken))
      .thenReturn(receiptApiMock);
    when(receiptApiMock.getReceiptPdfWithHttpInfo(receiptId, organizationId))
      .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

    FileResourceDTO response = receiptClient.getReceiptPdf(receiptId, organizationId, accessToken);

    Assertions.assertNull(response);
  }
}

