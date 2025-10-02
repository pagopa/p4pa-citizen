package it.gov.pagopa.pu.citizen.service.organization;

import it.gov.pagopa.pu.citizen.connector.debtpositions.DebtPositionTypeOrgService;
import it.gov.pagopa.pu.citizen.connector.organization.OrganizationService;
import it.gov.pagopa.pu.citizen.dto.generated.OrganizationsWithSpontaneousDTO;
import it.gov.pagopa.pu.citizen.mapper.OrganizationsWithSpontaneousDTOMapper;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionTypeOrgWithActiveSpontaneousCount;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrganizationRetrieverServiceImplTest {

  @Mock
  private OrganizationService organizationServiceMock;
  @Mock
  private DebtPositionTypeOrgService debtPositionTypeOrgServiceMock;
  @Mock
  private OrganizationsWithSpontaneousDTOMapper organizationsWithSpontaneousDTOMapperMock;

  private static final PodamFactory podamFactory = TestUtils.getPodamFactory();
  private final Integer maxSize = 1000;

  OrganizationRetrieverService organizationRetrieverService;

  @BeforeEach
  void setUp() {
    organizationRetrieverService = new OrganizationRetrieverServiceImpl(organizationServiceMock, debtPositionTypeOrgServiceMock, organizationsWithSpontaneousDTOMapperMock, maxSize );
  }

  @Test
  void givenBrokerIdWhenGetOrganizationsWithSpontaneousThenReturnMappedResult() {
    // given
    Long brokerId = 1L;
    String accessToken = "ACCESS_TOKEN";
    PageRequest pageable = PageRequest.of(0, maxSize);

    Organization org1 = new Organization(); org1.setOrganizationId(1L);
    Organization org2 = new Organization(); org2.setOrganizationId(2L);
    Organization org3 = new Organization(); org3.setOrganizationId(3L);
    List<Organization> organizations = List.of(org1, org2, org3);

    DebtPositionTypeOrgWithActiveSpontaneousCount debt1 = new DebtPositionTypeOrgWithActiveSpontaneousCount();
    debt1.setOrganizationId(1L);
    DebtPositionTypeOrgWithActiveSpontaneousCount debt2 = new DebtPositionTypeOrgWithActiveSpontaneousCount();
    debt2.setOrganizationId(3L);
    List<DebtPositionTypeOrgWithActiveSpontaneousCount> debtPositions = List.of(debt1, debt2);

    OrganizationsWithSpontaneousDTO dto1 = new OrganizationsWithSpontaneousDTO(); dto1.setOrganizationId(1L);
    OrganizationsWithSpontaneousDTO dto3 = new OrganizationsWithSpontaneousDTO(); dto3.setOrganizationId(3L);
    List<OrganizationsWithSpontaneousDTO> expectedResult = List.of(dto1, dto3);

    Mockito.when(organizationServiceMock.getOrganizationsByBrokerIdAndFilters(
        brokerId, null, null, null, pageable, accessToken))
      .thenReturn(organizations);

    Mockito.when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrgWithActiveSpontaneousCount(
        List.of(1L, 2L, 3L), accessToken))
      .thenReturn(debtPositions);

    Mockito.when(organizationsWithSpontaneousDTOMapperMock.map(Mockito.anyList()))
      .thenReturn(expectedResult);

    // when
    List<OrganizationsWithSpontaneousDTO> result =
      organizationRetrieverService.getOrganizationsWithSpontaneous(brokerId, accessToken);

    // then
    assertNotNull(result);
    assertEquals(2, result.size());
    assertTrue(result.stream().allMatch(dto -> dto.getOrganizationId() == 1L || dto.getOrganizationId() == 3L));
    assertFalse(result.stream().anyMatch(dto -> dto.getOrganizationId() == 2L));
    Mockito.verifyNoMoreInteractions(organizationServiceMock, debtPositionTypeOrgServiceMock, organizationsWithSpontaneousDTOMapperMock);
  }

  @Test
  void givenNoOrganizationsWhenGetOrganizationsWithSpontaneousThenReturnEmptyList() {
    Long brokerId = 1L;
    String accessToken = "ACCESS_TOKEN";
    PageRequest pageable = PageRequest.of(0, maxSize);

    Mockito.when(organizationServiceMock.getOrganizationsByBrokerIdAndFilters(
      brokerId, null, null, null, pageable, accessToken)).thenReturn(Collections.emptyList());

    List<OrganizationsWithSpontaneousDTO> result =
      organizationRetrieverService.getOrganizationsWithSpontaneous(brokerId, accessToken);

    assertNotNull(result);
    assertTrue(result.isEmpty());

  }

  @Test
  void givenOrganizationsButNoMatchingDebtPositionsWhenGetOrganizationsWithSpontaneousThenReturnEmptyList() {
    Long brokerId = 1L;
    String accessToken = "ACCESS_TOKEN";
    PageRequest pageable = PageRequest.of(0, maxSize);

    List<Organization> organizations = podamFactory.manufacturePojo(List.class, Organization.class);
    List<DebtPositionTypeOrgWithActiveSpontaneousCount> debtPositions =
      podamFactory.manufacturePojo(List.class, DebtPositionTypeOrgWithActiveSpontaneousCount.class);
    debtPositions.forEach(d -> d.setOrganizationId(Long.MAX_VALUE));

    Mockito.when(organizationServiceMock.getOrganizationsByBrokerIdAndFilters(
      brokerId, null, null, null, pageable, accessToken)).thenReturn(organizations);
    Mockito.when(debtPositionTypeOrgServiceMock
        .getDebtPositionTypeOrgWithActiveSpontaneousCount(Mockito.anyList(), Mockito.eq(accessToken)))
      .thenReturn(debtPositions);

    List<OrganizationsWithSpontaneousDTO> result =
      organizationRetrieverService.getOrganizationsWithSpontaneous(brokerId, accessToken);

    assertNotNull(result);
    assertTrue(result.isEmpty());

  }

  @Test
  void givenMapperReturnsEmptyListWhenGetOrganizationsWithSpontaneousThenReturnEmptyList() {
    Long brokerId = 1L;
    String accessToken = "ACCESS_TOKEN";
    PageRequest pageable = PageRequest.of(0, maxSize);

    List<Organization> organizations = podamFactory.manufacturePojo(List.class, Organization.class);
    List<DebtPositionTypeOrgWithActiveSpontaneousCount> debtPositions =
      podamFactory.manufacturePojo(List.class, DebtPositionTypeOrgWithActiveSpontaneousCount.class);

    Mockito.when(organizationServiceMock.getOrganizationsByBrokerIdAndFilters(
      brokerId, null, null, null, pageable, accessToken)).thenReturn(organizations);
    Mockito.when(debtPositionTypeOrgServiceMock
        .getDebtPositionTypeOrgWithActiveSpontaneousCount(Mockito.anyList(), Mockito.eq(accessToken)))
      .thenReturn(debtPositions);
    Mockito.when(organizationsWithSpontaneousDTOMapperMock.map(Mockito.anyList()))
      .thenReturn(Collections.emptyList());

    List<OrganizationsWithSpontaneousDTO> result =
      organizationRetrieverService.getOrganizationsWithSpontaneous(brokerId, accessToken);

    assertNotNull(result);
    assertTrue(result.isEmpty());
    Mockito.verifyNoMoreInteractions(organizationServiceMock, debtPositionTypeOrgServiceMock, organizationsWithSpontaneousDTOMapperMock);
  }
}
