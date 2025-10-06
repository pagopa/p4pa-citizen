package it.gov.pagopa.pu.citizen.connector.organization;

import it.gov.pagopa.pu.citizen.connector.organization.client.OrganizationSearchClient;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
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

@ExtendWith(MockitoExtension.class)
class OrganizationServiceImplTest {

  @Mock
  private OrganizationSearchClient organizationSearchClientMock;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  OrganizationService organizationService;

  @BeforeEach
  void setUp() {
    organizationService =new OrganizationServiceImpl(organizationSearchClientMock);
  }

  @AfterEach
  void mockitoVerify(){
    Mockito.verifyNoMoreInteractions(organizationSearchClientMock);
  }

  @Test
  void givenParameterWhenGetPagedOrganizationsByBrokerIdAndStatusThenReturnOrganizationsList() {
    //given
    String accessToken = "ACCESS_TOKEN";
    Long brokerId = 1L;
    PagedModelOrganization expectedResult = podamFactory.manufacturePojo(PagedModelOrganization.class);

    PageRequest pageable = PageRequest.of(0, 10);

    Mockito.when(organizationSearchClientMock.getPagedOrganizationsByBrokerIdAndStatus(brokerId, OrganizationStatus.ACTIVE, pageable, accessToken)).thenReturn(expectedResult);
    //when
    PagedModelOrganization result = organizationService.getPagedOrganizationsByBrokerIdAndStatus(brokerId,OrganizationStatus.ACTIVE, pageable, accessToken);

    //then
    Assertions.assertNotNull(result);
    Assertions.assertEquals(expectedResult, result);
  }
}
