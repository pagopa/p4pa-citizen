package it.gov.pagopa.pu.citizen.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import it.gov.pagopa.pu.auth.dto.generated.UserInfo;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionResponseDTO;
import it.gov.pagopa.pu.citizen.security.SecurityUtilsTest;
import it.gov.pagopa.pu.citizen.service.debtposition.DebtPositionRetrieverService;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.jemos.podam.api.PodamFactory;

@ExtendWith(MockitoExtension.class)
class DebtPositionControllerTest {

  @Mock
  private DebtPositionRetrieverService debtPositionRetrieverServiceMock;

  @InjectMocks
  private DebtPositionController debtPositionController;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  private final String accessToken = "fakeAccessToken";
  private final UserInfo loggedUser = podamFactory.manufacturePojo(UserInfo.class);

  @BeforeEach
  void setUp() {
    SecurityUtilsTest.configureSecurityContext(accessToken, loggedUser);
  }

  @AfterEach
  void verifyNoMoreInteractions(){
    Mockito.verifyNoMoreInteractions(
      debtPositionRetrieverServiceMock
    );
  }

  @AfterEach
  void clearContext(){
    SecurityUtilsTest.clearSecurityContext();
  }

  @Test
  void givenCorrectRequestWhenCreateDebtPositionThenOk() {
    Long brokerId = 1L;
    DebtPositionRequestDTO debtPositionRequestDTO = podamFactory.manufacturePojo(DebtPositionRequestDTO.class);
    DebtPositionResponseDTO expectedResult = podamFactory.manufacturePojo(DebtPositionResponseDTO.class);

    Mockito.when(debtPositionRetrieverServiceMock.createSpontaneousDebtPosition(brokerId, debtPositionRequestDTO, accessToken))
      .thenReturn(expectedResult);

    ResponseEntity<DebtPositionResponseDTO> response = debtPositionController.createSpontaneousDebtPosition(brokerId, debtPositionRequestDTO);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertSame(expectedResult, response.getBody());
  }

  @Test
  void whenGetUnpaidPaymentNoticeZipThenOk() {
    Long brokerId = 1L;
    Long debtPositionId = 2L;
    String fiscalCode = "fiscalCode";

    Resource resource = new ByteArrayResource("PDF-DATA".getBytes());

    Mockito.when(debtPositionRetrieverServiceMock.getDebtPositionNoticesZip(brokerId, fiscalCode, debtPositionId, accessToken))
        .thenReturn(resource);

    ResponseEntity<Resource> response = debtPositionController.getUnpaidPaymentNoticeZip(brokerId, fiscalCode, debtPositionId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(resource, response.getBody());
    assertEquals(debtPositionId+"_NOTICES_PDF.zip", response.getHeaders().getContentDisposition().getFilename());
  }

  @Test
  void givenNullResourceWhenGetUnpaidPaymentNoticeZipThenNoContent() {
    Long brokerId = 1L;
    Long debtPositionId = 2L;
    String fiscalCode = "fiscalCode";

    Mockito.when(debtPositionRetrieverServiceMock.getDebtPositionNoticesZip(brokerId, fiscalCode, debtPositionId, accessToken))
        .thenReturn(null);

    ResponseEntity<Resource> response = debtPositionController.getUnpaidPaymentNoticeZip(brokerId, fiscalCode, debtPositionId);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertNull(response.getBody());
  }
}
