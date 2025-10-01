package it.gov.pagopa.pu.citizen.service.organization;

import it.gov.pagopa.pu.citizen.connector.debtpositions.DebtPositionTypeOrgService;
import it.gov.pagopa.pu.citizen.connector.organization.OrganizationService;
import it.gov.pagopa.pu.citizen.dto.generated.OrganizationsWithSpontaneousDTO;
import it.gov.pagopa.pu.citizen.exception.ResourceNotFoundException;
import it.gov.pagopa.pu.citizen.mapper.OrganizationsWithSpontaneousDTOMapper;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.CollectionModelDebtPositionTypeOrgWithActiveSpontaneousCount;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.organization.dto.generated.PagedModelOrganization;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.List;
import java.util.stream.Stream;

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

  @AfterEach
  void mockitoVerify(){
    Mockito.verifyNoMoreInteractions(organizationServiceMock, debtPositionTypeOrgServiceMock, organizationsWithSpontaneousDTOMapperMock);
  }

  @Test
  void givenBrokerIdWhenGetOrganizationsListWithSpontaneousThenReturnPagedOrganizationsListWithSpontaneousDTO() {
    //given
    Long brokerId = 1L;
    String accessToken = "ACCESS_TOKEN";
    PageRequest pageable = PageRequest.of(0, maxSize);
    PagedModelOrganization pagedModelOrganization = podamFactory.manufacturePojo(PagedModelOrganization.class);

    CollectionModelDebtPositionTypeOrgWithActiveSpontaneousCount collectionModelDebtPositionTypeOrgWithActiveSpontaneousCount = podamFactory.manufacturePojo(CollectionModelDebtPositionTypeOrgWithActiveSpontaneousCount.class);
    List<OrganizationsWithSpontaneousDTO> expectedResult = podamFactory.manufacturePojo(List.class, OrganizationsWithSpontaneousDTO.class);
    List<Long> organizationIds = pagedModelOrganization.getEmbedded().getOrganizations().stream().map(Organization::getOrganizationId).toList();

    Mockito.when(organizationServiceMock.getOrganizationsByBrokerIdAndFilters(brokerId, null, null,  null, pageable, accessToken)).thenReturn(pagedModelOrganization);
    Mockito.when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrgWithActiveSpontaneousCount(organizationIds, accessToken)).thenReturn(collectionModelDebtPositionTypeOrgWithActiveSpontaneousCount);
    Mockito.when(organizationsWithSpontaneousDTOMapperMock.map(pagedModelOrganization, collectionModelDebtPositionTypeOrgWithActiveSpontaneousCount)).thenReturn(expectedResult);
    //when
    List<OrganizationsWithSpontaneousDTO> result = organizationRetrieverService.getOrganizationsListWithSpontaneous(brokerId, accessToken);
    //then
    assertNotNull(result);
    assertEquals(expectedResult, result);
  }

  @ParameterizedTest
  @MethodSource("pagedModelOrganizationSource")
  void givenNullPagedModelOrganizationWhenGetOrganizationsListWithSpontaneousThenThrowException(PagedModelOrganization pagedModelOrganization) {
    Long brokerId = 1L;
    String accessToken = "ACCESS_TOKEN";
    PageRequest pageable = PageRequest.of(0, 1000);
    Mockito.when(organizationServiceMock.getOrganizationsByBrokerIdAndFilters(brokerId, null, null,null, pageable, accessToken)).thenReturn(pagedModelOrganization);

    ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> organizationRetrieverService.getOrganizationsListWithSpontaneous(brokerId, accessToken));
    assertEquals("No organizations found for broker with id 1", ex.getMessage());
  }

  static Stream<Arguments> pagedModelOrganizationSource() {

    PagedModelOrganization case1 = null;

    PagedModelOrganization case2 = podamFactory.manufacturePojo(PagedModelOrganization.class);
    case2.setEmbedded(null);

    PagedModelOrganization case3 = podamFactory.manufacturePojo(PagedModelOrganization.class);
    case3.getEmbedded().setOrganizations(null);

    return Stream.of(
      Arguments.of(case1),
      Arguments.of(case2),
      Arguments.of(case3)
    );
  }


}
