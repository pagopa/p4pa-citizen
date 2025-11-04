package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.DebtorReceiptDTO;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorReceiptsDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.PagedModelReceiptNoPIIView;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptNoPIIView;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PagedDebtorReceiptsDTOMapper{

  @Mapping(target = "content", expression = "java(source.getEmbedded() != null ? mapReceiptNoPIIViewWithOrganization(organizations, source.getEmbedded().getReceiptNoPIIViews()) : java.util.Collections.emptyList())")
  @Mapping(target = "totalPages", source = "source.page.totalPages")
  @Mapping(target = "size", source = "source.page.size")
  @Mapping(target = "number", source = "source.page.number")
  @Mapping(target = "totalElements", source = "source.page.totalElements")
  PagedDebtorReceiptsDTO map(List<Organization> organizations, PagedModelReceiptNoPIIView source);

  @Mapping(target = "organizationId", source = "organization.organizationId")
  @Mapping(target = "orgName", source = "organization.orgName")
  @Mapping(target = "orgFiscalCode", source = "organization.orgFiscalCode")
  DebtorReceiptDTO map(Organization organization, ReceiptNoPIIView receiptNoPIIView);

  default List<DebtorReceiptDTO> mapReceiptNoPIIViewWithOrganization(List<Organization> organizations, List<ReceiptNoPIIView> receiptNoPIIViewList){
    return receiptNoPIIViewList.stream()
      .map(personalReceiptNoPIIView -> map(retrieveOrganization(organizations, personalReceiptNoPIIView), personalReceiptNoPIIView)).toList();
  }

  default Organization retrieveOrganization(List<Organization> organizations, ReceiptNoPIIView receiptNoPIIView){
    return organizations.stream()
      .filter(org -> org.getOrgFiscalCode().equals(receiptNoPIIView.getOrgFiscalCode()))
      .findAny()
      .orElseThrow(() -> new IllegalStateException("No Organization found for receipt with Id %d".formatted(receiptNoPIIView.getReceiptId())));
  }

}
