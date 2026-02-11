package it.gov.pagopa.pu.citizen.connector.organization.client;

import it.gov.pagopa.pu.citizen.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.organization.controller.generated.BrokerEntityControllerApi;
import it.gov.pagopa.pu.organization.dto.generated.Broker;
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

@ExtendWith(MockitoExtension.class)
class BrokerEntityClientTest {
  @Mock
  private OrganizationApisHolder organizationApisHolder;
  @Mock
  private BrokerEntityControllerApi brokerEntityControllerApiMock;

  private BrokerEntityClient brokerEntityClient;

  @BeforeEach
  void setUp() {
    brokerEntityClient = new BrokerEntityClient(organizationApisHolder);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      organizationApisHolder,
      brokerEntityControllerApiMock
    );
  }

  @Test
  void whenGetBrokerThenInvokeWithAccessToken() {
    Long brokerId = 1L;
    String accessToken = "ACCESSTOKEN";
    Broker expectedResult = new Broker();

    Mockito.when(organizationApisHolder.getBrokerEntityControllerApi(accessToken))
      .thenReturn(brokerEntityControllerApiMock);
    Mockito.when(brokerEntityControllerApiMock.crudGetBroker(String.valueOf(brokerId)))
      .thenReturn(expectedResult);

    Broker result = brokerEntityClient.getBroker(brokerId, accessToken);

    Assertions.assertSame(expectedResult, result);
  }

  @Test
  void givenNotFoundWhenGetBrokerThenNull() {
    Long brokerId = 1L;
    String accessToken = "ACCESSTOKEN";

    Mockito.when(organizationApisHolder.getBrokerEntityControllerApi(accessToken))
      .thenReturn(brokerEntityControllerApiMock);
    Mockito.when(brokerEntityControllerApiMock.crudGetBroker(String.valueOf(brokerId)))
      .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

    Broker result = brokerEntityClient.getBroker(brokerId, accessToken);

    Assertions.assertNull(result);
  }
}
