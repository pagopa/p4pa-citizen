package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.citizen.connector.debtpositions.client.TransferSearchClient;
import it.gov.pagopa.pu.debtpositions.dto.generated.PostalIbanVerifyResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceImplTest {

  @Mock
  private TransferSearchClient transferSearchClientMock;

  private TransferServiceImpl transferService;

  @BeforeEach
  void setUp() {
    transferService = new TransferServiceImpl(transferSearchClientMock);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      transferSearchClientMock
    );
  }

  @Test
  void whenVerifyPostalIbanThenDelegateToClient() {
    // given
    List<Long> installmentIds = List.of(1L, 2L);
    String accessToken = "ACCESSTOKEN";

    PostalIbanVerifyResponse expectedResponse = new PostalIbanVerifyResponse();

    when(transferSearchClientMock.verifyPostalIban(installmentIds, accessToken))
      .thenReturn(expectedResponse);

    // when
    PostalIbanVerifyResponse result =
      transferService.verifyPostalIban(installmentIds, accessToken);

    // then
    assertSame(expectedResponse, result);
  }

}
