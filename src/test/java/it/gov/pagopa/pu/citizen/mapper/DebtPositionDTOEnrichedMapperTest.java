package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.DebtPositionDTOEnriched;
import it.gov.pagopa.pu.citizen.dto.InstallmentExtendedDTO;
import it.gov.pagopa.pu.citizen.dto.PaymentOptionExtendedDTO;
import it.gov.pagopa.pu.citizen.utils.InstallmentUtils;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DebtPositionDTOEnrichedMapperTest {

  private final DebtPositionDTOEnrichedMapper mapper = Mappers.getMapper(DebtPositionDTOEnrichedMapper.class);

  private final PodamFactory podam = TestUtils.getPodamFactory();

  @Test
  void givenDebtPositionWhenMapThenReturnEnrichedDTO() {
    // given
    DebtPositionDTO debtPositionDTO = podam.manufacturePojo(DebtPositionDTO.class);

    InstallmentDTO installment =
      debtPositionDTO.getPaymentOptions().getFirst().getInstallments().getFirst();

    installment.setInstallmentId(10L);

    PostalIbanVerifyResponse postalIbanVerifyResponse =
      podam.manufacturePojo(PostalIbanVerifyResponse.class);

    // when
    try(MockedStatic<InstallmentUtils> installmentUtilsMock = Mockito.mockStatic(InstallmentUtils.class)) {
      installmentUtilsMock.when(() -> InstallmentUtils.extractAllCCP(installment.getInstallmentId(), postalIbanVerifyResponse)).thenReturn(false);
      DebtPositionDTOEnriched result = mapper.map(debtPositionDTO, postalIbanVerifyResponse);

      // then
      assertNotNull(result);
      assertNotNull(result.getPaymentOptions());
      assertFalse(result.getPaymentOptions().isEmpty());

      PaymentOptionExtendedDTO first = result.getPaymentOptions().getFirst();
      assertNotNull(first);

      assertNotNull(first.getInstallments());
      assertFalse(first.getInstallments().isEmpty());

      InstallmentExtendedDTO inst = first.getInstallments().getFirst();

      assertNotNull(inst);
      assertNotNull(inst.getAllCCP());

      TestUtils.checkNotNullFields(result);
    }
  }

  @Test
  void givenPaymentOptionWhenMapThenReturnExtendedPaymentOption() {
    // given
    PaymentOptionDTO paymentOptionDTO =
      podam.manufacturePojo(PaymentOptionDTO.class);

    paymentOptionDTO.getInstallments().getFirst().setInstallmentId(10L);

    PostalIbanVerifyResponse response =
      podam.manufacturePojo(PostalIbanVerifyResponse.class);

    // when
    try (MockedStatic<InstallmentUtils> installmentUtilsMock = Mockito.mockStatic(InstallmentUtils.class)) {
      for (InstallmentDTO installment : paymentOptionDTO.getInstallments()){
        installmentUtilsMock.when(() -> InstallmentUtils.extractAllCCP(installment.getInstallmentId(), response)).thenReturn(false);
      }

      PaymentOptionExtendedDTO result =  mapper.map(paymentOptionDTO, response);

      // then
      assertNotNull(result);
      assertNotNull(result.getInstallments());
      assertFalse(result.getInstallments().isEmpty());

      InstallmentExtendedDTO inst = result.getInstallments().getFirst();

      assertNotNull(inst);
      assertNotNull(inst.getAllCCP());

      TestUtils.checkNotNullFields(result);
    }
  }

  @Test
  void givenInstallmentWhenMapThenReturnExtendedInstallment() {
    // given
    InstallmentDTO installment =
      podam.manufacturePojo(InstallmentDTO.class);

    installment.setInstallmentId(10L);

    PostalIbanVerifyResponse response =
      podam.manufacturePojo(PostalIbanVerifyResponse.class);

    // when
    try(MockedStatic<InstallmentUtils> installmentUtilsMock = Mockito.mockStatic(InstallmentUtils.class)) {
      installmentUtilsMock.when(() -> InstallmentUtils.extractAllCCP(installment.getInstallmentId(), response)).thenReturn(false);
      InstallmentExtendedDTO result =
        mapper.map(installment, response);

      // then
      assertNotNull(result);
      assertNotNull(result.getAllCCP());

      TestUtils.checkNotNullFields(result);
    }
  }

  @Test
  void givenNullPostalIbanVerifyResponseThenReturnExtendedInstallment() {
    // given
    InstallmentDTO installment = podam.manufacturePojo(InstallmentDTO.class);
    installment.setInstallmentId(10L);

    // when
      InstallmentExtendedDTO result =  mapper.map(installment, null);

    // then
    assertNotNull(result);
    assertNull(result.getAllCCP());

    TestUtils.checkNotNullFields(result, "allCCP");
  }
}
