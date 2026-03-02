package it.gov.pagopa.pu.citizen.controller;

import it.gov.pagopa.pu.auth.dto.generated.UserInfo;
import it.gov.pagopa.pu.citizen.dto.generated.BrokerInfoDTO;
import it.gov.pagopa.pu.citizen.security.SecurityUtilsTest;
import it.gov.pagopa.pu.citizen.service.broker.BrokerRetrieverService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.jemos.podam.api.PodamFactory;

@ExtendWith(MockitoExtension.class)
class BrokerControllerTest {

  @Mock
  private BrokerRetrieverService brokerRetrieverServiceMock;

  @InjectMocks
  private BrokerController brokerController;

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
      brokerRetrieverServiceMock
    );
  }

  @AfterEach
  void clearContext(){
    SecurityUtilsTest.clearSecurityContext();
  }

  @Test
  void whenGetBrokerInfoThenOk() {
    Long brokerId = 1L;
    BrokerInfoDTO expectedResult = podamFactory.manufacturePojo(BrokerInfoDTO.class);

    Mockito.when(brokerRetrieverServiceMock.getBrokerInfo(brokerId, null, accessToken))
      .thenReturn(expectedResult);

    ResponseEntity<BrokerInfoDTO> response = brokerController.getBrokerInfo(brokerId, null);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertSame(expectedResult, response.getBody());
  }

  @Test
  void whenGetBrokerInfoByExternalIdThenOk() {
    String externalId = "EXT123";
    BrokerInfoDTO expectedResult = podamFactory.manufacturePojo(BrokerInfoDTO.class);

    Mockito.when(brokerRetrieverServiceMock.getBrokerInfo(null, externalId, accessToken))
      .thenReturn(expectedResult);

    ResponseEntity<BrokerInfoDTO> response = brokerController.getBrokerInfo(null, externalId);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertSame(expectedResult, response.getBody());
  }

  @Test
  void givenNoBrokerInfoDTOWhenGetBrokerInfoThenNotFound() {
    Long brokerId = 1L;

    Mockito.when(brokerRetrieverServiceMock.getBrokerInfo(brokerId, null, accessToken))
      .thenReturn(null);

    ResponseEntity<BrokerInfoDTO> response = brokerController.getBrokerInfo(brokerId, null);

    Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Assertions.assertNull(response.getBody());
  }

}
