package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.ReceiptDetailExtendedDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptDetailDTO;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReceiptDetailExtendedMapper {

  @Mapping(target = "organizationId", source = "organization.organizationId")
  @Mapping(target = "orgName", source = "organization.orgName")
  @Mapping(target = "orgFiscalCode", source = "organization.orgFiscalCode")
  ReceiptDetailExtendedDTO map(Organization organization, ReceiptDetailDTO receipt);
}
