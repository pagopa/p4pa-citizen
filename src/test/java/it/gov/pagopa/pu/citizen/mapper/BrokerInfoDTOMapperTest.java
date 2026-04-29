package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.BrokerInfoDTO;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.organization.dto.generated.BrokerConfiguration;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.co.jemos.podam.api.PodamFactory;

import static org.junit.jupiter.api.Assertions.*;

class BrokerInfoDTOMapperTest {

  BrokerInfoDTOMapper mapper = Mappers.getMapper(BrokerInfoDTOMapper.class);
  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @Test
  void whenMapThenOk() {
    Organization organization = podamFactory.manufacturePojo(Organization.class);
    String arpuConfig = "arpuConfig";
    String favicon = "favicon";
    BrokerConfiguration brokerConfiguration = new BrokerConfiguration();
    brokerConfiguration.setArpuConfig(arpuConfig);
    brokerConfiguration.setFavicon(favicon);

    BrokerInfoDTO result = mapper.map(organization, "externalId", brokerConfiguration);

    assertNotNull(result);
    TestUtils.checkNotNullFields(result);
    assertEquals(organization.getOrgName(), result.getBrokerName());
    assertEquals(organization.getOrgFiscalCode(), result.getBrokerFiscalCode());
    assertEquals(organization.getOrgLogo(), result.getBrokerLogo());
    assertEquals(arpuConfig, result.getConfig());
    assertEquals("externalId", result.getExternalId());
    assertEquals(organization.getAddress(), result.getAddress());
    assertEquals(organization.getZipCode(), result.getZipCode());
    assertEquals(organization.getCity(), result.getCity());
    assertEquals(favicon, result.getFavicon());
  }

  @Test
  void givenNullOrganizationWhenMapThenNull() {
    BrokerInfoDTO result = mapper.map(null,"externalId", null);

    assertNull(result);
  }

  @Test
  void givenBrokerConfigAndNoOrganizationWhenMapThenNull() {
    BrokerConfiguration brokerConfiguration = new BrokerConfiguration();
    BrokerInfoDTO result = mapper.map(null, "externalId", brokerConfiguration);

    assertNull(result);
  }

  @Test
  void givenOrganizationAndNoBrokerConfigWhenMapThenOk() {
    Organization organization = podamFactory.manufacturePojo(Organization.class);

    BrokerInfoDTO result = mapper.map(organization, "externalId" , null);

    assertNotNull(result);
    TestUtils.checkNotNullFields(result, "config", "favicon");
    assertEquals(organization.getOrgName(), result.getBrokerName());
    assertEquals(organization.getOrgFiscalCode(), result.getBrokerFiscalCode());
    assertEquals(organization.getOrgLogo(), result.getBrokerLogo());
    assertNull(result.getConfig());
    assertEquals("externalId", result.getExternalId());
    assertEquals(organization.getAddress(), result.getAddress());
    assertEquals(organization.getZipCode(), result.getZipCode());
    assertEquals(organization.getCity(), result.getCity());
  }
}
