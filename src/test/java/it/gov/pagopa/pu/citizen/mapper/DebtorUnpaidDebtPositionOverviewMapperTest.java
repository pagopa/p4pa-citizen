package it.gov.pagopa.pu.citizen.mapper;

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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    BasePaymentOption po2 = buildPaymentOption(2000, LocalDate.of(2024,1,5));

    dp.setPaymentOptions(List.of(po1, po2));

    // when
    DebtorUnpaidDebtPositionOverviewDTO result = mapper.map(org, dp);

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
    result.getPaymentOptions().forEach(po ->{
      po.getInstallments().forEach(TestUtils::checkNotNullFields);
      assertEquals(1, po.getInstallments().size());
      });

    TestUtils.checkNotNullFields(result);
  }

}
