package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionResponseDTO;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.PaymentOptionDTO;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.co.jemos.podam.api.PodamFactory;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DebtPositionResponseDTOMapperTest {

  private final DebtPositionResponseDTOMapper mapper = Mappers.getMapper(DebtPositionResponseDTOMapper.class);
  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @Test
  void givenDebtPositionTypeOrgAndSpontaneousFormWhenMapThenFieldsAreMappedCorrectly() {
    // given
    Organization organization = podamFactory.manufacturePojo(Organization.class);
    DebtPositionDTO debtPositionDTO = podamFactory.manufacturePojo(DebtPositionDTO.class);

    PaymentOptionDTO paymentOption = new PaymentOptionDTO();
    InstallmentDTO installment = new InstallmentDTO();
    installment.setAmountCents(100L);
    installment.setDueDate(LocalDate.now().plusDays(5));
    paymentOption.setInstallments(java.util.List.of(installment));
    debtPositionDTO.setPaymentOptions(java.util.List.of(paymentOption));

    // when
    DebtPositionResponseDTO result = mapper.map(debtPositionDTO, organization);

    // then
    assertNotNull(result);
    assertEquals(organization.getOrganizationId(), result.getOrganizationId());
    assertEquals(organization.getOrgFiscalCode(), result.getOrgFiscalCode());
    assertEquals(organization.getOrgName(), result.getOrgName());

    assertNotNull(result.getPaymentDetails());
    assertEquals(installment.getAmountCents(), result.getPaymentDetails().getAmountCents());
    assertEquals(installment.getDueDate(), result.getPaymentDetails().getDueDate());
    TestUtils.checkNotNullFields(result);
  }

  @Test
  void givenDebtPositionWithEmptyPaymentOptionsWhenMapThenPaymentDetailsIsNull() {
    // given
    DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
    debtPositionDTO.setPaymentOptions(java.util.Collections.emptyList());
    Organization organization = podamFactory.manufacturePojo(Organization.class);

    // when
    DebtPositionResponseDTO result = mapper.map(debtPositionDTO, organization);

    // then
    assertNotNull(result);
    assertNull(result.getPaymentDetails());
  }

  @Test
  void givenPaymentOptionWithNoInstallmentsWhenMapThenPaymentDetailsIsNull() {
    // given
    PaymentOptionDTO paymentOption = new PaymentOptionDTO();
    paymentOption.setInstallments(java.util.Collections.emptyList());

    DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
    debtPositionDTO.setPaymentOptions(java.util.List.of(paymentOption));

    Organization organization = podamFactory.manufacturePojo(Organization.class);

    // when
    DebtPositionResponseDTO result = mapper.map(debtPositionDTO, organization);

    // then
    assertNotNull(result);
    assertNull(result.getPaymentDetails());
  }

  @Test
  void givenNullDebtPositionWhenMapThenPaymentDetailsIsNullButOrganizationMapped() {
    // given
    Organization organization = podamFactory.manufacturePojo(Organization.class);

    // when
    DebtPositionResponseDTO result = mapper.map(null, organization);

    // then
    assertNotNull(result);
    assertEquals(organization.getOrganizationId(), result.getOrganizationId());
    assertEquals(organization.getOrgFiscalCode(), result.getOrgFiscalCode());
    assertEquals(organization.getOrgName(), result.getOrgName());
    assertNull(result.getPaymentDetails());
  }
}

