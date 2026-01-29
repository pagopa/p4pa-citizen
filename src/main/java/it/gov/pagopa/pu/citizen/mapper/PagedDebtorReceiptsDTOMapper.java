package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.DebtorReceiptDTO;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorReceiptsDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.PagedModelReceiptNoPIIView;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptNoPIIView;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Mapper(componentModel = "spring")
public interface PagedDebtorReceiptsDTOMapper{

  @Mapping(target = "content", expression = "java(source.getEmbedded() != null ? mapReceiptNoPIIViewWithOrganization(organizationsMap, source.getEmbedded().getReceiptNoPIIViews()) : java.util.Collections.emptyList())")
  @Mapping(target = "totalPages", source = "source.page.totalPages")
  @Mapping(target = "size", source = "source.page.size")
  @Mapping(target = "number", source = "source.page.number")
  @Mapping(target = "totalElements", source = "source.page.totalElements")
  PagedDebtorReceiptsDTO map(Map<String, Organization> organizationsMap, PagedModelReceiptNoPIIView source);

  @Mapping(target = "organizationId", source = "organization.organizationId")
  @Mapping(target = "orgName", source = "organization.orgName")
  @Mapping(target = "orgFiscalCode", source = "organization.orgFiscalCode")
  DebtorReceiptDTO map(Organization organization, ReceiptNoPIIView receiptNoPIIView);

  default List<DebtorReceiptDTO> mapReceiptNoPIIViewWithOrganization(Map<String, Organization> organizationsMap, List<ReceiptNoPIIView> receiptNoPIIViewList){
    return receiptNoPIIViewList.stream()
      .map(receiptNoPIIViewToDebtorReceiptDTOFunction(organizationsMap)).toList();
  }

  private Function<ReceiptNoPIIView, DebtorReceiptDTO> receiptNoPIIViewToDebtorReceiptDTOFunction(Map<String, Organization> organizationsMap) {
    return receiptNoPIIView -> map(retrieveOrganization(organizationsMap, receiptNoPIIView), receiptNoPIIView);
  }

  default List<DebtorReceiptDTO> sortedMapReceiptNoPIIViewWithOrganization(Map<String, Organization> organizationsMap, List<ReceiptNoPIIView> receiptNoPIIViewList){
    if(CollectionUtils.isEmpty(receiptNoPIIViewList)){
      return Collections.emptyList();
    }
    return receiptNoPIIViewList.stream()
        .sorted(Comparator.comparing(ReceiptNoPIIView::getPaymentDateTime))
        .map(receiptNoPIIViewToDebtorReceiptDTOFunction(organizationsMap))
        .toList();
  }

  default Organization retrieveOrganization(Map<String, Organization> organizationsMap, ReceiptNoPIIView receiptNoPIIView){
    Organization organization = organizationsMap.get(receiptNoPIIView.getOrgFiscalCode());
    if (organization == null){
      throw new IllegalStateException("[ORGANIZATION_NOT_FOUND] No Organization found for receipt with Id %d".formatted(receiptNoPIIView.getReceiptId()));
    }
   return organization;
  }

}
