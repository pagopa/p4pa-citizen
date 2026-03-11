package it.gov.pagopa.pu.citizen.mapper.cie;

import io.micrometer.common.util.StringUtils;
import it.gov.pagopa.pu.cie.dto.generated.DebtPositionCieOriginAllowedEnum;
import it.gov.pagopa.pu.cie.dto.generated.DebtPositionCieRequestDTO;
import it.gov.pagopa.pu.citizen.dto.cie.CieFieldValuesDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.InstallmentRequestDTO;
import it.gov.pagopa.pu.citizen.exception.DebtPositionInvalidFieldValuesException;
import it.gov.pagopa.pu.citizen.exception.InvalidRequestBodyException;
import it.gov.pagopa.pu.debtpositions.dto.generated.PersonDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DebtPositionCieRequestDTOMapper {
  private final JsonMapper jsonMapper;
  private final Validator validator;

  public DebtPositionCieRequestDTOMapper(JsonMapper jsonMapper, Validator validator) {
    this.jsonMapper = jsonMapper;
    this.validator = validator;
  }

  public DebtPositionCieRequestDTO map(DebtPositionRequestDTO dto, String debtPositionTypeOrgCode) {
    if (!isValid(dto, debtPositionTypeOrgCode)) {
      throw new InvalidRequestBodyException("INVALID_DEBT_POSITION_REQUEST_BODY","Invalid DebtPositionRequestDTO. Missing installments or debtPositionTypeOrgCode");
    }
    InstallmentRequestDTO installment = extractFirstInstallment(dto);
    return buildDebtPositionCieRequest(installment, dto.getFieldValues(), debtPositionTypeOrgCode);
  }

  private DebtPositionCieRequestDTO buildDebtPositionCieRequest(InstallmentRequestDTO installment,
      Map<String, Object> fieldValues, String debtPositionTypeOrgCode) {
    DebtPositionCieRequestDTO result = new DebtPositionCieRequestDTO();
    result.setOrigin(DebtPositionCieOriginAllowedEnum.SPONTANEOUS);
    result.setDebtPositionTypeOrgCode(debtPositionTypeOrgCode);
    result.setDebtor(installment.getDebtor());
    result.setRemittanceInformation(installment.getRemittanceInformation());

    CieFieldValuesDTO cieFieldValuesDTO = parseFieldValues(fieldValues);
    validateFieldValues(cieFieldValuesDTO);
    if(hasPayerFields(cieFieldValuesDTO)){
      validatePayerFields(cieFieldValuesDTO);
      result.setPayer(buildPayer(cieFieldValuesDTO));
    }
    result.setOrgFiscalCode(cieFieldValuesDTO.getOrgFiscalCode());
    return result;
  }

  private void validateFieldValues(CieFieldValuesDTO cieFieldValuesDTO) {
    Set<ConstraintViolation<CieFieldValuesDTO>> violations = validator.validate(cieFieldValuesDTO);
    if(!violations.isEmpty()){
      String violationsDescription = violations
        .stream()
        .map(e -> " " + e.getPropertyPath() + ": " + e.getMessage())
        .sorted()
        .collect(Collectors.joining(";"));
      throw new DebtPositionInvalidFieldValuesException("MISSING_DEBT_POSITION_FIELD_VALUES", "Required fields missing from CieFieldValuesDTO. [%s]".formatted(violationsDescription));
    }
  }

  private CieFieldValuesDTO parseFieldValues(Map<String, Object> fieldValues) {
    try {
      return jsonMapper.convertValue(fieldValues, CieFieldValuesDTO.class);
    } catch (IllegalArgumentException e) {
      throw new DebtPositionInvalidFieldValuesException("INVALID_DEBT_POSITION_FIELD_VALUES","Could not convert fieldValues to CieFieldValuesDTO");
    }
  }

  private PersonDTO buildPayer(CieFieldValuesDTO cieFieldValuesDTO) {
    return PersonDTO.builder()
      .entityType(cieFieldValuesDTO.getPayerEntityType())
      .fiscalCode(cieFieldValuesDTO.getPayerFiscalCode())
      .fullName(cieFieldValuesDTO.getPayerFullName())
      .address(cieFieldValuesDTO.getPayerAddress())
      .civic(cieFieldValuesDTO.getPayerCivic())
      .postalCode(cieFieldValuesDTO.getPayerPostalCode())
      .location(cieFieldValuesDTO.getPayerLocation())
      .province(cieFieldValuesDTO.getPayerProvince())
      .nation(cieFieldValuesDTO.getPayerNation())
      .email(cieFieldValuesDTO.getPayerEmail())
      .build();
  }

  private InstallmentRequestDTO extractFirstInstallment(DebtPositionRequestDTO dto) {
    return dto.getPaymentOptions().getFirst().getInstallments().getFirst();
  }

  private boolean isValid(DebtPositionRequestDTO dto, String debtPositionTypeOrgCode) {
    return dto != null
      && !CollectionUtils.isEmpty(dto.getPaymentOptions())
      && !CollectionUtils.isEmpty(dto.getPaymentOptions().getFirst().getInstallments())
      && debtPositionTypeOrgCode != null;
  }

  private boolean hasPayerFields(CieFieldValuesDTO cieFieldValuesDTO) {
    return cieFieldValuesDTO.getPayerEntityType() != null
      || StringUtils.isNotBlank(cieFieldValuesDTO.getPayerFiscalCode())
      || StringUtils.isNotBlank(cieFieldValuesDTO.getPayerFullName())
      || StringUtils.isNotBlank(cieFieldValuesDTO.getPayerAddress())
      || StringUtils.isNotBlank(cieFieldValuesDTO.getPayerCivic())
      || StringUtils.isNotBlank(cieFieldValuesDTO.getPayerPostalCode())
      || StringUtils.isNotBlank(cieFieldValuesDTO.getPayerLocation())
      || StringUtils.isNotBlank(cieFieldValuesDTO.getPayerProvince())
      || StringUtils.isNotBlank(cieFieldValuesDTO.getPayerNation())
      || StringUtils.isNotBlank(cieFieldValuesDTO.getPayerEmail());
  }

  private void validatePayerFields(CieFieldValuesDTO cieFieldValuesDTO) {
    if (cieFieldValuesDTO.getPayerEntityType() == null
      || StringUtils.isBlank(cieFieldValuesDTO.getPayerFiscalCode())
      || StringUtils.isBlank(cieFieldValuesDTO.getPayerFullName())) {
      throw new DebtPositionInvalidFieldValuesException(
        "INVALID_DEBT_POSITION_FIELD_VALUES",
        "Payer fields entityType, fiscalCode and fullName are required when any payer field is provided"
      );
    }
  }
}
