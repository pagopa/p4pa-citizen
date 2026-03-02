package it.gov.pagopa.pu.citizen.connector.organization;

import it.gov.pagopa.pu.citizen.connector.organization.client.BrokerEntityClient;
import it.gov.pagopa.pu.citizen.connector.organization.client.BrokerSearchClient;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.organization.dto.generated.Broker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

@ExtendWith(MockitoExtension.class)
class BrokerServiceImplTest {
  @Mock
  private BrokerEntityClient brokerEntityClientMock;
  @Mock
  private BrokerSearchClient brokerSearchClientMock;
  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  BrokerService brokerService;

  @BeforeEach
  void setUp() {
    brokerService = new BrokerServiceImpl(brokerEntityClientMock, brokerSearchClientMock);
  }

  @AfterEach
  void mockitoVerify(){
    Mockito.verifyNoMoreInteractions(brokerEntityClientMock);
  }

  @Test
  void whenGetBrokerThenInvokeClient() {
    String accessToken = "ACCESS_TOKEN";
    Long brokerId = 1L;
    Broker expectedResult = podamFactory.manufacturePojo(Broker.class);

    Mockito.when(brokerEntityClientMock.getBroker(brokerId, accessToken)).thenReturn(expectedResult);

    Broker result = brokerService.getBroker(brokerId, accessToken);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(expectedResult, result);
  }

  @Test
  void whenGetBrokerByExternalIdThenInvokeClient() {
    String accessToken = "ACCESS_TOKEN";
    String externalId = "1";
    Broker expectedResult = podamFactory.manufacturePojo(Broker.class);

    Mockito.when(brokerSearchClientMock.getBrokerByExternalId(externalId, accessToken)).thenReturn(expectedResult);

    Broker result = brokerService.getBrokerByExternalId(accessToken, externalId);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(expectedResult, result);
  }
}
