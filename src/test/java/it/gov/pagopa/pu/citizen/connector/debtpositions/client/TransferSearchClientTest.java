package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import it.gov.pagopa.pu.debtpositions.controller.generated.TransferApi;
import it.gov.pagopa.pu.debtpositions.dto.generated.PostalIbanVerifyResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferSearchClientTest {

  @Mock
  private DebtPositionsApisHolder debtPositionsApisHolderMock;

  @Mock
  private TransferApi transferApiMock;

  private TransferSearchClient transferSearchClient;

  @BeforeEach
  void setUp() {
    transferSearchClient = new TransferSearchClient(debtPositionsApisHolderMock);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      debtPositionsApisHolderMock
    );
  }

  @Test
  void whenVerifyPostalIbanThenInvokeWithAccessToken() {
    // given
    List<Long> installmentIds = List.of(1L, 2L, 3L);
    String accessToken = "ACCESSTOKEN";

    PostalIbanVerifyResponse expectedResponse = new PostalIbanVerifyResponse();

    when(debtPositionsApisHolderMock.getTransferApi(accessToken))
      .thenReturn(transferApiMock);

    when(transferApiMock.verifyPostalIban(installmentIds))
      .thenReturn(expectedResponse);

    // when
    PostalIbanVerifyResponse result =
      transferSearchClient.verifyPostalIban(installmentIds, accessToken);

    // then
    assertSame(expectedResponse, result);
  }

  @Test
  void givenNotFoundWhenVerifyPostalIbanThenReturnNull() {
    // given
    List<Long> installmentIds = List.of(1L, 2L, 3L);
    String accessToken = "ACCESSTOKEN";

    when(debtPositionsApisHolderMock.getTransferApi(accessToken))
      .thenReturn(transferApiMock);

    when(transferApiMock.verifyPostalIban(installmentIds))
      .thenThrow(HttpClientErrorException.create(
        HttpStatus.NOT_FOUND,
        "NotFound",
        null,
        null,
        null
      ));

    // when
    PostalIbanVerifyResponse result =
      transferSearchClient.verifyPostalIban(installmentIds, accessToken);

    // then
    Assertions.assertNull(result);
  }
}
