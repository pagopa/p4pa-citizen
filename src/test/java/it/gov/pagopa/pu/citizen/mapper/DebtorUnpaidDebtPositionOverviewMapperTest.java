package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.DebtorPaymentOptionOverviewDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtorUnpaidDebtPositionOverviewDTO;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.BaseInstallment;
import it.gov.pagopa.pu.debtpositions.dto.generated.BasePaymentOption;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionStatus;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtorDebtPositionDTO;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.co.jemos.podam.api.PodamFactory;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DebtorUnpaidDebtPositionOverviewMapperTest {

  private final DebtorUnpaidDebtPositionOverviewMapper mapper = Mappers.getMapper(DebtorUnpaidDebtPositionOverviewMapper.class);
  private final PodamFactory podam = TestUtils.getPodamFactory();

  private BasePaymentOption buildPaymentOption(long totalAmount, LocalDate dueDate) {
    BasePaymentOption po = podam.manufacturePojo(BasePaymentOption.class);
    po.setTotalAmountCents(totalAmount);
    po.setInstallments(new java.util.ArrayList<>());

      BaseInstallment inst = podam.manufacturePojo(BaseInstallment.class);
      inst.setAmountCents(totalAmount);
      inst.setDueDate(dueDate);
      po.getInstallments().add(inst);

    return po;
  }

  @Test
  void givenOrganizationAndDebtPositionWhenMapThenReturnMappedDTO() {
    // given
    Organization org = podam.manufacturePojo(Organization.class);
    org.setOrganizationId(1L);
    org.setOrgName("TestOrg");
    org.setOrgFiscalCode("12345678901");

    DebtorDebtPositionDTO dp = podam.manufacturePojo(DebtorDebtPositionDTO.class);
    dp.setDebtPositionDescription("DebtPositionTest");
    dp.setStatus(DebtPositionStatus.UNPAID);

    BasePaymentOption po1 = buildPaymentOption(2000, LocalDate.of(2024,1,10));
    po1.getInstallments().getFirst().setInstallmentId(1L);
    BasePaymentOption po2 = buildPaymentOption(2000, LocalDate.of(2024,1,5));

    dp.setPaymentOptions(List.of(po1, po2));

    Map<Long, OffsetDateTime> offsetDateTimeReceiptMap = Map.of(1L, OffsetDateTime.now());

    // when
    DebtorUnpaidDebtPositionOverviewDTO result = mapper.map(org, dp, offsetDateTimeReceiptMap);

    // then
    assertNotNull(result);
    assertEquals(dp.getDebtPositionId(), result.getDebtPositionId());
    assertEquals(dp.getDebtPositionDescription(), result.getDebtPositionDescription());
    assertEquals(dp.getDebtPositionTypeOrgDescription(), result.getDebtPositionTypeOrgDescription());
    assertEquals(dp.getStatus(), result.getStatus());
    assertEquals(dp.getIupdOrg(), result.getIupd());
    assertEquals(org.getOrganizationId(), result.getOrganizationId());
    assertEquals(org.getOrgName(), result.getOrgName());
    assertEquals(org.getOrgFiscalCode(), result.getOrgFiscalCode());

    assertNotNull(result.getPaymentOptions());
    assertEquals(2, result.getPaymentOptions().size());

    result.getPaymentOptions().forEach(TestUtils::checkNotNullFields);
    DebtorPaymentOptionOverviewDTO firstPaymentOption = result.getPaymentOptions().getFirst();
    DebtorPaymentOptionOverviewDTO lastPaymentOption = result.getPaymentOptions().getLast();

    TestUtils.checkNotNullFields(firstPaymentOption);
    TestUtils.checkNotNullFields(lastPaymentOption);
    assertEquals(1, firstPaymentOption.getInstallments().size());
    assertEquals(offsetDateTimeReceiptMap.get(1L), firstPaymentOption.getInstallments().getFirst().getPaymentDateTime());
    assertEquals(1, lastPaymentOption.getInstallments().size());

    TestUtils.checkNotNullFields(result);
  }

  @Test
  void givenNullReceiptMapWhenExtractPaymentDateTimeThenReturnNull() {
    // when
    OffsetDateTime result = mapper.extractPaymentDateTime(1L, null);

    // then
    assertNull(result);
  }

  @Test
  void givenReceiptMapWithInstallmentIdWhenExtractPaymentDateTimeThenReturnMappedDate() {
    // given
    Long installmentId = 10L;
    OffsetDateTime expectedDateTime = OffsetDateTime.now();

    Map<Long, OffsetDateTime> receiptMap = Map.of(
      installmentId, expectedDateTime
    );

    // when
    OffsetDateTime result = mapper.extractPaymentDateTime(installmentId, receiptMap);

    // then
    assertEquals(expectedDateTime, result);
  }

  @Test
  void givenReceiptMapWithoutInstallmentIdWhenExtractPaymentDateTimeThenReturnNull() {
    // given
    Map<Long, OffsetDateTime> receiptMap = Map.of(
      99L, OffsetDateTime.now()
    );

    // when
    OffsetDateTime result = mapper.extractPaymentDateTime(1L, receiptMap);

    // then
    assertNull(result);
  }
}
