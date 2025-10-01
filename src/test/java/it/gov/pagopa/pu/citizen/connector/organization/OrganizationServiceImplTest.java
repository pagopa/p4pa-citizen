package it.gov.pagopa.pu.citizen.connector.organization;

import it.gov.pagopa.pu.citizen.connector.organization.client.OrganizationSearchClient;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
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
  void givenWhenGetOrganizationsByBrokerIdAndFiltersThen() {
    //given
    String accessToken = "ACCESS_TOKEN";
    Long brokerId = 1L;
    String orgName = "orgName";
    String ipaCode = "ipaCode";
    PagedModelOrganization expectedResult = podamFactory.manufacturePojo(PagedModelOrganization.class);

    PageRequest pageable = PageRequest.of(0, 10);

    Mockito.when(organizationSearchClientMock.getOrganizationsByBrokerIdAndFilters(brokerId,orgName, ipaCode, Set.of(1L), pageable, accessToken)).thenReturn(expectedResult);
    //when
    PagedModelOrganization result = organizationService.getOrganizationsByBrokerIdAndFilters(brokerId, orgName, ipaCode, Set.of(1L), pageable, accessToken);

    //then
    Assertions.assertNotNull(result);
    Assertions.assertEquals(expectedResult, result);
  }
}
