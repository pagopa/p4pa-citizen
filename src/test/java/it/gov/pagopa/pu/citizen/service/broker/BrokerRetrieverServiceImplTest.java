package it.gov.pagopa.pu.citizen.service.broker;

import it.gov.pagopa.pu.citizen.connector.organization.BrokerConfigurationService;
import it.gov.pagopa.pu.citizen.connector.organization.BrokerService;
import it.gov.pagopa.pu.citizen.connector.organization.OrganizationService;
import it.gov.pagopa.pu.citizen.dto.generated.BrokerInfoDTO;
import it.gov.pagopa.pu.citizen.exception.InvalidParamException;
import it.gov.pagopa.pu.citizen.exception.common.NotFoundException;
import it.gov.pagopa.pu.citizen.mapper.BrokerInfoDTOMapper;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.organization.dto.generated.Broker;
import it.gov.pagopa.pu.organization.dto.generated.BrokerConfiguration;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrokerRetrieverServiceImplTest {

  @Mock
  private OrganizationService organizationServiceMock;
  @Mock
  private BrokerInfoDTOMapper brokerInfoDTOMapperMock;
  @Mock
  private BrokerService brokerServiceMock;
  @Mock
  private BrokerConfigurationService brokerConfigurationServiceMock;

  private BrokerRetrieverService brokerRetrieverService;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();
  private final String accessToken = "TOKEN";

  @BeforeEach
  void setUp() {
    brokerRetrieverService =
      new BrokerRetrieverServiceImpl(
        organizationServiceMock,
        brokerInfoDTOMapperMock,
        brokerServiceMock,
        brokerConfigurationServiceMock);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      organizationServiceMock,
      brokerInfoDTOMapperMock,
      brokerServiceMock,
      brokerConfigurationServiceMock
    );
  }

  @Test
  void whenBrokerIdPresentThenUseGetBrokerAndReturnMappedResult() {

    Long brokerId = 10L;

    Broker broker = podamFactory.manufacturePojo(Broker.class);
    broker.setBrokerId(brokerId);

    BrokerConfiguration brokerConfiguration = podamFactory.manufacturePojo(BrokerConfiguration.class);
    brokerConfiguration.setBrokerId(brokerId);

    Organization organization = podamFactory.manufacturePojo(Organization.class);
    BrokerInfoDTO expectedResult = podamFactory.manufacturePojo(BrokerInfoDTO.class);

    when(brokerServiceMock.getBroker(brokerId, accessToken))
      .thenReturn(broker);

    when(brokerConfigurationServiceMock.getBrokerConfiguration(brokerId, accessToken))
      .thenReturn(brokerConfiguration);

    when(organizationServiceMock.getBrokerOrganization(brokerId, accessToken))
      .thenReturn(organization);

    when(brokerInfoDTOMapperMock.map(organization, broker.getExternalId(), brokerConfiguration))
      .thenReturn(expectedResult);

    BrokerInfoDTO result =
      brokerRetrieverService.getBrokerInfo(brokerId, null, accessToken);

    assertNotNull(result);
    assertSame(expectedResult, result);
  }

  @Test
  void whenBrokerIdIsNullThenUseExternalIdAndReturnMappedResult() {

    String externalId = "EXT-123";

    Broker broker = podamFactory.manufacturePojo(Broker.class);
    broker.setBrokerId(20L);

    Organization organization = podamFactory.manufacturePojo(Organization.class);
    BrokerConfiguration brokerConfiguration = podamFactory.manufacturePojo(BrokerConfiguration.class);
    brokerConfiguration.setBrokerId(broker.getBrokerId());
    BrokerInfoDTO expectedResult = podamFactory.manufacturePojo(BrokerInfoDTO.class);

    when(brokerServiceMock.getBrokerByExternalId(externalId, accessToken))
      .thenReturn(broker);

    when(organizationServiceMock.getBrokerOrganization(broker.getBrokerId(), accessToken))
      .thenReturn(organization);

    when(brokerConfigurationServiceMock.getBrokerConfiguration(broker.getBrokerId(), accessToken))
      .thenReturn(brokerConfiguration);

    when(brokerInfoDTOMapperMock.map(organization, broker.getExternalId(), brokerConfiguration))
      .thenReturn(expectedResult);

    BrokerInfoDTO result =
      brokerRetrieverService.getBrokerInfo(null, externalId, accessToken);

    assertNotNull(result);
    assertSame(expectedResult, result);
  }

  @Test
  void givenBrokerIdAndNoBrokerFoundThenThrowResourceNotFound() {

    Long brokerId = 1L;

    when(brokerServiceMock.getBroker(brokerId, accessToken))
      .thenReturn(null);

    NotFoundException ex = assertThrows(NotFoundException.class,
        () -> brokerRetrieverService.getBrokerInfo(brokerId, null, accessToken));

    assertEquals("BROKER_NOT_FOUND", ex.getCode());

    Mockito.verifyNoInteractions(organizationServiceMock, brokerInfoDTOMapperMock);
  }

  @Test
  void givenBrokerConfigurationNotFoundThenThrowResourceNotFound() {
    Long brokerId = 1L;

    Broker broker = podamFactory.manufacturePojo(Broker.class);
    broker.setBrokerId(brokerId);

    when(brokerServiceMock.getBroker(brokerId, accessToken))
      .thenReturn(broker);

    when(brokerConfigurationServiceMock.getBrokerConfiguration(brokerId, accessToken))
      .thenReturn(null);

    NotFoundException ex =
      assertThrows(NotFoundException.class,
        () -> brokerRetrieverService.getBrokerInfo(brokerId, null, accessToken));

    assertEquals("BROKER_CONFIGURATION_NOT_FOUND", ex.getCode());
  }

  @Test
  void givenExternalIdAndNoBrokerFoundThenThrowResourceNotFound() {

    String externalId = "EXT-404";

    when(brokerServiceMock.getBrokerByExternalId(externalId, accessToken))
      .thenReturn(null);

    NotFoundException ex =
      assertThrows(NotFoundException.class,
        () -> brokerRetrieverService.getBrokerInfo(null, externalId, accessToken));

    assertEquals("BROKER_NOT_FOUND", ex.getCode());

    Mockito.verifyNoInteractions(organizationServiceMock, brokerInfoDTOMapperMock);
  }

  @Test
  void givenBothBrokerIdAndExternalIdNullWhenGetBrokerInfoThenThrowInvalidParamException() {
    InvalidParamException ex = Assertions.assertThrows(
      InvalidParamException.class,
      () -> brokerRetrieverService.getBrokerInfo(null, null, "token")
    );

    Assertions.assertEquals("INVALID_FIELDS", ex.getCode());
    Mockito.verifyNoInteractions(brokerServiceMock);
  }
}
