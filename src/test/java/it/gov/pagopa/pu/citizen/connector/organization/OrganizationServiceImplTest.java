package it.gov.pagopa.pu.citizen.connector.organization;

import it.gov.pagopa.pu.citizen.connector.organization.client.OrganizationSearchClient;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
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

import java.util.List;
import java.util.Set;

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
  void givenParameterWhenGetOrganizationsByBrokerIdAndFiltersThenReturnOrganizationsList() {
    //given
    String accessToken = "ACCESS_TOKEN";
    Long brokerId = 1L;
    String orgName = "orgName";
    String ipaCode = "ipaCode";
    List<Organization> expectedResult = podamFactory.manufacturePojo(List.class, Organization.class);

    PageRequest pageable = PageRequest.of(0, 10);

    Mockito.when(organizationSearchClientMock.getOrganizationsByBrokerIdAndFilters(brokerId,orgName, ipaCode, Set.of(1L), pageable, accessToken)).thenReturn(expectedResult);
    //when
    List<Organization> result = organizationService.getOrganizationsByBrokerIdAndFilters(brokerId, orgName, ipaCode, Set.of(1L), pageable, accessToken);

    //then
    Assertions.assertNotNull(result);
    Assertions.assertEquals(expectedResult, result);
  }
}
