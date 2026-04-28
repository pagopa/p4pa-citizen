package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.BrokerInfoDTO;
import it.gov.pagopa.pu.organization.dto.generated.BrokerConfiguration;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BrokerInfoDTOMapper {

  @Mapping(target = "brokerId", source = "organization.brokerId")
  @Mapping(target = "brokerLogo", source = "organization.orgLogo")
  @Mapping(target = "brokerName", source = "organization.orgName")
  @Mapping(target = "brokerFiscalCode", source = "organization.orgFiscalCode")
  @Mapping(target = "config", source = "brokerConfiguration.arpuConfig")
  BrokerInfoDTO innerMap(Organization organization, String externalId, BrokerConfiguration brokerConfiguration);

  default BrokerInfoDTO map(Organization organization, String externalId, BrokerConfiguration brokerConfiguration) {
    if (organization == null) {
      return null;
    }
    return innerMap(organization, externalId, brokerConfiguration);
  }
}
