package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.OrganizationLogoDTO;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.co.jemos.podam.api.PodamFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrganizationLogoDTOMapperTest {

  private static final PodamFactory podamFactory = TestUtils.getPodamFactory();
  private final OrganizationLogoDTOMapper mapper = Mappers.getMapper(OrganizationLogoDTOMapper.class);

  @Test
  void whenMapThenOk() {
    // given
    Organization organization = podamFactory.manufacturePojo(Organization.class);

    // when
    OrganizationLogoDTO result = mapper.map(organization);

    // then
    assertNotNull(result);
    Assertions.assertEquals(organization.getOrgLogo(),result.getOrgLogo());
    TestUtils.checkNotNullFields(result);
  }
}

