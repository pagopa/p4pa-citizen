package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.DebtorPaymentOptionDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtorUnpaidDebtPositionDTO;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorDebtPositionDTO;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.BaseInstallment;
import it.gov.pagopa.pu.debtpositions.dto.generated.BasePaymentOption;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtorDebtPositionDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.PagedDebtorUnpaidDebtPositionDTO;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.co.jemos.podam.api.PodamFactory;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


class PagedDebtorDebtPositionMapperTest {

  private final PagedDebtorDebtPositionMapper mapper = Mappers.getMapper(PagedDebtorDebtPositionMapper.class);
  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  private BasePaymentOption buildPaymentOption(long totalAmount, LocalDate... dueDates) {
    BasePaymentOption po = podamFactory.manufacturePojo(BasePaymentOption.class);
    po.setTotalAmountCents(totalAmount);
    po.setInstallments(new java.util.ArrayList<>());

    long installmentValue = totalAmount / dueDates.length;

    for (LocalDate d : dueDates) {
      BaseInstallment inst = podamFactory.manufacturePojo(BaseInstallment.class);
      inst.setAmountCents(installmentValue);
      inst.setDueDate(d);
      po.getInstallments().add(inst);
    }

    return po;
  }

  @Test
  void givenPagedDebtorUnpaidDebtPositionAndOrganizationsMapWhenMapThenReturnPagedDebtorDebtPositionDTO() {
    // given
    Organization o1 = podamFactory.manufacturePojo(Organization.class);
    o1.setOrganizationId(1L);
    Organization o2 = podamFactory.manufacturePojo(Organization.class);
    o2.setOrganizationId(2L);

    Map<Long, Organization> organizationsMap = new HashMap<>();
    organizationsMap.put(o1.getOrganizationId(), o1);
    organizationsMap.put(o2.getOrganizationId(), o2);

    DebtorDebtPositionDTO dp1 = podamFactory.manufacturePojo(DebtorDebtPositionDTO.class);
    DebtorDebtPositionDTO dp2 = podamFactory.manufacturePojo(DebtorDebtPositionDTO.class);

    dp1.setOrganizationId(1L);
    dp2.setOrganizationId(2L);

    BasePaymentOption po1 = buildPaymentOption(1500, LocalDate.of(2024, 3, 10));
    BasePaymentOption po2 = buildPaymentOption(2500, LocalDate.of(2024, 2, 20));

    dp1.setPaymentOptions(List.of(po1));
    dp2.setPaymentOptions(List.of(po2));

    PagedDebtorUnpaidDebtPositionDTO page =
      podamFactory.manufacturePojo(PagedDebtorUnpaidDebtPositionDTO.class);

    page.setContent(List.of(dp1, dp2));

    // when
    PagedDebtorDebtPositionDTO result = mapper.map(organizationsMap, page);

    // then
    assertNotNull(result);
    assertNotNull(result.getContent());
    assertEquals(2, result.getContent().size());

    DebtorUnpaidDebtPositionDTO first = result.getContent().get(0);
    DebtorUnpaidDebtPositionDTO second = result.getContent().get(1);

    assertEquals(o1.getOrganizationId(), first.getOrganizationId());
    assertEquals(o2.getOrganizationId(), second.getOrganizationId());

    assertEquals(page.getTotalPages(), result.getTotalPages());
    assertEquals(page.getSize(), result.getSize());
    assertEquals(page.getNumber(), result.getNumber());
    assertEquals(page.getTotalElements(), result.getTotalElements());

    result.getContent().forEach(item -> {
      TestUtils.checkNotNullFields(item);
      assertNotNull(item.getPaymentOptions());
      assertFalse(item.getPaymentOptions().isEmpty());
      item.getPaymentOptions().forEach(TestUtils::checkNotNullFields);
    });

    TestUtils.checkNotNullFields(result);
  }

  @Test
  void givenMultiplePaymentOptionsWhenCalculateDueDateThenReturnEarliestInstallment() {
    BasePaymentOption po1 = buildPaymentOption(1000, LocalDate.of(2024, 1, 10));
    BasePaymentOption po2 = buildPaymentOption(1000, LocalDate.of(2024, 1, 5));

    LocalDate result = mapper.calculateDueDate(List.of(po1, po2));

    assertEquals(LocalDate.of(2024, 1, 5), result);
  }

  @Test
  void givenSinglePaymentOptionWhenCalculateTotalAmountThenReturnSumOfInstallments() {
    BasePaymentOption po = buildPaymentOption(
      3000,
      LocalDate.of(2024, 1, 10),
      LocalDate.of(2024, 1, 20),
      LocalDate.of(2024, 2, 10)
    );

    Long result = mapper.calculateTotalAmountCents(po);

    assertEquals(3000L, result);
  }

  @Test
  void givenMultiplePaymentOptionsWithSameTotalWhenCalculateTotalAmountThenReturnTotal() {
    BasePaymentOption po1 = buildPaymentOption(2000, LocalDate.of(2024, 1, 15));
    BasePaymentOption po2 = buildPaymentOption(2000, LocalDate.of(2024, 1, 20));

    Long result = mapper.calculateTotalAmountCents(List.of(po1, po2));

    assertEquals(2000L, result);
  }

  @Test
  void givenMultiplePaymentOptionsWithDifferentTotalsWhenCalculateTotalAmountThenReturnNull() {
    BasePaymentOption po1 = buildPaymentOption(2000, LocalDate.of(2024, 1, 15));
    BasePaymentOption po2 = buildPaymentOption(3000, LocalDate.of(2024, 1, 20));

    Long result = mapper.calculateTotalAmountCents(List.of(po1, po2));

    assertNull(result);
  }

  @Test
  void givenOrganizationAndDebtPositionWhenMapThenReturnMappedDTO() {
    Organization org = podamFactory.manufacturePojo(Organization.class);
    org.setOrganizationId(1L);
    org.setOrgName("orgName");
    org.setOrgFiscalCode("12345678901");

    DebtorDebtPositionDTO dp = podamFactory.manufacturePojo(DebtorDebtPositionDTO.class);
    dp.setOrganizationId(1L);
    dp.setDebtPositionDescription("Test Position");

    BasePaymentOption po1 = buildPaymentOption(2000, LocalDate.of(2024, 1, 10));
    BasePaymentOption po2 = buildPaymentOption(2000, LocalDate.of(2024, 1, 5));

    dp.setPaymentOptions(List.of(po1, po2));

    DebtorUnpaidDebtPositionDTO result = mapper.map(org, dp);

    assertThat(result.getOrgName()).isEqualTo("orgName");
    assertThat(result.getOrgFiscalCode()).isEqualTo("12345678901");
    assertThat(result.getPaymentOptions()).hasSize(2);

    assertThat(result.getPaymentOptions().stream()
      .map(DebtorPaymentOptionDTO::getDueDate))
      .contains(LocalDate.of(2024, 1, 5));
    TestUtils.checkNotNullFields(result);
  }

  @Test
  void givenMissingOrganizationWhenRetrieveOrganizationThenThrowIllegalStateException() {
    Map<Long, Organization> organizations = Map.of();

    DebtorDebtPositionDTO dp = podamFactory.manufacturePojo(DebtorDebtPositionDTO.class);
    dp.setDebtPositionId(77L);
    dp.setOrganizationId(123L);

    assertThrows(IllegalStateException.class, () -> mapper.retrieveOrganization(organizations, dp));
  }

  @Test
  void givenNullPaymentOptionsWhenMapPaymentOptionsThenReturnEmptyList() {
    // given
    List<BasePaymentOption> paymentOptions = null;

    // when
    List<DebtorPaymentOptionDTO> result = mapper.mapPaymentOptions(paymentOptions);

    // then
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void givenEmptyPaymentOptionsWhenMapPaymentOptionsThenReturnEmptyList() {
    // given
    List<BasePaymentOption> paymentOptions = Collections.emptyList();

    // when
    List<DebtorPaymentOptionDTO> result = mapper.mapPaymentOptions(paymentOptions);

    // then
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

}
