package it.gov.pagopa.pu.citizen.service.broker;

import it.gov.pagopa.pu.auth.dto.generated.UserInfo;
import it.gov.pagopa.pu.citizen.connector.organization.OrganizationService;
import it.gov.pagopa.pu.citizen.dto.generated.BrokerInfoDTO;
import it.gov.pagopa.pu.citizen.mapper.BrokerInfoDTOMapper;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
class BrokerRetrieverServiceImplTest {
  @Mock
  private OrganizationService organizationServiceMock;
  @Mock
  private BrokerInfoDTOMapper brokerInfoDTOMapperMock;
  private BrokerRetrieverService brokerRetrieverService;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  private final String accessToken = "TOKEN";

  @BeforeEach
  void setUp() {
    brokerRetrieverService = new BrokerRetrieverServiceImpl(organizationServiceMock,brokerInfoDTOMapperMock);
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
    BrokerInfoDTO expectedResult = podamFactory.manufacturePojo(BrokerInfoDTO.class);

    Mockito.when(organizationServiceMock.getBrokerOrganization(organization.getBrokerId(), accessToken))
      .thenReturn(organization);
    Mockito.when(brokerInfoDTOMapperMock.mapFromOrganization(organization)).thenReturn(expectedResult);

    BrokerInfoDTO result = brokerRetrieverService.getBrokerInfo(organization.getBrokerId(), accessToken);

    assertNotNull(result);
    assertSame(expectedResult, result);
  }
}
