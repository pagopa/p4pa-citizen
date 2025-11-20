package it.gov.pagopa.pu.citizen.mapper;

import static org.junit.jupiter.api.Assertions.*;

import it.gov.pagopa.pu.citizen.dto.generated.DebtorDebtPositionDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtorPaymentOptionDTO;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorDebtPositionDTO;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.*;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.co.jemos.podam.api.PodamFactory;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

class PagedDebtorDebtPositionMapperTest {

  private final PagedDebtorDebtPositionMapper mapper = Mappers.getMapper(PagedDebtorDebtPositionMapper.class);
  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @Test
  void givenPagedModelWhenMapThenReturnPagedDebtorDebtPositionDTO() {
    // given
    List<Organization> organizations = podamFactory.manufacturePojo(List.class, Organization.class);
    Map<Long, Organization> organizationsMap = organizations.stream()
      .collect(Collectors.toMap(Organization::getOrganizationId, org -> org));

    DebtPositionView dp1 = podamFactory.manufacturePojo(DebtPositionView.class);
    DebtPositionView dp2 = podamFactory.manufacturePojo(DebtPositionView.class);

    dp1.setOrganizationId(organizations.get(0).getOrganizationId());
    dp2.setOrganizationId(organizations.get(1).getOrganizationId());

    List<DebtPositionView> debtPositions = List.of(dp1, dp2);

    Map<Long, List<PaymentOption>> paymentOptionsMap = new HashMap<>();
    Map<Long, List<InstallmentNoPII>> installmentsMap = new HashMap<>();
    paymentOptionsMap.put(dp1.getDebtPositionId(), podamFactory.manufacturePojo(List.class, PaymentOption.class));
    paymentOptionsMap.put(dp2.getDebtPositionId(), podamFactory.manufacturePojo(List.class, PaymentOption.class));
    installmentsMap.put(dp1.getDebtPositionId(), podamFactory.manufacturePojo(List.class, InstallmentNoPII.class));
    installmentsMap.put(dp2.getDebtPositionId(), podamFactory.manufacturePojo(List.class, InstallmentNoPII.class));

    PagedModelDebtPositionView page = podamFactory.manufacturePojo(PagedModelDebtPositionView.class);
    page.getEmbedded().setDebtPositionViews(debtPositions);

    // when
    PagedDebtorDebtPositionDTO result = mapper.map(organizationsMap, page, paymentOptionsMap, installmentsMap);

    // then
    assertNotNull(result);
    assertEquals(2, result.getContent().size());
    assertEquals(page.getPage().getTotalPages(), result.getTotalPages());
    assertEquals(page.getPage().getSize(), result.getSize());
    assertEquals(page.getPage().getNumber(), result.getNumber());
    assertEquals(page.getPage().getTotalElements(), result.getTotalElements());

    TestUtils.checkNotNullFields(result);
  }

  @Test
  void givenOrganizationDebtPositionAndPaymentDataWhenMapThenReturnDebtorDebtPositionDTO() {
    // given
    Organization org = podamFactory.manufacturePojo(Organization.class);
    DebtPositionView dp = podamFactory.manufacturePojo(DebtPositionView.class);

    List<PaymentOption> paymentOptions = podamFactory.manufacturePojo(List.class, PaymentOption.class);
    List<InstallmentNoPII> installments = podamFactory.manufacturePojo(List.class, InstallmentNoPII.class);

    // when
    DebtorDebtPositionDTO result = mapper.map(org, dp, paymentOptions, installments);

    // then
    assertNotNull(result);
    assertEquals(org.getOrganizationId(), result.getOrganizationId());
    assertEquals(org.getOrgName(), result.getOrgName());
    assertEquals(org.getOrgFiscalCode(), result.getOrgFiscalCode());

    result.getPaymentOptions().forEach(TestUtils::checkNotNullFields);
    TestUtils.checkNotNullFields(result);
  }

  @Test
  void givenPaymentOptionsWhenMapThenReturnMappedDTOList() {
    // given
    List<PaymentOption> paymentOptions = podamFactory.manufacturePojo(List.class, PaymentOption.class);
    List<InstallmentNoPII> installments = podamFactory.manufacturePojo(List.class, InstallmentNoPII.class);

    // when
    List<DebtorPaymentOptionDTO> result = mapper.mapPaymentOptions(paymentOptions, installments);

    // then
    assertEquals(paymentOptions.size(), result.size());
    result.forEach(TestUtils::checkNotNullFields);
  }

  @Test
  void givenOrganizationNotFoundWhenRetrieveOrganizationThenThrow() {
    // given
    Map<Long, Organization> orgMap = new HashMap<>();
    DebtPositionView dp = podamFactory.manufacturePojo(DebtPositionView.class);

    // then
    assertThrows(IllegalStateException.class,
      () -> mapper.retrieveOrganization(orgMap, dp));
  }

  @Test
  void givenPaymentOptionsNotFoundWhenRetrievePaymentOptionsThenThrow() {
    // given
    Map<Long, List<PaymentOption>> poMap = new HashMap<>();
    DebtPositionView dp = podamFactory.manufacturePojo(DebtPositionView.class);

    // then
    assertThrows(IllegalStateException.class,
      () -> mapper.retrievePaymentOptions(poMap, dp));
  }

  @Test
  void givenInstallmentsNotFoundWhenRetrieveInstallmentsThenThrow() {
    // given
    Map<Long, List<InstallmentNoPII>> instMap = new HashMap<>();
    DebtPositionView dp = podamFactory.manufacturePojo(DebtPositionView.class);

    // then
    assertThrows(IllegalStateException.class,
      () -> mapper.retrieveInstallments(instMap, dp));
  }

  @Test
  void givenInstallmentsWhenCalculateTotalAmountThenSumCorrectly() {
    // given
    InstallmentNoPII i1 = new InstallmentNoPII();
    InstallmentNoPII i2 = new InstallmentNoPII();
    i1.setAmountCents(100L);
    i2.setAmountCents(300L);
    List<InstallmentNoPII> inst = List.of(i1, i2);

    PaymentOption po = new PaymentOption();
    po.setTotalAmountCents(400L);

    // when
    Long result = mapper.calculateTotalAmountCents(List.of(po), inst);

    // then
    assertEquals(400L, result);
  }

  @Test
  void givenNullPaymentOptionWhenCalculateTotalAmountThenReturnNull() {
    Long result = mapper.calculateTotalAmountCents(null, null);

    assertNull(result);
  }

  @Test
  void givenMultiplePaymentOptionsWithDifferentAmountsThenReturnNull() {
    PaymentOption p1 = new PaymentOption();
    PaymentOption p2 = new PaymentOption();
    p1.setTotalAmountCents(100L);
    p2.setTotalAmountCents(200L);

    Long result = mapper.calculateTotalAmountCents(List.of(p1, p2), List.of());

    assertNull(result);
  }

  @Test
  void givenInstallmentsWhenCalculateDueDateThenReturnEarliestOne() {
    InstallmentNoPII i1 = new InstallmentNoPII();
    InstallmentNoPII i2 = new InstallmentNoPII();
    i1.setDueDate(LocalDate.parse("2025-01-10"));
    i2.setDueDate(LocalDate.parse("2025-01-05"));

    LocalDate result = mapper.calculateDueDate(List.of(), List.of(i1, i2));

    assertEquals(LocalDate.parse("2025-01-05"), result);
  }

  @Test
  void givenNullInstallmentsWhenCalculateDueDateThenReturnNull() {
    LocalDate result = mapper.calculateDueDate(List.of(), null);

    assertNull(result);
  }

}

