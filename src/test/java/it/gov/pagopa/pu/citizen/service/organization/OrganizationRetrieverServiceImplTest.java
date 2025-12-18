package it.gov.pagopa.pu.citizen.service.organization;

import it.gov.pagopa.pu.citizen.connector.debtpositions.DebtPositionTypeOrgService;
import it.gov.pagopa.pu.citizen.connector.organization.OrganizationService;
import it.gov.pagopa.pu.citizen.dto.generated.OrganizationsWithSpontaneousDTO;
import it.gov.pagopa.pu.citizen.exception.ResourceNotFoundException;
import it.gov.pagopa.pu.citizen.mapper.OrganizationsWithSpontaneousDTOMapper;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionTypeOrgWithActiveSpontaneousCount;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrganizationRetrieverServiceImplTest {

  @Mock
  private BrokerOrganizationsRetrieverService brokerOrganizationsRetrieverServiceMock;
  @Mock
  private DebtPositionTypeOrgService debtPositionTypeOrgServiceMock;
  @Mock
  private OrganizationsWithSpontaneousDTOMapper organizationsWithSpontaneousDTOMapperMock;
  @Mock
  private OrganizationService organizationServiceMock;

  private static final PodamFactory podamFactory = TestUtils.getPodamFactory();

  OrganizationRetrieverService organizationRetrieverService;

  @BeforeEach
  void setUp() {
    organizationRetrieverService = new OrganizationRetrieverServiceImpl(brokerOrganizationsRetrieverServiceMock, debtPositionTypeOrgServiceMock, organizationsWithSpontaneousDTOMapperMock, organizationServiceMock);
  }

  @AfterEach
  void mockitoVerify(){
    Mockito.verifyNoMoreInteractions(brokerOrganizationsRetrieverServiceMock, debtPositionTypeOrgServiceMock, organizationsWithSpontaneousDTOMapperMock, organizationServiceMock);
  }

  @Test
  void givenBrokerIdWhenGetOrganizationsWithSpontaneousThenReturnMappedResult() {
    // given
    Long brokerId = 1L;
    String accessToken = "ACCESS_TOKEN";

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

    Mockito.when(brokerOrganizationsRetrieverServiceMock.getAllActiveOrganizationsByBrokerId(
        brokerId, accessToken))
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
    Mockito.verifyNoMoreInteractions(brokerOrganizationsRetrieverServiceMock, debtPositionTypeOrgServiceMock, organizationsWithSpontaneousDTOMapperMock);
  }

  @Test
  void givenNoOrganizationsWhenGetOrganizationsWithSpontaneousThenReturnEmptyList() {
    Long brokerId = 1L;
    String accessToken = "ACCESS_TOKEN";

    Mockito.when(brokerOrganizationsRetrieverServiceMock.getAllActiveOrganizationsByBrokerId(
      brokerId, accessToken)).thenReturn(Collections.emptyList());

    List<OrganizationsWithSpontaneousDTO> result =
      organizationRetrieverService.getOrganizationsWithSpontaneous(brokerId, accessToken);

    assertNotNull(result);
    assertTrue(result.isEmpty());
    Mockito.verifyNoInteractions(debtPositionTypeOrgServiceMock);
  }

  @Test
  void givenOrganizationsButNoMatchingDebtPositionsWhenGetOrganizationsWithSpontaneousThenReturnEmptyList() {
    Long brokerId = 1L;
    String accessToken = "ACCESS_TOKEN";

    List<Organization> organizations = podamFactory.manufacturePojo(List.class, Organization.class);
    List<DebtPositionTypeOrgWithActiveSpontaneousCount> debtPositions =
      podamFactory.manufacturePojo(List.class, DebtPositionTypeOrgWithActiveSpontaneousCount.class);
    debtPositions.forEach(d -> d.setOrganizationId(Long.MAX_VALUE));

    Mockito.when(brokerOrganizationsRetrieverServiceMock.getAllActiveOrganizationsByBrokerId(
      brokerId, accessToken)).thenReturn(organizations);
    Mockito.when(debtPositionTypeOrgServiceMock
        .getDebtPositionTypeOrgWithActiveSpontaneousCount(Mockito.anyList(), Mockito.eq(accessToken)))
      .thenReturn(debtPositions);
    Mockito.when(organizationsWithSpontaneousDTOMapperMock.map(Mockito.anyList())).thenReturn(Collections.emptyList());

    List<OrganizationsWithSpontaneousDTO> result =
      organizationRetrieverService.getOrganizationsWithSpontaneous(brokerId, accessToken);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void whenValidateOrganizationThenOk(){
    String accessToken = "ACCESS_TOKEN";
    long brokerId = 1L;
    long organizationId = 2L;

    Organization organization = podamFactory.manufacturePojo(Organization.class);
    organization.setBrokerId(brokerId);
    organization.setOrganizationId(organizationId);

    Mockito.when(organizationServiceMock.getOrganizationByOrganizationId(organizationId,accessToken)).thenReturn(organization);

    Assertions.assertDoesNotThrow(() -> organizationRetrieverService.validateOrganization(organizationId,brokerId,accessToken));
  }

  @Test
  void givenNoOrganizationWhenValidateOrganizationThenResourceNotFoundException(){
    String accessToken = "ACCESS_TOKEN";
    long brokerId = 1L;
    long organizationId = 2L;

    Mockito.when(organizationServiceMock.getOrganizationByOrganizationId(organizationId,accessToken)).thenReturn(null);

    Assertions.assertThrows(ResourceNotFoundException.class,() -> organizationRetrieverService.validateOrganization(organizationId,brokerId,accessToken));
  }

  @Test
  void givenNoMatchingBrokerIdWhenValidateOrganizationThenResourceNotFoundException(){
    String accessToken = "ACCESS_TOKEN";
    long brokerId = 1L;
    long organizationId = 2L;

    Organization organization = podamFactory.manufacturePojo(Organization.class);
    organization.setBrokerId(brokerId+1);
    organization.setOrganizationId(organizationId);

    Mockito.when(organizationServiceMock.getOrganizationByOrganizationId(organizationId,accessToken)).thenReturn(organization);

    Assertions.assertThrows(ResourceNotFoundException.class,() -> organizationRetrieverService.validateOrganization(organizationId,brokerId,accessToken));
  }

  @Test
  void whenGetValidOrganizationThenOk(){
    String accessToken = "ACCESS_TOKEN";
    long brokerId = 1L;
    long organizationId = 2L;

    Organization organization = podamFactory.manufacturePojo(Organization.class);
    organization.setBrokerId(brokerId);
    organization.setOrganizationId(organizationId);

    Mockito.when(organizationServiceMock.getOrganizationByOrganizationId(organizationId,accessToken)).thenReturn(organization);

    Organization result = organizationRetrieverService.getValidOrganization(organizationId, brokerId, accessToken);

    assertEquals(organization, result);
  }

  @Test
  void givenValidOrgFiscalCodeWhenGetValidOrganizationThenOk(){
    String accessToken = "ACCESS_TOKEN";
    long brokerId = 1L;
    String orgFiscalCode = "orgFiscalCode";

    Organization organization = podamFactory.manufacturePojo(Organization.class);
    organization.setBrokerId(brokerId);

    Mockito.when(organizationServiceMock.findByOrgFiscalCode(orgFiscalCode,accessToken)).thenReturn(organization);

    Organization result = organizationRetrieverService.getValidOrganization(orgFiscalCode,brokerId,accessToken);

    assertEquals(organization, result);
  }

  @Test
  void givenNoOrganizationWhenGetValidOrganizationThenResourceNotFoundException(){
    String accessToken = "ACCESS_TOKEN";
    long brokerId = 1L;
    String orgFiscalCode = "orgFiscalCode";

    Mockito.when(organizationServiceMock.findByOrgFiscalCode(orgFiscalCode,accessToken)).thenReturn(null);

    Assertions.assertThrows(ResourceNotFoundException.class,() -> organizationRetrieverService.getValidOrganization(orgFiscalCode,brokerId,accessToken));
  }

  @Test
  void givenNoMatchingBrokerIdWhenGetValidOrganizationThenResourceNotFoundException(){
    String accessToken = "ACCESS_TOKEN";
    long brokerId = 1L;
    String orgFiscalCode = "orgFiscalCode";

    Organization organization = podamFactory.manufacturePojo(Organization.class);
    organization.setBrokerId(brokerId+1);

    Mockito.when(organizationServiceMock.findByOrgFiscalCode(orgFiscalCode,accessToken)).thenReturn(organization);

    Assertions.assertThrows(ResourceNotFoundException.class,() -> organizationRetrieverService.getValidOrganization(orgFiscalCode,brokerId,accessToken));
  }
}
