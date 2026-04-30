package it.gov.pagopa.pu.citizen.service.organization;

import it.gov.pagopa.pu.citizen.connector.organization.OrganizationService;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.organization.dto.generated.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class BrokerOrganizationsRetrieverServiceTest {

  @Mock
  private OrganizationService organizationServiceMock;

  private static final PodamFactory podamFactory = TestUtils.getPodamFactory();

  BrokerOrganizationsRetrieverService brokerOrganizationsRetrieverService;

  @BeforeEach
  void setUp() {
    brokerOrganizationsRetrieverService = new BrokerOrganizationsRetrieverService(1, organizationServiceMock);
  }

  @Test
  void givenMultiplePagesWhenGetAllActiveOrganizationsByBrokerIdThenReturnAllActiveOrganizations() {
    // given
    Long brokerId = 1L;
    String accessToken = "ACCESS_TOKEN";


    Organization org1 = podamFactory.manufacturePojo(Organization.class);
    Organization org2 = podamFactory.manufacturePojo(Organization.class);
    List<Organization> organizationsPage0 = new ArrayList<>();
    organizationsPage0.add(org1);
    organizationsPage0.add(org2);

    PagedModelOrganization page0 = new PagedModelOrganization();
    page0.setPage(new PageMetadata(1L, 2L, 2L, 0L));
    page0.setEmbedded(new PagedModelOrganizationEmbedded(organizationsPage0));

    Organization org3 = podamFactory.manufacturePojo(Organization.class);
    List<Organization> organizationsPage1 = new ArrayList<>();
    organizationsPage0.add(org3);

    PagedModelOrganization page1 = new PagedModelOrganization();
    page1.setPage(new PageMetadata(1L, 2L, 2L, 1L));
    page1.setEmbedded(new PagedModelOrganizationEmbedded(organizationsPage1));

    Mockito.when(organizationServiceMock.getPagedOrganizationsByBrokerIdAndStatus(
      eq(brokerId), eq(OrganizationStatus.ACTIVE), any(Pageable.class), eq(accessToken)))
      .thenReturn(page0, page1);

    // when
    List<Organization> result = brokerOrganizationsRetrieverService.getAllActiveOrganizationsByBrokerId(
      brokerId, accessToken);

    // then
    assertThat(result)
      .hasSize(3)
      .extracting(organization -> organization )
      .containsExactly(org1, org2, org3);

    verify(organizationServiceMock, Mockito.times(2))
      .getPagedOrganizationsByBrokerIdAndStatus(eq(brokerId),eq(OrganizationStatus.ACTIVE), any(Pageable.class), eq(accessToken));
  }

  @Test
  void givenNullPageWhenGetAllActiveOrganizationsByBrokerIdThenReturnEmptyList() {
    // given
    Long brokerId = 1L;
    String accessToken = "ACCESS_TOKEN";

    Mockito.when(organizationServiceMock.getPagedOrganizationsByBrokerIdAndStatus(
      anyLong(), any(), any(Pageable.class), anyString()))
      .thenReturn(null);

    // when
    List<Organization> result = brokerOrganizationsRetrieverService.getAllActiveOrganizationsByBrokerId(
      brokerId, accessToken);

    // then
    assertThat(result).isEmpty();
    Mockito.verifyNoMoreInteractions(organizationServiceMock);
  }

  @Test
  void givenLastPageWhenNextPageExceedsTotalPagesThenStreamStops() {
    Long brokerId = 1L;
    String accessToken = "ACCESS_TOKEN";

    Organization org1 = podamFactory.manufacturePojo(Organization.class);
    List<Organization> organizationsPage0 = new ArrayList<>();
    organizationsPage0.add(org1);

    PagedModelOrganization page0 = new PagedModelOrganization();
    page0.setPage(new PageMetadata(1L, 1L, 1L, 1L));
    page0.setEmbedded(new PagedModelOrganizationEmbedded(organizationsPage0));

    Mockito.when(organizationServiceMock.getPagedOrganizationsByBrokerIdAndStatus(
      eq(brokerId), eq(OrganizationStatus.ACTIVE), any(Pageable.class), eq(accessToken)))
      .thenReturn(page0);

    List<Organization> result = brokerOrganizationsRetrieverService.getAllActiveOrganizationsByBrokerId(
      brokerId, accessToken);

    assertThat(result)
      .hasSize(1)
      .extracting(organization -> organization )
      .containsExactly(org1);

    verifyNoMoreInteractions(organizationServiceMock);
  }

  @Test
  void givenPageWithNullEmbeddedWhenFilteredThenPageExcluded() {
    Long brokerId = 1L;
    String accessToken = "ACCESS_TOKEN";

    PagedModelOrganization pageWithNullEmbedded = new PagedModelOrganization();
    pageWithNullEmbedded.setPage(new PageMetadata(0L, 1L, 1L, 0L));
    pageWithNullEmbedded.setEmbedded(null);

    Mockito.when(organizationServiceMock.getPagedOrganizationsByBrokerIdAndStatus(
      anyLong(),any(), any(Pageable.class), anyString()))
      .thenReturn(pageWithNullEmbedded);

    List<Organization> result = brokerOrganizationsRetrieverService.getAllActiveOrganizationsByBrokerId(
      brokerId, accessToken);

    assertThat(result).isEmpty();
  }

  @Test
  void givenPageWithNullOrganizationsWhenFilteredThenPageExcluded() {
    Long brokerId = 1L;
    String accessToken = "ACCESS_TOKEN";

    PagedModelOrganization pageWithNullOrgs = new PagedModelOrganization();
    pageWithNullOrgs.setPage(new PageMetadata(0L, 1L, 1L, 0L));
    pageWithNullOrgs.setEmbedded(new PagedModelOrganizationEmbedded(null));

    Mockito.when(organizationServiceMock.getPagedOrganizationsByBrokerIdAndStatus(
      anyLong(), any(), any(Pageable.class), anyString()))
      .thenReturn(pageWithNullOrgs);

    List<Organization> result = brokerOrganizationsRetrieverService.getAllActiveOrganizationsByBrokerId(
      brokerId, accessToken);

    assertThat(result).isEmpty();
  }

  @Test
  void givenMultiplePagesWhenGetAllOrganizationsByBrokerIdAndStatusThenReturnAllOrganizations() {
    // given
    Long brokerId = 1L;
    String accessToken = "ACCESS_TOKEN";
    String orgName = "orgName";


    Organization org1 = podamFactory.manufacturePojo(Organization.class);
    Organization org2 = podamFactory.manufacturePojo(Organization.class);
    List<Organization> organizationsPage0 = new ArrayList<>();
    organizationsPage0.add(org1);
    organizationsPage0.add(org2);

    PagedModelOrganization page0 = new PagedModelOrganization();
    page0.setPage(new PageMetadata(1L, 2L, 2L, 0L));
    page0.setEmbedded(new PagedModelOrganizationEmbedded(organizationsPage0));

    Organization org3 = podamFactory.manufacturePojo(Organization.class);
    List<Organization> organizationsPage1 = new ArrayList<>();
    organizationsPage0.add(org3);

    PagedModelOrganization page1 = new PagedModelOrganization();
    page1.setPage(new PageMetadata(1L, 2L, 2L, 1L));
    page1.setEmbedded(new PagedModelOrganizationEmbedded(organizationsPage1));

    Mockito.when(organizationServiceMock.getOrganizationsListByBrokerIdAndOrgName(
        eq(brokerId), eq(orgName), any(Pageable.class), eq(accessToken)))
      .thenReturn(page0, page1);

    // when
    List<Organization> result = brokerOrganizationsRetrieverService.getAllOrganizationsByBrokerIdAndOrgName(
      brokerId, orgName,accessToken);

    // then
    assertThat(result)
      .hasSize(3)
      .extracting(organization -> organization )
      .containsExactly(org1, org2, org3);

    verify(organizationServiceMock, Mockito.times(2))
      .getOrganizationsListByBrokerIdAndOrgName(eq(brokerId),eq(orgName), any(Pageable.class), eq(accessToken));
  }

  @Test
  void givenMultiplePagesWhenGetAllOrganizationsByBrokerIdAndOrgNameAndOrgFiscalCodeThenReturnAllOrganizations() {
    // given
    Long brokerId = 1L;
    String accessToken = "ACCESS_TOKEN";
    String orgName = "orgName";
    String orgFiscalCode = "orgFiscalCode";

    // Page 0
    Organization org1 = podamFactory.manufacturePojo(Organization.class);
    Organization org2 = podamFactory.manufacturePojo(Organization.class);
    List<Organization> page0Orgs = new ArrayList<>();
    page0Orgs.add(org1);
    page0Orgs.add(org2);

    PagedModelOrganization page0 = new PagedModelOrganization();
    page0.setPage(new PageMetadata(1L, 2L, 2L, 0L));
    page0.setEmbedded(new PagedModelOrganizationEmbedded(page0Orgs));

    Organization org3 = podamFactory.manufacturePojo(Organization.class);
    List<Organization> page1Orgs = new ArrayList<>();
    page1Orgs.add(org3);

    PagedModelOrganization page1 = new PagedModelOrganization();
    page1.setPage(new PageMetadata(1L, 2L, 2L, 1L));
    page1.setEmbedded(new PagedModelOrganizationEmbedded(page1Orgs));

    Mockito.when(organizationServiceMock.getOrganizationsByBrokerIdAndOrgNameAndOrgFiscalCode(
      eq(brokerId),
      eq(orgName),
      eq(orgFiscalCode),
      any(Pageable.class),
      eq(accessToken)
    )).thenReturn(page0, page1);

    // when
    List<Organization> result = brokerOrganizationsRetrieverService
      .getAllOrganizationsByBrokerIdAndOrgNameAndOrgFiscalCode(
        brokerId, orgName, orgFiscalCode, accessToken
      );

    // then
    assertThat(result)
      .hasSize(3)
      .containsExactly(org1, org2, org3);

    verify(organizationServiceMock, Mockito.times(2))
      .getOrganizationsByBrokerIdAndOrgNameAndOrgFiscalCode(
        eq(brokerId),
        eq(orgName),
        eq(orgFiscalCode),
        any(Pageable.class),
        eq(accessToken)
      );

    verifyNoMoreInteractions(organizationServiceMock);
  }

}
