package it.gov.pagopa.pu.citizen.connector.organization.client;

import it.gov.pagopa.pu.citizen.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.organization.controller.generated.BrokerSearchControllerApi;
import it.gov.pagopa.pu.organization.dto.generated.Broker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrokerSearchClientTest {

  @Mock
  private OrganizationApisHolder organizationApisHolderMock;

  @Mock
  private BrokerSearchControllerApi brokerSearchControllerApiMock;

  @InjectMocks
  private BrokerSearchClient brokerSearchClient;

  @AfterEach
  void verifyNoMoreInteractionsAfterEach() {
    Mockito.verifyNoMoreInteractions(
      organizationApisHolderMock,
      brokerSearchControllerApiMock
    );
  }

  @Test
  void whenGetBrokerByExternalIdThenInvokeWithAccessToken() {
    // given
    String accessToken = "access-token";
    String externalId = "external-id";
    Broker expectedBroker = new Broker();

    when(organizationApisHolderMock.getBrokerSearchControllerApi(accessToken))
      .thenReturn(brokerSearchControllerApiMock);

    when(brokerSearchControllerApiMock.crudBrokersFindBrokerByExternalId(externalId))
      .thenReturn(expectedBroker);

    // when
    Broker result = brokerSearchClient.getBrokerByExternalId(externalId, accessToken);

    // then
    assertSame(expectedBroker, result);
  }

  @Test
  void givenNotFoundWhenGetBrokerByExternalIdThenNull() {
    String externalId = "external-id";
    String accessToken = "ACCESSTOKEN";

    when(organizationApisHolderMock.getBrokerSearchControllerApi(accessToken))
      .thenReturn(brokerSearchControllerApiMock);

    when(brokerSearchControllerApiMock.crudBrokersFindBrokerByExternalId(externalId))
      .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

    Broker result = brokerSearchClient.getBrokerByExternalId(externalId, accessToken);

    Assertions.assertNull(result);
  }
}
