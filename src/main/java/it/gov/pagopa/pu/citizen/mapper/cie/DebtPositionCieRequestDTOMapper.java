package it.gov.pagopa.pu.citizen.mapper.cie;

import it.gov.pagopa.pu.cie.dto.generated.DebtPositionCieOriginAllowedEnum;
import it.gov.pagopa.pu.cie.dto.generated.DebtPositionCieRequestDTO;
import it.gov.pagopa.pu.citizen.dto.cie.CieFieldValuesDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.InstallmentRequestDTO;
import it.gov.pagopa.pu.citizen.exception.DebtPositionInvalidFieldValuesException;
import it.gov.pagopa.pu.debtpositions.dto.generated.PersonDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;
import java.util.Set;

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
      return null;
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
    result.setPayer(buildPayer(cieFieldValuesDTO));
    result.setOrgFiscalCode(cieFieldValuesDTO.getOrgFiscalCode());
    return result;
  }

  private void validateFieldValues(CieFieldValuesDTO cieFieldValuesDTO) {
    Set<ConstraintViolation<CieFieldValuesDTO>> violations = validator.validate(cieFieldValuesDTO);
    if(!violations.isEmpty()){
      throw new DebtPositionInvalidFieldValuesException("DEBT_POSITION_FIELD_VALUES_MISSING","Required fields missing from CieFieldValuesDTO");
    }
  }

  private CieFieldValuesDTO parseFieldValues(Map<String, Object> fieldValues) {
    try {
      return jsonMapper.convertValue(fieldValues, CieFieldValuesDTO.class);
    } catch (IllegalArgumentException e) {
      throw new DebtPositionInvalidFieldValuesException("DEBT_POSITION_FIELD_VALUES_INVALID","Could not convert fieldValues to CieFieldValuesDTO");
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
}
