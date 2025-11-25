package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.BrokerInfoDTO;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BrokerInfoDTOMapper {

  @Mapping(target = "brokerLogo", source = "orgLogo")
  @Mapping(target = "brokerName", source = "orgName")
  @Mapping(target = "brokerFiscalCode", source = "orgFiscalCode")
  BrokerInfoDTO mapFromOrganization(Organization organization);
}
