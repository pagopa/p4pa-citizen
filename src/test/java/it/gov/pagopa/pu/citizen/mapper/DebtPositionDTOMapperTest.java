package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.InstallmentRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.PaymentOptionRequestDTO;
import it.gov.pagopa.pu.citizen.exception.InvalidRequestBodyException;
import it.gov.pagopa.pu.citizen.utils.DebtPositionConstants;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.*;
import org.junit.jupiter.api.Assertions;
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
    int expirationDays = 5;

    // when
    DebtPositionDTO result = mapper.mapSpontaneousDebtPositionDTO(request, expirationDays);

    // then
    assertNotNull(result);
    assertTrue(result.getFlagPuPagoPaPayment());
    assertEquals(DebtPositionOrigin.SPONTANEOUS, result.getDebtPositionOrigin());
    assertEquals(DebtPositionStatus.UNPAID, result.getStatus());
    assertEquals(DebtPositionConstants.DEBT_POSITION_DESCRIPTION_PLACEHOLDER, result.getDescription());

    result.getPaymentOptions().forEach(opt -> {
      assertEquals(PaymentOptionStatus.UNPAID, opt.getStatus());
      assertEquals(PaymentOptionType.SINGLE_INSTALLMENT, opt.getPaymentOptionType());
      assertEquals(1, opt.getPaymentOptionIndex());
      TestUtils.checkNotNullFields(opt, "paymentOptionId", "debtPositionId", "description","creationDate", "updateDate", "updateOperatorExternalId", "updateTraceId");

      opt.getInstallments().forEach(inst -> {
        assertEquals(InstallmentStatus.UNPAID, inst.getStatus());
        assertTrue(inst.getGenerateNotice());
        assertEquals(LocalDate.now().plusDays(expirationDays), inst.getDueDate());
        assertNull(inst.getTransfers());
        assertTrue(inst.getSwitchToExpired());
        TestUtils.checkNotNullFields(inst, "updateTraceId", "nav", "updateDate", "iun", "iur", "iuv", "creationDate", "iupdPagopa", "ingestionFlowFileId", "ingestionFlowFileLineNumber", "installmentId", "balance", "transfers", "iud", "iuf", "legacyPaymentMetadata", "sourceFlowName", "updateOperatorExternalId", "receiptId", "syncStatus", "ingestionFlowFileAction", "paymentOptionId", "notificationDate");
      });
    });

  }

  @Test
  void givenPaymentOptionRequestDTOWhenMapSpontaneousPaymentOptionDTOThenReturnPaymentOptionDTO() {
    // given
    PaymentOptionRequestDTO request = podamFactory.manufacturePojo(PaymentOptionRequestDTO.class);
    Integer expirationDays = 10;

    // when
    PaymentOptionDTO result = mapper.mapSpontaneousPaymentOptionDTO(request, expirationDays);

    // then
    assertNotNull(result);
    assertEquals(PaymentOptionType.SINGLE_INSTALLMENT, result.getPaymentOptionType());
    assertEquals(1, result.getPaymentOptionIndex());
    assertEquals(PaymentOptionStatus.UNPAID, result.getStatus());
    TestUtils.checkNotNullFields(result, "paymentOptionId", "debtPositionId", "description", "creationDate", "updateDate", "updateOperatorExternalId", "updateTraceId");
  }

  @Test
  void givenNoTotalAmountCentsWhenMapSpontaneousPaymentOptionDTOThenInvalidRequestBodyException() {
    // given
    PaymentOptionRequestDTO request = podamFactory.manufacturePojo(PaymentOptionRequestDTO.class);
    Integer expirationDays = 10;
    request.setTotalAmountCents(null);

    // when
    InvalidRequestBodyException invalidRequestBodyException = assertThrows(InvalidRequestBodyException.class, () -> mapper.mapSpontaneousPaymentOptionDTO(request, expirationDays));

    // then
    Assertions.assertEquals("INVALID_DEBT_POSITION_REQUEST_BODY",invalidRequestBodyException.getCode());
  }

  @Test
  void givenInstallmentRequestDTOWhenMapSpontaneousInstallmentDTOThenReturnInstallmentDTO() {
    // given
    InstallmentRequestDTO request = podamFactory.manufacturePojo(InstallmentRequestDTO.class);
    int expirationDays = 7;

    // when
    InstallmentDTO result = mapper.mapSpontaneousInstallmentDTO(request, expirationDays);

    // then
    assertNotNull(result);
    assertTrue(result.getGenerateNotice());
    assertEquals(InstallmentStatus.UNPAID, result.getStatus());
    assertEquals(LocalDate.now().plusDays(expirationDays), result.getDueDate());
    assertTrue(result.getSwitchToExpired());
    assertEquals(request.getRemittanceInformation(),result.getOriginalRemittanceInformation());
    assertEquals(DebtPositionConstants.INSTALLMENT_REMITTANCE_INFORMATION_PLACEHOLDER +" "+request.getUserRemittanceInformation(),result.getRemittanceInformation());
    TestUtils.checkNotNullFields(result, "updateTraceId", "nav", "updateDate", "iun", "iur", "iuv", "creationDate", "iupdPagopa", "ingestionFlowFileId", "ingestionFlowFileLineNumber", "installmentId", "balance", "transfers", "iud", "iuf", "legacyPaymentMetadata", "sourceFlowName", "updateOperatorExternalId", "receiptId", "syncStatus", "ingestionFlowFileAction", "paymentOptionId", "notificationDate");
  }

  @Test
  void givenInstallmentRequestDTOWithNoUserRemittanceInformationWhenMapSpontaneousInstallmentDTOThenReturnInstallmentDTO() {
    // given
    InstallmentRequestDTO request = podamFactory.manufacturePojo(InstallmentRequestDTO.class);
    request.setUserRemittanceInformation(null);
    int expirationDays = 7;

    // when
    InstallmentDTO result = mapper.mapSpontaneousInstallmentDTO(request, expirationDays);

    // then
    assertNotNull(result);
    assertTrue(result.getGenerateNotice());
    assertEquals(InstallmentStatus.UNPAID, result.getStatus());
    assertEquals(LocalDate.now().plusDays(expirationDays), result.getDueDate());
    assertTrue(result.getSwitchToExpired());
    assertEquals(request.getRemittanceInformation(),result.getOriginalRemittanceInformation());
    assertEquals(DebtPositionConstants.INSTALLMENT_REMITTANCE_INFORMATION_PLACEHOLDER,result.getRemittanceInformation());
    TestUtils.checkNotNullFields(result, "updateTraceId", "nav", "updateDate", "iun", "iur", "iuv", "creationDate", "iupdPagopa", "ingestionFlowFileId", "ingestionFlowFileLineNumber", "installmentId", "balance", "transfers", "iud", "iuf", "legacyPaymentMetadata", "sourceFlowName", "updateOperatorExternalId", "receiptId", "syncStatus", "ingestionFlowFileAction", "paymentOptionId", "notificationDate");
  }

  @Test
  void givenNullExpirationDaysMapSpontaneousInstallmentDTOThenMapLocalDate() {
    // given
    InstallmentRequestDTO request = podamFactory.manufacturePojo(InstallmentRequestDTO.class);

    // when
    InstallmentDTO result = mapper.mapSpontaneousInstallmentDTO(request, null);

    // then
    assertNotNull(result);
    assertEquals(LocalDate.now(), result.getDueDate());
    assertTrue(result.getGenerateNotice());
    assertEquals(InstallmentStatus.UNPAID, result.getStatus());
    assertTrue(result.getSwitchToExpired());
    assertEquals(request.getRemittanceInformation(),result.getOriginalRemittanceInformation());
    assertEquals(DebtPositionConstants.INSTALLMENT_REMITTANCE_INFORMATION_PLACEHOLDER +" "+request.getUserRemittanceInformation(),result.getRemittanceInformation());
    TestUtils.checkNotNullFields(result, "updateTraceId", "nav", "updateDate", "iun", "iur", "iuv", "creationDate", "iupdPagopa", "ingestionFlowFileId", "ingestionFlowFileLineNumber", "installmentId", "balance", "transfers", "iud", "iuf", "legacyPaymentMetadata", "sourceFlowName", "updateOperatorExternalId", "receiptId", "syncStatus", "ingestionFlowFileAction", "paymentOptionId", "notificationDate");
  }

  @Test
  void givenNoAmountCentsWhenMapSpontaneousInstallmentDTOThenInvalidRequestBodyException() {
    // given
    InstallmentRequestDTO request = podamFactory.manufacturePojo(InstallmentRequestDTO.class);
    request.setAmountCents(null);

    // when
    InvalidRequestBodyException invalidRequestBodyException = assertThrows(InvalidRequestBodyException.class, () -> mapper.mapSpontaneousInstallmentDTO(request, null));

    // then
    Assertions.assertEquals("INVALID_DEBT_POSITION_REQUEST_BODY",invalidRequestBodyException.getCode());
  }

}
