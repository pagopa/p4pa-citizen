package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.BrokerInfoDTO;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.co.jemos.podam.api.PodamFactory;

import static org.junit.jupiter.api.Assertions.*;

class BrokerInfoDTOMapperTest {

  BrokerInfoDTOMapper mapper = Mappers.getMapper(BrokerInfoDTOMapper.class);
  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @Test
  void whenMapFromOrganizationThenOk() {
    Organization organization = podamFactory.manufacturePojo(Organization.class);

    BrokerInfoDTO result = mapper.mapFromOrganization(organization);

    assertNotNull(result);
    TestUtils.checkNotNullFields(result);
    assertEquals(organization.getOrgName(), result.getBrokerName());
    assertEquals(organization.getOrgFiscalCode(), result.getBrokerFiscalCode());
    assertEquals(organization.getOrgLogo(), result.getBrokerLogo());
  }

  @Test
  void givenNullOrganizationWhenMapFromOrganizationThenNull() {

    BrokerInfoDTO result = mapper.mapFromOrganization(null);

    assertNull(result);
  }
}
