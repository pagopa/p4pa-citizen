package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.OrganizationLogoDTO;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrganizationLogoDTOMapper {
  OrganizationLogoDTO map(Organization organization);
}
