package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.OrganizationsWithSpontaneousDTO;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrganizationsWithSpontaneousDTOMapper {

  List<OrganizationsWithSpontaneousDTO> map(List<Organization> organizations);

}
