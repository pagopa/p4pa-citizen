package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.DebtorUnpaidDebtPositionInstallmentsDTO;
import it.gov.pagopa.pu.citizen.utils.InstallmentUtils;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentNoPII;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentStatus;
import it.gov.pagopa.pu.debtpositions.dto.generated.PostalIbanVerifyResponse;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import uk.co.jemos.podam.api.PodamFactory;

import java.time.LocalDate;
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
    PostalIbanVerifyResponse postalIbanVerifyResponse = podam.manufacturePojo(PostalIbanVerifyResponse.class);

    try(MockedStatic<InstallmentUtils> installmentUtilsMock = Mockito.mockStatic(InstallmentUtils.class)) {
      InstallmentNoPII installment = podam.manufacturePojo(InstallmentNoPII.class);
      installment.setStatus(InstallmentStatus.UNPAID);
      installmentUtilsMock.when(()->InstallmentUtils.resolveInstallmentStatus(installment.getStatus())).thenReturn(installment.getStatus());
      installmentUtilsMock.when(()->InstallmentUtils.extractAllCCP(installment.getInstallmentId(), postalIbanVerifyResponse)).thenReturn(false);
      // when
      DebtorUnpaidDebtPositionInstallmentsDTO result = mapper.map(org, installment, debtPositionId, postalIbanVerifyResponse);

      // then
      assertNotNull(result);

      assertEquals(org.getOrganizationId(), result.getOrganizationId());
      assertEquals(org.getOrgName(), result.getOrgName());
      assertEquals(org.getOrgFiscalCode(), result.getOrgFiscalCode());

      assertEquals(installment.getStatus(), result.getStatus());
      assertEquals(Boolean.FALSE, result.getAllCCP());

      TestUtils.checkNotNullFields(result);
      installmentUtilsMock.verify(()->InstallmentUtils.resolveInstallmentStatus(installment.getStatus()));
    }
  }

  @Test
  void givenOrganizationAndInstallmentsListWhenMapListThenReturnOrderedMappedList() {
    // given
    Organization org = podam.manufacturePojo(Organization.class);
    org.setOrganizationId(200L);
    org.setOrgName("AnotherOrg");
    org.setOrgFiscalCode("98765432109");

    Long debtPositionId = 1L;

    InstallmentNoPII installment = podam.manufacturePojo(InstallmentNoPII.class);
    installment.setStatus(InstallmentStatus.UNPAID);
    installment.dueDate(LocalDate.of(2025, 12, 3));

    InstallmentNoPII installment2 = podam.manufacturePojo(InstallmentNoPII.class);
    installment2.setStatus(InstallmentStatus.UNPAID);
    installment2.dueDate(LocalDate.of(2024, 3, 3));
    List<InstallmentNoPII> installments = List.of(installment, installment2);

    PostalIbanVerifyResponse postalIbanVerifyResponse = podam.manufacturePojo(PostalIbanVerifyResponse.class);
    try(MockedStatic<InstallmentUtils> installmentUtilsMock = Mockito.mockStatic(InstallmentUtils.class)) {
      installmentUtilsMock.when(()->InstallmentUtils.resolveInstallmentStatus(installment.getStatus())).thenReturn(installment.getStatus());
      installmentUtilsMock.when(()->InstallmentUtils.extractAllCCP(installment.getInstallmentId(), postalIbanVerifyResponse)).thenReturn(false);
      // when
      List<DebtorUnpaidDebtPositionInstallmentsDTO> result =
        mapper.mapDebtorUnpaidDebtPositionInstallmentsList(org, installments, debtPositionId, postalIbanVerifyResponse);

      // then
      assertNotNull(result);
      assertEquals(installments.size(), result.size());
      assertEquals(installment2.getDueDate(), result.getFirst().getDueDate());
      assertEquals(installment.getDueDate(), result.getLast().getDueDate());

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

  @Test
  void givenNullPostalIbanVerifyResponseThenOk() {
    // given
    Organization org = podam.manufacturePojo(Organization.class);
    org.setOrganizationId(100L);
    org.setOrgName("TestOrg");
    org.setOrgFiscalCode("12345678901");

    Long debtPositionId = 1L;

    try(MockedStatic<InstallmentUtils> installmentUtilsMock = Mockito.mockStatic(InstallmentUtils.class)) {
      InstallmentNoPII installment = podam.manufacturePojo(InstallmentNoPII.class);
      installment.setStatus(InstallmentStatus.UNPAID);
      installmentUtilsMock.when(()->InstallmentUtils.resolveInstallmentStatus(installment.getStatus())).thenReturn(installment.getStatus());
      // when
      DebtorUnpaidDebtPositionInstallmentsDTO result = mapper.map(org, installment, debtPositionId, null);

      // then
      assertNotNull(result);
      assertNull(result.getAllCCP());

      TestUtils.checkNotNullFields(result, "allCCP");
    }
  }
}
