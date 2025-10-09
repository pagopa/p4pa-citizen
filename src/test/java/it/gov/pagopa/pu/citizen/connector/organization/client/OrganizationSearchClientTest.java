package it.gov.pagopa.pu.citizen.connector.organization.client;

import it.gov.pagopa.pu.citizen.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.organization.controller.generated.OrganizationSearchControllerApi;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationStatus;
import it.gov.pagopa.pu.organization.dto.generated.PagedModelOrganization;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
class OrganizationSearchClientTest {

  @Mock
  private OrganizationApisHolder organizationApisHolderMock;
  @Mock
  private OrganizationSearchControllerApi organizationSearchControllerApiMock;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  OrganizationSearchClient organizationSearchClient;

  @BeforeEach
  void setUp() {
    organizationSearchClient = new OrganizationSearchClient(organizationApisHolderMock);
  }

  @AfterEach
  void mockitoVerify(){
    Mockito.verifyNoMoreInteractions(organizationApisHolderMock, organizationSearchControllerApiMock);
  }

  @Test
  void givenBrokerIdWhenGetPagedOrganizationsByBrokerIdAndStatusThenReturnOrganizationsList() {
    //given
    String accessToken = "ACCESS_TOKEN";
    Long brokerId = 1L;
    PagedModelOrganization expectedResult = podamFactory.manufacturePojo(PagedModelOrganization.class);

    PageRequest pageable = PageRequest.of(0, 10);

    Mockito.when(organizationApisHolderMock.getOrganizationSearchControllerApi(accessToken)).thenReturn(organizationSearchControllerApiMock);

    Mockito.when(organizationSearchControllerApiMock.crudOrganizationsFindPagedOrganizationsByBrokerIdAndStatus(brokerId, OrganizationStatus.ACTIVE, pageable.getPageNumber(), pageable.getPageSize(), new ArrayList<>())).thenReturn(expectedResult);
    //when
    PagedModelOrganization result = organizationSearchClient.getPagedOrganizationsByBrokerIdAndStatus(brokerId, OrganizationStatus.ACTIVE, pageable, accessToken);
    //then
    Assertions.assertNotNull(result);
    Assertions.assertEquals(expectedResult, result);
  }

}
