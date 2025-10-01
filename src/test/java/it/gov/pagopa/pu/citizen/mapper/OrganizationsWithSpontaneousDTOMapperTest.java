package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.OrganizationsWithSpontaneousDTO;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.CollectionModelDebtPositionTypeOrgWithActiveSpontaneousCount;
import it.gov.pagopa.pu.organization.dto.generated.PagedModelOrganization;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class OrganizationsWithSpontaneousDTOMapperTest {

  private static final PodamFactory podamFactory = TestUtils.getPodamFactory();
  private final OrganizationsWithSpontaneousDTOMapper mapper = Mappers.getMapper(OrganizationsWithSpontaneousDTOMapper.class);

  @Test
  void givenValidPagedModelOrganizationWhenMapThenFieldsAreMappedCorrectly() {
    // given
    PagedModelOrganization pagedModelOrganization = podamFactory.manufacturePojo(PagedModelOrganization.class);
    CollectionModelDebtPositionTypeOrgWithActiveSpontaneousCount collection = podamFactory.manufacturePojo(CollectionModelDebtPositionTypeOrgWithActiveSpontaneousCount.class);

    Long validId = collection.getEmbedded()
      .getDebtPositionTypeOrgWithActiveSpontaneousCounts()
      .getFirst()
      .getOrganizationId();
    pagedModelOrganization.getEmbedded().getOrganizations().getFirst().setOrganizationId(validId);

    // when
    List<OrganizationsWithSpontaneousDTO> result = mapper.map(pagedModelOrganization, collection);

    // then
    assertNotNull(result);

    assertFalse(result.isEmpty());
    assertTrue(result.stream()
      .allMatch(dto -> dto.getOrganizationId().equals(validId)));

    result.forEach(TestUtils::checkNotNullFields);
  }

  @ParameterizedTest
  @MethodSource("invalidPagedModelOrganizationSource")
  void givenInvalidPagedModelOrganization_whenMap_thenContentIsEmpty(PagedModelOrganization pagedModelOrganization,
                                                                     CollectionModelDebtPositionTypeOrgWithActiveSpontaneousCount collection) {
    // when
    List<OrganizationsWithSpontaneousDTO> result = mapper.map(pagedModelOrganization, collection);

    // then
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  static Stream<Arguments> invalidPagedModelOrganizationSource() {
    PagedModelOrganization case1 = podamFactory.manufacturePojo(PagedModelOrganization.class);
    CollectionModelDebtPositionTypeOrgWithActiveSpontaneousCount coll1 = null;

    PagedModelOrganization case2 = podamFactory.manufacturePojo(PagedModelOrganization.class);
    case2.setEmbedded(null);
    CollectionModelDebtPositionTypeOrgWithActiveSpontaneousCount coll2 =
      podamFactory.manufacturePojo(CollectionModelDebtPositionTypeOrgWithActiveSpontaneousCount.class);

    PagedModelOrganization case3 = podamFactory.manufacturePojo(PagedModelOrganization.class);
    CollectionModelDebtPositionTypeOrgWithActiveSpontaneousCount coll3 =
      podamFactory.manufacturePojo(CollectionModelDebtPositionTypeOrgWithActiveSpontaneousCount.class);
    case3.getEmbedded().getOrganizations()
      .forEach(org -> org.setOrganizationId(Long.MAX_VALUE));

    return Stream.of(
      Arguments.of(case1, coll1),
      Arguments.of(case2, coll2),
      Arguments.of(case3, coll3)
    );
  }

}

