package it.gov.pagopa.pu.citizen.connector.organization;

import it.gov.pagopa.pu.citizen.connector.organization.client.OrganizationEntityClient;
import it.gov.pagopa.pu.citizen.connector.organization.client.OrganizationSearchClient;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
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
import org.springframework.data.domain.Pageable;
import uk.co.jemos.podam.api.PodamFactory;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceImplTest {

  @Mock
  private OrganizationSearchClient organizationSearchClientMock;
  @Mock
  private OrganizationEntityClient organizationEntityClientMock;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  OrganizationService organizationService;

  @BeforeEach
  void setUp() {
    organizationService = new OrganizationServiceImpl(organizationSearchClientMock, organizationEntityClientMock);
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

  @Test
  void testGetOrganizationByOrganizationId() {
    Organization expected = new Organization();
    Long organizationId = 1L;
    String accessToken = "accessToken";

    when(organizationEntityClientMock.getOrganizationByOrganizationId(Mockito.same(organizationId), Mockito.same(accessToken)))
      .thenReturn(expected);

    Organization result = organizationService.getOrganizationByOrganizationId(organizationId, accessToken);

    assertSame(expected, result);
  }

  @Test
  void givenBrokerIdAndOrgNameWhenGetOrganizationsListByBrokerIdAndOrgNameThenReturnPagedModelOrganization() {
    String accessToken = "ACCESS_TOKEN";
    Long brokerId = 1L;
    String orgName = "orgName";
    PagedModelOrganization expectedResult = podamFactory.manufacturePojo(PagedModelOrganization.class);

    PageRequest pageable = PageRequest.of(0, 10);

    Mockito.when(organizationSearchClientMock.getOrganizationsListByBrokerIdAndOrgName(brokerId, orgName, pageable, accessToken)).thenReturn(expectedResult);
    //when
    PagedModelOrganization result = organizationService.getOrganizationsListByBrokerIdAndOrgName(brokerId, orgName, pageable, accessToken);

    //then
    Assertions.assertNotNull(result);
    Assertions.assertEquals(expectedResult, result);
  }

  @Test
  void whenGetOrganizationsByBrokerIdAndOrgNameAndOrgFiscalCodeThenInvokeClient() {
    // given
    String accessToken = "ACCESS_TOKEN";
    Long brokerId = 10L;
    String orgName = "Test Organization";
    String orgFiscalCode = "12345678901";
    Pageable pageable = PageRequest.of(0, 20);

    PagedModelOrganization expectedResult = podamFactory.manufacturePojo(PagedModelOrganization.class);

    Mockito.when(organizationSearchClientMock.getOrganizationsByBrokerIdAndOrgNameAndOrgFiscalCode(
      brokerId, orgName, orgFiscalCode, pageable, accessToken
    )).thenReturn(expectedResult);

    // when
    PagedModelOrganization result = organizationService.getOrganizationsByBrokerIdAndOrgNameAndOrgFiscalCode(
      brokerId, orgName, orgFiscalCode, pageable, accessToken
    );

    // then
    Assertions.assertNotNull(result);
    Assertions.assertSame(expectedResult, result);
  }

}
