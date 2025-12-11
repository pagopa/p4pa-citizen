package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.DebtorUnpaidDebtPositionInstallmentsDTO;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentNoPII;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentStatus;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DebtorUnpaidDebtPositionInstallmentsMapperTest {

  private final DebtorUnpaidDebtPositionInstallmentsMapper mapper = Mappers.getMapper(DebtorUnpaidDebtPositionInstallmentsMapper.class);

  private final PodamFactory podam = TestUtils.getPodamFactory();

  @Test
  void givenOrganizationAndInstallmentWhenMapThenReturnMappedDTO() {
    // given
    Organization org = podam.manufacturePojo(Organization.class);
    org.setOrganizationId(100L);
    org.setOrgName("TestOrg");
    org.setOrgFiscalCode("12345678901");

    Long debtPositionId = 1L;

    InstallmentNoPII installment = podam.manufacturePojo(InstallmentNoPII.class);
    installment.setStatus(InstallmentStatus.UNPAID);

    // when
    DebtorUnpaidDebtPositionInstallmentsDTO result = mapper.map(org, installment, debtPositionId);

    // then
    assertNotNull(result);

    assertEquals(org.getOrganizationId(), result.getOrganizationId());
    assertEquals(org.getOrgName(), result.getOrgName());
    assertEquals(org.getOrgFiscalCode(), result.getOrgFiscalCode());

    assertEquals(installment.getStatus(), result.getStatus());

    TestUtils.checkNotNullFields(result);
  }

  @Test
  void givenOrganizationAndInstallmentsListWhenMapListThenReturnMappedList() {
    // given
    Organization org = podam.manufacturePojo(Organization.class);
    org.setOrganizationId(200L);
    org.setOrgName("AnotherOrg");
    org.setOrgFiscalCode("98765432109");

    Long debtPositionId = 1L;

    List<InstallmentNoPII> installments = podam.manufacturePojo(List.class, InstallmentNoPII.class);

    // when
    List<DebtorUnpaidDebtPositionInstallmentsDTO> result =
      mapper.mapDebtorUnpaidDebtPositionInstallmentsList(org, installments, debtPositionId);

    // then
    assertNotNull(result);
    assertEquals(installments.size(), result.size());

    result.forEach(TestUtils::checkNotNullFields);

    for (int i = 0; i < installments.size(); i++) {
      InstallmentNoPII source = installments.get(i);
      DebtorUnpaidDebtPositionInstallmentsDTO mapped = result.get(i);

      assertEquals(org.getOrganizationId(), mapped.getOrganizationId());
      assertEquals(org.getOrgName(), mapped.getOrgName());
      assertEquals(org.getOrgFiscalCode(), mapped.getOrgFiscalCode());

      assertEquals(source.getStatus(), mapped.getStatus());
    }
  }
}
