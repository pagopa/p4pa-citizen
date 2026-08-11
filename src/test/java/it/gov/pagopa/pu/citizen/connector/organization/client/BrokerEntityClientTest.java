package it.gov.pagopa.pu.citizen.connector.organization.client;

import it.gov.pagopa.pu.citizen.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.citizen.exception.common.RestInvokeNotFoundException;
import it.gov.pagopa.pu.organization.client.generated.BrokerEntityControllerApi;
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

import static org.mockito.Mockito.when;

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

    when(organizationApisHolder.getBrokerEntityControllerApi(accessToken))
      .thenReturn(brokerEntityControllerApiMock);
    when(brokerEntityControllerApiMock.crudGetBroker(String.valueOf(brokerId)))
      .thenReturn(expectedResult);

    Broker result = brokerEntityClient.getBroker(brokerId, accessToken);

    Assertions.assertSame(expectedResult, result);
  }

  @Test
  void givenNotFoundWhenGetBrokerThenNull() {
    Long brokerId = 1L;
    String accessToken = "ACCESSTOKEN";

    when(organizationApisHolder.getBrokerEntityControllerApi(accessToken))
      .thenReturn(brokerEntityControllerApiMock);
    when(brokerEntityControllerApiMock.crudGetBroker(String.valueOf(brokerId)))
      .thenThrow(new RestInvokeNotFoundException("APPNAME", HttpStatus.NOT_FOUND, "ERROR", "ERRORCODE", "ERRORMESSAGE"));

    Broker result = brokerEntityClient.getBroker(brokerId, accessToken);

    Assertions.assertNull(result);
  }
}
