package it.gov.pagopa.pu.citizen.controller;

import it.gov.pagopa.pu.auth.dto.generated.UserInfo;
import it.gov.pagopa.pu.citizen.dto.FileResourceDTO;
import it.gov.pagopa.pu.citizen.dto.generated.*;
import it.gov.pagopa.pu.citizen.security.SecurityUtilsTest;
import it.gov.pagopa.pu.citizen.service.debtposition.DebtPositionFacadeService;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.jemos.podam.api.PodamFactory;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DebtPositionControllerTest {

  @Mock
  private DebtPositionFacadeService debtPositionFacadeServiceMock;

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
      debtPositionFacadeServiceMock
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

    Mockito.when(debtPositionFacadeServiceMock.createSpontaneousDebtPosition(brokerId, debtPositionRequestDTO, accessToken))
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

    Mockito.when(debtPositionFacadeServiceMock.getDebtPositionNoticesZip(brokerId, fiscalCode, debtPositionId, accessToken))
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

    Mockito.when(debtPositionFacadeServiceMock.getDebtPositionNoticesZip(brokerId, fiscalCode, debtPositionId, accessToken))
        .thenReturn(null);

    ResponseEntity<Resource> response = debtPositionController.getUnpaidPaymentNoticeZip(brokerId, fiscalCode, debtPositionId);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertNull(response.getBody());
  }

  @Test
  void whenGetDebtPositionDetailThenOk() {
    //given
    Long brokerId = 1L;
    Long debtPositionId = 2L;
    String fiscalCode = "fiscalCode";

    DebtPositionDTO debtPositionDTO = podamFactory.manufacturePojo(DebtPositionDTO.class);
    Mockito.when(debtPositionFacadeServiceMock.getDebtPositionDetail(brokerId, fiscalCode, debtPositionId, accessToken)).thenReturn(debtPositionDTO);
    //when
    ResponseEntity<DebtPositionDTO> response = debtPositionController.getDebtPositionDetail(brokerId, debtPositionId, fiscalCode);
    //then

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertSame(debtPositionDTO, response.getBody());
  }

  @Test
  void givenNullDebtPositionWhenGetDebtPositionDetailThenNotFound() {
    //given
    Long brokerId = 1L;
    Long debtPositionId = 2L;
    String fiscalCode = "fiscalCode";

    Mockito.when(debtPositionFacadeServiceMock.getDebtPositionDetail(brokerId, fiscalCode, debtPositionId, accessToken)).thenReturn(null);
    //when
    ResponseEntity<DebtPositionDTO> response = debtPositionController.getDebtPositionDetail(brokerId, debtPositionId, fiscalCode);
    //then

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNull(response.getBody());
  }

  @Test
  void whenGetPaymentNoticeThenOk() {
    Long brokerId = 1L;
    Long organizationId = 2L;
    String nav = "nav";
    String iud = "iud";
    Long installmentId = 3L;
    String fiscalCode = "fiscalCode";

    String fileName = "fileName";
    Resource resource = new ByteArrayResource("PDF-DATA".getBytes());
    FileResourceDTO fileResourceDTO = new FileResourceDTO(resource, fileName);

    Mockito.when(debtPositionFacadeServiceMock.getPaymentNotice(fiscalCode, brokerId, organizationId, installmentId, nav, iud, accessToken))
      .thenReturn(fileResourceDTO);

    ResponseEntity<Resource> response = debtPositionController.getPaymentNotice(fiscalCode, brokerId, organizationId, nav, installmentId, iud);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(resource, response.getBody());
    assertEquals(fileName, response.getHeaders().getContentDisposition().getFilename());
  }

  @Test
  void givenNullResourceWhenGetPaymentNoticeThenNoContent() {
    Long brokerId = 1L;
    Long organizationId = 2L;
    String nav = "nav";
    String iud = "iud";
    Long installmentId = 3L;
    String fiscalCode = "fiscalCode";

    Mockito.when(debtPositionFacadeServiceMock.getPaymentNotice(fiscalCode, brokerId, organizationId, installmentId, nav, iud, accessToken))
      .thenReturn(null);

    ResponseEntity<Resource> response = debtPositionController.getPaymentNotice(fiscalCode, brokerId, organizationId, nav, installmentId, iud);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertNull(response.getBody());
  }

  @Test
  void whenGetPagedDebtPositionsThenOk() {
    // given
    String xFiscalCode = "debtorFiscalCode";
    Long brokerId = 100L;
    String orgName = "OrgName";
    String orgFiscalCode = "12345678901";
    Pageable pageable = PageRequest.of(0, 10);

    PagedDebtorDebtPositionDTO expectedResult = podamFactory.manufacturePojo(PagedDebtorDebtPositionDTO.class);

    Mockito.when(debtPositionFacadeServiceMock.getPagedUnpaidDebtPositions(
      xFiscalCode,
      brokerId,
      orgName,
      orgFiscalCode,
      pageable,
      accessToken
    )).thenReturn(expectedResult);

    // when
    ResponseEntity<PagedDebtorDebtPositionDTO> response =
      debtPositionController.getPagedUnpaidDebtPositions(xFiscalCode, brokerId, orgName, orgFiscalCode, pageable);

    // then
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertSame(expectedResult, response.getBody());
  }

  @Test
  void whenGetDebtorUnpaidDebtPositionOverviewThenOk() {
    // given
    Long brokerId = 1L;
    Long debtPositionId = 2L;
    Long organizationId = 3L;
    String xFiscalCode = "fiscalCode";

    DebtorUnpaidDebtPositionOverviewDTO expected = podamFactory.manufacturePojo(DebtorUnpaidDebtPositionOverviewDTO.class);

    Mockito.when(debtPositionFacadeServiceMock.getDebtorUnpaidDebtPositionOverview(
      brokerId,
      debtPositionId,
      xFiscalCode,
      organizationId,
      accessToken
    )).thenReturn(expected);

    // when
    ResponseEntity<DebtorUnpaidDebtPositionOverviewDTO> response =
      debtPositionController.getDebtorUnpaidDebtPositionOverview(
        brokerId,
        debtPositionId,
        xFiscalCode,
        organizationId
      );

    // then
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertSame(expected, response.getBody());
  }

  @Test
  void givenNullWhenGetDebtorUnpaidDebtPositionOverviewThenNotFound() {
    // given
    Long brokerId = 1L;
    Long debtPositionId = 2L;
    Long organizationId = 3L;
    String xFiscalCode = "fiscalCode";

    Mockito.when(debtPositionFacadeServiceMock.getDebtorUnpaidDebtPositionOverview(
      brokerId,
      debtPositionId,
      xFiscalCode,
      organizationId,
      accessToken
    )).thenReturn(null);

    // when
    ResponseEntity<DebtorUnpaidDebtPositionOverviewDTO> response =
      debtPositionController.getDebtorUnpaidDebtPositionOverview(
        brokerId,
        debtPositionId,
        xFiscalCode,
        organizationId
      );

    // then
    Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Assertions.assertNull(response.getBody());
  }

}
