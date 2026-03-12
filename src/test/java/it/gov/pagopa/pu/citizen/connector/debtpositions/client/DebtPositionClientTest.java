package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.controller.generated.DebtPositionApi;
import it.gov.pagopa.pu.debtpositions.controller.generated.DebtPositionViewSearchControllerApi;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionOrigin;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtorDebtPositionDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.PagedDebtorUnpaidDebtPositionDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DebtPositionClientTest {

  @Mock
  private DebtPositionsApisHolder debtPositionApisHolderMock;
  @Mock
  private DebtPositionViewSearchControllerApi debtPositionViewSearchControllerApiMock;
  @Mock
  private DebtPositionApi debtPositionApiMock;

  private DebtPositionClient debtPositionClient;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @BeforeEach
  void setUp() {
    debtPositionClient = new DebtPositionClient(debtPositionApisHolderMock);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      debtPositionApisHolderMock,
      debtPositionViewSearchControllerApiMock,
      debtPositionApiMock
    );
  }

  @Test
  void whenCreateDebtPositionThenInvokeWithAccessToken() {
    DebtPositionDTO debtPositionDTO = podamFactory.manufacturePojo(DebtPositionDTO.class);
    Boolean massive = true;
    String accessToken = "ACCESSTOKEN";
    DebtPositionDTO expectedResult = podamFactory.manufacturePojo(DebtPositionDTO.class);

    when(debtPositionApisHolderMock.getDebtPositionApi(accessToken))
      .thenReturn(debtPositionApiMock);
    when(debtPositionApiMock.createDebtPosition(debtPositionDTO, massive))
      .thenReturn(expectedResult);

    DebtPositionDTO result = debtPositionClient.createDebtPosition(debtPositionDTO, massive, accessToken);

    Assertions.assertSame(expectedResult, result);
  }

  @Test
  void whenGetDebtPositionThenInvokeWithAccessToken() {
    String accessToken = "ACCESSTOKEN";
    DebtPositionDTO expectedResult = podamFactory.manufacturePojo(DebtPositionDTO.class);

    when(debtPositionApisHolderMock.getDebtPositionApi(accessToken))
      .thenReturn(debtPositionApiMock);
    when(debtPositionApiMock.getDebtPosition(expectedResult.getDebtPositionId()))
      .thenReturn(expectedResult);

    DebtPositionDTO result = debtPositionClient.getDebtPosition(expectedResult.getDebtPositionId(), accessToken);

    Assertions.assertSame(expectedResult, result);
  }

  @Test
  void givenNoDebtPositionWhenGetDebtPositionThenNullResult() {
    String accessToken = "ACCESSTOKEN";
    Long debtPositionId = 1L;

    when(debtPositionApisHolderMock.getDebtPositionApi(accessToken))
      .thenReturn(debtPositionApiMock);
    when(debtPositionApiMock.getDebtPosition(debtPositionId))
      .thenThrow(
        HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

    DebtPositionDTO result = debtPositionClient.getDebtPosition(debtPositionId, accessToken);

    Assertions.assertNull(result);
  }

  @Test
  void whenGetDebtPositionByInstallmentIdThenInvokeWithAccessToken() {
    String accessToken = "ACCESSTOKEN";
    Long installmentId = 1L;
    DebtPositionDTO expectedResult = podamFactory.manufacturePojo(DebtPositionDTO.class);

    when(debtPositionApisHolderMock.getDebtPositionApi(accessToken))
      .thenReturn(debtPositionApiMock);
    when(debtPositionApiMock.getDebtPositionByInstallmentId(installmentId))
      .thenReturn(expectedResult);

    DebtPositionDTO result = debtPositionClient.getDebtPositionByInstallmentId(installmentId, accessToken);

    Assertions.assertSame(expectedResult, result);
  }

  @Test
  void givenNoDebtPositionWhenGetDebtPositionByInstallmentIdThenNullResult() {
    String accessToken = "ACCESSTOKEN";
    Long installmentId = 1L;

    when(debtPositionApisHolderMock.getDebtPositionApi(accessToken))
      .thenReturn(debtPositionApiMock);
    when(debtPositionApiMock.getDebtPositionByInstallmentId(installmentId))
      .thenThrow(
        HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

    DebtPositionDTO result = debtPositionClient.getDebtPositionByInstallmentId(installmentId, accessToken);

    Assertions.assertNull(result);
  }

  @Test
  void whenGetDebtPositionsByOrganizationIdAndIuvThenInvokeWithAccessToken() {
    String accessToken = "ACCESSTOKEN";
    Long organizationId = 1L;
    String nav = "nav";
    List<DebtPositionOrigin> debtPositionOrigins = List.of(DebtPositionOrigin.ORDINARY);
    List<DebtPositionDTO> expectedResult = podamFactory.manufacturePojo(List.class,DebtPositionDTO.class);

    when(debtPositionApisHolderMock.getDebtPositionApi(accessToken))
      .thenReturn(debtPositionApiMock);
    when(debtPositionApiMock.getDebtPositionsByOrganizationIdAndNav(organizationId,nav,debtPositionOrigins))
      .thenReturn(expectedResult);

    List<DebtPositionDTO> result = debtPositionClient.getDebtPositionsByOrganizationIdAndNav(organizationId, nav, debtPositionOrigins, accessToken);

    Assertions.assertSame(expectedResult, result);
  }

  @Test
  void whenGetDebtPositionsByOrganizationIdAndIudThenInvokeWithAccessToken() {
    String accessToken = "ACCESSTOKEN";
    Long organizationId = 1L;
    String iud = "iud";
    List<DebtPositionOrigin> debtPositionOrigins = List.of(DebtPositionOrigin.ORDINARY);
    List<DebtPositionDTO> expectedResult = podamFactory.manufacturePojo(List.class,DebtPositionDTO.class);

    when(debtPositionApisHolderMock.getDebtPositionApi(accessToken))
      .thenReturn(debtPositionApiMock);
    when(debtPositionApiMock.getDebtPositionsByOrganizationIdAndIud(organizationId,iud,debtPositionOrigins))
      .thenReturn(expectedResult);

    List<DebtPositionDTO> result = debtPositionClient.getDebtPositionsByOrganizationIdAndIud(organizationId, iud, debtPositionOrigins, accessToken);

    Assertions.assertSame(expectedResult, result);
  }

  @Test
  void whenGetPagedDebtorUnpaidDebtPositionThenInvokeWithAccessToken() {
    String accessToken = "ACCESSTOKEN";
    String debtorFiscalCode = "debtorFiscalCode";
    List<Long> organizationIds = List.of(1L);

    PagedDebtorUnpaidDebtPositionDTO expectedResult = podamFactory.manufacturePojo(PagedDebtorUnpaidDebtPositionDTO.class);

    when(debtPositionApisHolderMock.getDebtPositionApi(accessToken))
      .thenReturn(debtPositionApiMock);
    when(debtPositionApiMock.getPagedDebtorUnpaidDebtPositions(debtorFiscalCode, organizationIds, 0, 1, new ArrayList<>()))
      .thenReturn(expectedResult);

    PagedDebtorUnpaidDebtPositionDTO result = debtPositionClient.getPagedDebtorUnpaidDebtPosition(debtorFiscalCode, organizationIds, Pageable.ofSize(1), accessToken);

    Assertions.assertNotNull(result);
    Assertions.assertSame(expectedResult, result);
  }

  @Test
  void whenGetDebtorDebtPositionOverviewThenInvokeWithAccessToken() {
    String accessToken = "ACCESSTOKEN";
    String debtorFiscalCode = "debtorFiscalCode";
    Long organizationId = 1L;
    Long debtPositionId = 1L;

    DebtorDebtPositionDTO expectedResult = podamFactory.manufacturePojo(DebtorDebtPositionDTO.class);

    when(debtPositionApisHolderMock.getDebtPositionApi(accessToken))
      .thenReturn(debtPositionApiMock);
    when(debtPositionApiMock.getDebtorUnpaidDebtPositionOverview(debtPositionId, debtorFiscalCode, organizationId))
      .thenReturn(expectedResult);

    DebtorDebtPositionDTO result = debtPositionClient.getDebtorDebtPositionOverview(debtPositionId, debtorFiscalCode, organizationId, accessToken);

    Assertions.assertNotNull(result);
    Assertions.assertSame(expectedResult, result);
  }

  @Test
  void givenNoDebtorDebtPositionDTOwhenGetDebtorDebtPositionOverviewThenNullResult() {
    String accessToken = "ACCESSTOKEN";
    String debtorFiscalCode = "debtorFiscalCode";
    Long organizationId = 1L;
    Long debtPositionId = 1L;

    when(debtPositionApisHolderMock.getDebtPositionApi(accessToken))
      .thenReturn(debtPositionApiMock);
    when(debtPositionApiMock.getDebtorUnpaidDebtPositionOverview(debtPositionId, debtorFiscalCode, organizationId))
      .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

    DebtorDebtPositionDTO result = debtPositionClient.getDebtorDebtPositionOverview(debtPositionId, debtorFiscalCode, organizationId, accessToken);

    Assertions.assertNull(result);
  }
}
