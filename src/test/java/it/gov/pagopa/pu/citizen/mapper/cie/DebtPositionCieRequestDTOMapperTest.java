package it.gov.pagopa.pu.citizen.mapper.cie;

import it.gov.pagopa.pu.cie.dto.generated.DebtPositionCieOriginAllowedEnum;
import it.gov.pagopa.pu.cie.dto.generated.DebtPositionCieRequestDTO;
import it.gov.pagopa.pu.citizen.dto.cie.CieFieldValuesDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.InstallmentRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.PaymentOptionRequestDTO;
import it.gov.pagopa.pu.citizen.exception.DebtPositionInvalidFieldValuesException;
import it.gov.pagopa.pu.citizen.exception.InvalidRequestBodyException;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.json.JsonMapper;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class DebtPositionCieRequestDTOMapperTest {

  @Mock
  private JsonMapper jsonMapperMock;
  @Mock
  private Validator validatorMock;
  @Mock
  private ConstraintViolation<CieFieldValuesDTO> violationMock;
  private final PodamFactory podamFactory = TestUtils.getPodamFactory();
  private DebtPositionCieRequestDTOMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new DebtPositionCieRequestDTOMapper(jsonMapperMock, validatorMock);
  }

  @Test
  void whenMapThenOk(){
    String debtPositionTypeOrgCode = "debtPositionTypeOrgCode";
    DebtPositionRequestDTO debtPositionRequestDTO = podamFactory.manufacturePojo(DebtPositionRequestDTO.class);
    PaymentOptionRequestDTO paymentOptionRequestDTO = podamFactory.manufacturePojo(PaymentOptionRequestDTO.class);
    InstallmentRequestDTO installmentRequestDTO = podamFactory.manufacturePojo(InstallmentRequestDTO.class);
    paymentOptionRequestDTO.setInstallments(List.of(installmentRequestDTO));
    debtPositionRequestDTO.setPaymentOptions(List.of(paymentOptionRequestDTO));
    CieFieldValuesDTO cieFieldValuesDTO = podamFactory.manufacturePojo(CieFieldValuesDTO.class);

    Mockito.when(jsonMapperMock.convertValue(debtPositionRequestDTO.getFieldValues(), CieFieldValuesDTO.class)).thenReturn(cieFieldValuesDTO);
    Mockito.when(validatorMock.validate(cieFieldValuesDTO)).thenReturn(Collections.emptySet());


    DebtPositionCieRequestDTO result = mapper.map(debtPositionRequestDTO, debtPositionTypeOrgCode);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(DebtPositionCieOriginAllowedEnum.SPONTANEOUS,result.getOrigin());
    Assertions.assertEquals(debtPositionTypeOrgCode,result.getDebtPositionTypeOrgCode());
    Assertions.assertEquals(installmentRequestDTO.getRemittanceInformation(),result.getRemittanceInformation());
    TestUtils.reflectionEqualsByName(installmentRequestDTO.getDebtor(),result.getDebtor());
    Assertions.assertEquals(cieFieldValuesDTO.getOrgFiscalCode(),result.getOrgFiscalCode());
    Assertions.assertNotNull(result.getPayer());
    Assertions.assertEquals(cieFieldValuesDTO.getPayerEntityType(),result.getPayer().getEntityType());
    Assertions.assertEquals(cieFieldValuesDTO.getPayerFiscalCode(),result.getPayer().getFiscalCode());
    Assertions.assertEquals(cieFieldValuesDTO.getPayerFullName(),result.getPayer().getFullName());
    Assertions.assertEquals(cieFieldValuesDTO.getPayerAddress(),result.getPayer().getAddress());
    Assertions.assertEquals(cieFieldValuesDTO.getPayerCivic(),result.getPayer().getCivic());
    Assertions.assertEquals(cieFieldValuesDTO.getPayerPostalCode(),result.getPayer().getPostalCode());
    Assertions.assertEquals(cieFieldValuesDTO.getPayerLocation(),result.getPayer().getLocation());
    Assertions.assertEquals(cieFieldValuesDTO.getPayerProvince(),result.getPayer().getProvince());
    Assertions.assertEquals(cieFieldValuesDTO.getPayerNation(),result.getPayer().getNation());
    Assertions.assertEquals(cieFieldValuesDTO.getPayerEmail(),result.getPayer().getEmail());
  }

  @Test
  void givenMissingFieldValuesWhenMapThenDebtPositionInvalidFieldValuesException(){
    String debtPositionTypeOrgCode = "debtPositionTypeOrgCode";
    DebtPositionRequestDTO debtPositionRequestDTO = podamFactory.manufacturePojo(DebtPositionRequestDTO.class);
    PaymentOptionRequestDTO paymentOptionRequestDTO = podamFactory.manufacturePojo(PaymentOptionRequestDTO.class);
    InstallmentRequestDTO installmentRequestDTO = podamFactory.manufacturePojo(InstallmentRequestDTO.class);
    paymentOptionRequestDTO.setInstallments(List.of(installmentRequestDTO));
    debtPositionRequestDTO.setPaymentOptions(List.of(paymentOptionRequestDTO));
    CieFieldValuesDTO cieFieldValuesDTO = podamFactory.manufacturePojo(CieFieldValuesDTO.class);
    cieFieldValuesDTO.setPayerFiscalCode(null);

    Mockito.when(jsonMapperMock.convertValue(debtPositionRequestDTO.getFieldValues(), CieFieldValuesDTO.class)).thenReturn(cieFieldValuesDTO);
    Mockito.when(validatorMock.validate(cieFieldValuesDTO)).thenReturn(Set.of(violationMock));

    DebtPositionInvalidFieldValuesException debtPositionInvalidFieldValuesException = Assertions.assertThrows(DebtPositionInvalidFieldValuesException.class, () -> mapper.map(debtPositionRequestDTO, debtPositionTypeOrgCode));

    Assertions.assertEquals("DEBT_POSITION_FIELD_VALUES_MISSING",debtPositionInvalidFieldValuesException.getCode());
  }

  @Test
  void givenExceptionConvertingFieldValuesWhenMapThenDebtPositionInvalidFieldValuesException(){
    String debtPositionTypeOrgCode = "debtPositionTypeOrgCode";
    DebtPositionRequestDTO debtPositionRequestDTO = podamFactory.manufacturePojo(DebtPositionRequestDTO.class);
    PaymentOptionRequestDTO paymentOptionRequestDTO = podamFactory.manufacturePojo(PaymentOptionRequestDTO.class);
    InstallmentRequestDTO installmentRequestDTO = podamFactory.manufacturePojo(InstallmentRequestDTO.class);
    paymentOptionRequestDTO.setInstallments(List.of(installmentRequestDTO));
    debtPositionRequestDTO.setPaymentOptions(List.of(paymentOptionRequestDTO));

    Mockito.when(jsonMapperMock.convertValue(debtPositionRequestDTO.getFieldValues(), CieFieldValuesDTO.class))
      .thenThrow(new IllegalArgumentException());


    DebtPositionInvalidFieldValuesException debtPositionInvalidFieldValuesException = Assertions.assertThrows(DebtPositionInvalidFieldValuesException.class, () -> mapper.map(debtPositionRequestDTO, debtPositionTypeOrgCode));

    Assertions.assertEquals("DEBT_POSITION_FIELD_VALUES_INVALID",debtPositionInvalidFieldValuesException.getCode());
  }

  @Test
  void givenNoDebtPositionTypeOrgCodeWhenMapThenNull(){
    DebtPositionRequestDTO debtPositionRequestDTO = podamFactory.manufacturePojo(DebtPositionRequestDTO.class);
    PaymentOptionRequestDTO paymentOptionRequestDTO = podamFactory.manufacturePojo(PaymentOptionRequestDTO.class);
    InstallmentRequestDTO installmentRequestDTO = podamFactory.manufacturePojo(InstallmentRequestDTO.class);
    paymentOptionRequestDTO.setInstallments(List.of(installmentRequestDTO));
    debtPositionRequestDTO.setPaymentOptions(List.of(paymentOptionRequestDTO));

    InvalidRequestBodyException invalidRequestBodyException = Assertions.assertThrows(InvalidRequestBodyException.class,
      () -> mapper.map(debtPositionRequestDTO, null));

    Assertions.assertEquals("DEBT_POSITION_REQUEST_BODY_INVALID",invalidRequestBodyException.getCode());
  }

  @Test
  void givenNoInstallmentsWhenMapThenNull(){
    String debtPositionTypeOrgCode = "debtPositionTypeOrgCode";
    DebtPositionRequestDTO debtPositionRequestDTO = podamFactory.manufacturePojo(DebtPositionRequestDTO.class);
    PaymentOptionRequestDTO paymentOptionRequestDTO = podamFactory.manufacturePojo(PaymentOptionRequestDTO.class);
    paymentOptionRequestDTO.setInstallments(Collections.emptyList());
    debtPositionRequestDTO.setPaymentOptions(List.of(paymentOptionRequestDTO));

    InvalidRequestBodyException invalidRequestBodyException = Assertions.assertThrows(InvalidRequestBodyException.class,
      () -> mapper.map(debtPositionRequestDTO, debtPositionTypeOrgCode));

    Assertions.assertEquals("DEBT_POSITION_REQUEST_BODY_INVALID",invalidRequestBodyException.getCode());
  }

  @Test
  void givenNoPaymentOptionWhenMapThenNull(){
    String debtPositionTypeOrgCode = "debtPositionTypeOrgCode";
    DebtPositionRequestDTO debtPositionRequestDTO = podamFactory.manufacturePojo(DebtPositionRequestDTO.class);
    debtPositionRequestDTO.setPaymentOptions(Collections.emptyList());

    InvalidRequestBodyException invalidRequestBodyException = Assertions.assertThrows(InvalidRequestBodyException.class,
      () -> mapper.map(debtPositionRequestDTO, debtPositionTypeOrgCode));

    Assertions.assertEquals("DEBT_POSITION_REQUEST_BODY_INVALID",invalidRequestBodyException.getCode());
  }

  @Test
  void givenNoDebtPositionRequestDTOWhenMapThenNull(){
    String debtPositionTypeOrgCode = "debtPositionTypeOrgCode";

    InvalidRequestBodyException invalidRequestBodyException = Assertions.assertThrows(InvalidRequestBodyException.class,
      () -> mapper.map(null, debtPositionTypeOrgCode));

    Assertions.assertEquals("DEBT_POSITION_REQUEST_BODY_INVALID",invalidRequestBodyException.getCode());
  }
}
