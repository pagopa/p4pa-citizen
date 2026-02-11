package it.gov.pagopa.pu.citizen.service.broker;

import it.gov.pagopa.pu.auth.dto.generated.UserInfo;
import it.gov.pagopa.pu.citizen.connector.organization.BrokerService;
import it.gov.pagopa.pu.citizen.connector.organization.OrganizationService;
import it.gov.pagopa.pu.citizen.dto.generated.BrokerInfoDTO;
import it.gov.pagopa.pu.citizen.exception.ResourceNotFoundException;
import it.gov.pagopa.pu.citizen.mapper.BrokerInfoDTOMapper;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.organization.dto.generated.Broker;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BrokerRetrieverServiceImplTest {
  @Mock
  private OrganizationService organizationServiceMock;
  @Mock
  private BrokerInfoDTOMapper brokerInfoDTOMapperMock;
  @Mock
  private BrokerService brokerServiceMock;
  private BrokerRetrieverService brokerRetrieverService;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  private final String accessToken = "TOKEN";

  @BeforeEach
  void setUp() {
    brokerRetrieverService = new BrokerRetrieverServiceImpl(organizationServiceMock,brokerInfoDTOMapperMock, brokerServiceMock);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      organizationServiceMock,
      brokerInfoDTOMapperMock
    );
  }

  @Test
  void whenGetBrokerInfoThenOk() {
    UserInfo loggedUser = new UserInfo();
    loggedUser.setMappedExternalUserId("mappedExternalUserId");
    Organization organization = podamFactory.manufacturePojo(Organization.class);
    Broker broker = podamFactory.manufacturePojo(Broker.class);
    BrokerInfoDTO expectedResult = podamFactory.manufacturePojo(BrokerInfoDTO.class);

    Mockito.when(brokerServiceMock.getBroker(organization.getBrokerId(), accessToken))
      .thenReturn(broker);
    Mockito.when(organizationServiceMock.getBrokerOrganization(organization.getBrokerId(), accessToken))
      .thenReturn(organization);
    Mockito.when(brokerInfoDTOMapperMock.map(organization,broker.getArpuConfig())).thenReturn(expectedResult);

    BrokerInfoDTO result = brokerRetrieverService.getBrokerInfo(organization.getBrokerId(), accessToken);

    assertNotNull(result);
    assertSame(expectedResult, result);
  }

  @Test
  void givenNoBrokerWhenGetBrokerInfoThenResourceNotFound() {
    UserInfo loggedUser = new UserInfo();
    loggedUser.setMappedExternalUserId("mappedExternalUserId");
    Long brokerId = 1L;

    Mockito.when(brokerServiceMock.getBroker(brokerId, accessToken))
      .thenReturn(null);

    ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class, () -> brokerRetrieverService.getBrokerInfo(brokerId, accessToken));

    assertSame("BROKER_NOT_FOUND", resourceNotFoundException.getCode());
    Mockito.verifyNoInteractions(organizationServiceMock);
  }
}
