package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.InstallmentRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.PaymentOptionRequestDTO;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.*;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.co.jemos.podam.api.PodamFactory;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DebtPositionDTOMapperTest {

  DebtPositionDTOMapper mapper = Mappers.getMapper(DebtPositionDTOMapper.class);
  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @Test
  void givenDebtPositionRequestDTOWhenMapSpontaneousDebtPositionDTOThenFieldsAreMappedCorrectly() {
    // given
    DebtPositionRequestDTO request = podamFactory.manufacturePojo(DebtPositionRequestDTO.class);
    int dueDateOffsetDays = 5;

    // when
    DebtPositionDTO result = mapper.mapSpontaneousDebtPositionDTO(request, dueDateOffsetDays);

    // then
    assertNotNull(result);
    assertTrue(result.getFlagPuPagoPaPayment());
    assertEquals(DebtPositionOrigin.SPONTANEOUS, result.getDebtPositionOrigin());
    assertEquals(DebtPositionStatus.UNPAID, result.getStatus());

    result.getPaymentOptions().forEach(opt -> {
      assertEquals(PaymentOptionStatus.UNPAID, opt.getStatus());
      assertEquals(PaymentOptionTypeEnum.SINGLE_INSTALLMENT, opt.getPaymentOptionType());
      assertEquals(1, opt.getPaymentOptionIndex());

      opt.getInstallments().forEach(inst -> {
        assertEquals(InstallmentStatus.UNPAID, inst.getStatus());
        assertTrue(inst.getGenerateNotice());
        assertEquals(LocalDate.now().plusDays(dueDateOffsetDays), inst.getDueDate());
      });
    });
  }

  @Test
  void givenPaymentOptionRequestDTOWhenMapSpontaneousPaymentOptionDTOThenReturnPaymentOptionDTO() {
    // given
    PaymentOptionRequestDTO request = podamFactory.manufacturePojo(PaymentOptionRequestDTO.class);
    Integer dueDateOffsetDays = 10;

    // when
    PaymentOptionDTO result = mapper.mapSpontaneousPaymentOptionDTO(request, dueDateOffsetDays);

    // then
    assertNotNull(result);
    assertEquals(PaymentOptionTypeEnum.SINGLE_INSTALLMENT, result.getPaymentOptionType());
    assertEquals(1, result.getPaymentOptionIndex());
    assertEquals(PaymentOptionStatus.UNPAID, result.getStatus());
  }

  @Test
  void givenInstallmentRequestDTOWhenMapSpontaneousInstallmentDTOThenReturnInstallmentDTO() {
    // given
    InstallmentRequestDTO request = podamFactory.manufacturePojo(InstallmentRequestDTO.class);
    int dueDateOffsetDays = 7;

    // when
    InstallmentDTO result = mapper.mapSpontaneousInstallmentDTO(request, dueDateOffsetDays);

    // then
    assertNotNull(result);
    assertTrue(result.getGenerateNotice());
    assertEquals(InstallmentStatus.UNPAID, result.getStatus());
    assertEquals(LocalDate.now().plusDays(dueDateOffsetDays), result.getDueDate());
  }

  @Test
  void givenNullDueDateOffsetDaysMapSpontaneousInstallmentDTOThenMapLocalDate() {
    // given
    InstallmentRequestDTO request = podamFactory.manufacturePojo(InstallmentRequestDTO.class);

    // when
    InstallmentDTO result = mapper.mapSpontaneousInstallmentDTO(request, null);

    // then
    assertEquals(LocalDate.now(), result.getDueDate());
  }

}
