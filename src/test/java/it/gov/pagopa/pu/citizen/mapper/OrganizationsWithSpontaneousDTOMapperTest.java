package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.OrganizationsWithSpontaneousDTO;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrganizationsWithSpontaneousDTOMapperTest {

  private static final PodamFactory podamFactory = TestUtils.getPodamFactory();
  private final OrganizationsWithSpontaneousDTOMapper mapper = Mappers.getMapper(OrganizationsWithSpontaneousDTOMapper.class);

  @Test
  void givenValidOrganizationsListWhenMapThenFieldsAreMappedCorrectly() {
    // given
    List<Organization> organizationsList = podamFactory.manufacturePojo(List.class, Organization.class);

    // when
    List<OrganizationsWithSpontaneousDTO> result = mapper.map(organizationsList);

    // then
    assertNotNull(result);
    assertFalse(result.isEmpty());
    result.forEach(TestUtils::checkNotNullFields);
  }
}

