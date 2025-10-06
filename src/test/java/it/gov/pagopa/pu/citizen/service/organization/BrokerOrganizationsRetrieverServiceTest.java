package it.gov.pagopa.pu.citizen.service.organization;

import it.gov.pagopa.pu.citizen.connector.organization.OrganizationService;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.organization.dto.generated.PageMetadata;
import it.gov.pagopa.pu.organization.dto.generated.PagedModelOrganization;
import it.gov.pagopa.pu.organization.dto.generated.PagedModelOrganizationEmbedded;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

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
  void givenMultiplePagesWhenGetAllOrganizationsByBrokerIdThenReturnAllOrganizations() {
    // given
    Long brokerId = 1L;
    String orgName = "orgName";
    String ipaCode = "ipaCode";
    String accessToken = "ACCESS_TOKEN";
    Set<Long> organizationIds = Set.of(1L);

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

    Mockito.when(organizationServiceMock.getOrganizationsByBrokerIdAndFilters(
      eq(brokerId), eq(orgName), eq(ipaCode), eq(organizationIds), any(Pageable.class), eq(accessToken)))
      .thenReturn(page0, page1);

    // when
    List<Organization> result = brokerOrganizationsRetrieverService.getAllOrganizationsByBrokerId(
      brokerId, orgName, ipaCode, organizationIds, accessToken);

    // then
    assertThat(result)
      .hasSize(3)
      .extracting(organization -> organization )
      .containsExactly(org1, org2, org3);

    verify(organizationServiceMock, Mockito.times(2))
      .getOrganizationsByBrokerIdAndFilters(eq(brokerId), eq(orgName), eq(ipaCode),
        eq(organizationIds), any(Pageable.class), eq(accessToken));
  }

  @Test
  void givenNullPageWhenGetAllOrganizationsByBrokerIdThenReturnEmptyList() {
    // given
    Long brokerId = 1L;
    String orgName = "orgName";
    String ipaCode = "ipaCode";
    String accessToken = "ACCESS_TOKEN";

    Mockito.when(organizationServiceMock.getOrganizationsByBrokerIdAndFilters(
      anyLong(), anyString(), anyString(), anySet(), any(Pageable.class), anyString()))
      .thenReturn(null);

    // when
    List<Organization> result = brokerOrganizationsRetrieverService.getAllOrganizationsByBrokerId(
      brokerId, orgName, ipaCode, Set.of(1L), accessToken);

    // then
    assertThat(result).isEmpty();
    Mockito.verifyNoMoreInteractions(organizationServiceMock);
  }
}
