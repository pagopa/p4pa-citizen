package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.DebtorReceiptDTO;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorReceiptsDTO;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.PagedModelReceiptNoPIIView;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptNoPIIView;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class PagedDebtorReceiptsDTOMapperTest {

  private final PagedDebtorReceiptsDTOMapper mapper = Mappers.getMapper(PagedDebtorReceiptsDTOMapper.class);
  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @Test
  void givenOrganizationsMapAndPageWhenMapThenReturnPagedDebtorReceiptDTO() {
    // given
    List<Organization> organizations = podamFactory.manufacturePojo(List.class, Organization.class);
    Map<String, Organization> organizationsMap = organizations.stream()
      .collect(Collectors.toMap(Organization::getOrgFiscalCode, org -> org));

    ReceiptNoPIIView receipt1 = podamFactory.manufacturePojo(ReceiptNoPIIView.class);
    ReceiptNoPIIView receipt2 = podamFactory.manufacturePojo(ReceiptNoPIIView.class);

    receipt1.setOrgFiscalCode(organizations.get(0).getOrgFiscalCode());
    receipt2.setOrgFiscalCode(organizations.get(1).getOrgFiscalCode());

    List<ReceiptNoPIIView> content = List.of(receipt1, receipt2);

    PagedModelReceiptNoPIIView pagedModel = podamFactory.manufacturePojo(PagedModelReceiptNoPIIView.class);
    pagedModel.getEmbedded().setReceiptNoPIIViews(content);

    // when
    PagedDebtorReceiptsDTO result = mapper.map(organizationsMap, pagedModel);

    // then
    assertNotNull(result);
    assertEquals(2, result.getContent().size());
    assertEquals(pagedModel.getPage().getTotalPages(), result.getTotalPages());
    assertEquals(pagedModel.getPage().getSize(), result.getSize());
    assertEquals(pagedModel.getPage().getNumber(), result.getNumber());
    assertEquals(pagedModel.getPage().getTotalElements(), result.getTotalElements());

    DebtorReceiptDTO mappedReceipt = result.getContent().getFirst();
    Organization expectedOrg = organizationsMap.get(mappedReceipt.getOrgFiscalCode());

    assertEquals(expectedOrg.getOrganizationId(), mappedReceipt.getOrganizationId());
    assertEquals(expectedOrg.getOrgFiscalCode(), mappedReceipt.getOrgFiscalCode());
    assertEquals(expectedOrg.getOrgName(), mappedReceipt.getOrgName());

    TestUtils.checkNotNullFields(result);
  }

  @Test
  void givenOrganizationAndReceiptWhenMapThenReturnMappedDebtorReceiptDTO() {
    // given
    Organization organization = podamFactory.manufacturePojo(Organization.class);
    ReceiptNoPIIView receipt = podamFactory.manufacturePojo(ReceiptNoPIIView.class);

    // when
    DebtorReceiptDTO result = mapper.map(organization, receipt);

    // then
    assertNotNull(result);
    assertEquals(organization.getOrganizationId(), result.getOrganizationId());
    assertEquals(organization.getOrgFiscalCode(), result.getOrgFiscalCode());
    assertEquals(organization.getOrgName(), result.getOrgName());
    TestUtils.checkNotNullFields(result);
  }

  @Test
  void givenOrganizationsMapAndListWhenMapReceiptNoPIIViewWithOrganizationThenReturnListMapped() {
    // given
    List<Organization> organizations = podamFactory.manufacturePojo(List.class, Organization.class);
    Map<String, Organization> organizationsMap = organizations.stream()
      .collect(Collectors.toMap(Organization::getOrgFiscalCode, org -> org));

    ReceiptNoPIIView receipt = podamFactory.manufacturePojo(ReceiptNoPIIView.class);
    receipt.setOrgFiscalCode(organizations.getFirst().getOrgFiscalCode());

    List<ReceiptNoPIIView> receipts = List.of(receipt);

    // when
    List<DebtorReceiptDTO> result = mapper.mapReceiptNoPIIViewWithOrganization(organizationsMap, receipts);

    // then
    assertEquals(1, result.size());
    assertEquals(organizations.getFirst().getOrgFiscalCode(), result.getFirst().getOrgFiscalCode());
    result.forEach(TestUtils::checkNotNullFields);
  }

  @Test
  void givenOrgNotFoundWhenRetrieveOrganizationThenThrowException() {
    // given
    List<Organization> organizations = podamFactory.manufacturePojo(List.class, Organization.class);
    Map<String, Organization> organizationsMap = organizations.stream()
      .collect(Collectors.toMap(Organization::getOrgFiscalCode, org -> org));

    ReceiptNoPIIView receipt = podamFactory.manufacturePojo(ReceiptNoPIIView.class);
    receipt.setOrgFiscalCode("NOT_EXISTING");

    // then
    assertThrows(
      IllegalStateException.class,
      () -> mapper.retrieveOrganization(organizationsMap, receipt)
    );
  }
}
