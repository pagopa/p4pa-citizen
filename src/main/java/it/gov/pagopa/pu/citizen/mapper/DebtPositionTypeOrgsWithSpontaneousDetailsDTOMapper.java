package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDetailsDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtpositions.dto.generated.SpontaneousForm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DebtPositionTypeOrgsWithSpontaneousDetailsDTOMapper {

  @Mapping(target = "code", source = "debtPositionTypeOrg.code")
  @Mapping(target = "organizationId", source = "debtPositionTypeOrg.organizationId")
  @Mapping(target = "formCustom", source = "spontaneousForm")
  @Mapping(target = "formType", expression = "java(debtPositionTypeOrg != null ? calculateFormType(debtPositionTypeOrg) : null)")
  DebtPositionTypeOrgsWithSpontaneousDetailsDTO map(DebtPositionTypeOrg debtPositionTypeOrg, SpontaneousForm spontaneousForm);

  default DebtPositionTypeOrgsWithSpontaneousDetailsDTO.FormTypeEnum calculateFormType(DebtPositionTypeOrg org) {
    boolean hasAmount = org.getAmountCents() != null;
    boolean hasUrl = org.getExternalPaymentUrl() != null;
    boolean hasForm = org.getSpontaneousFormId() != null;

    if (!hasAmount && !hasUrl && !hasForm)
      return DebtPositionTypeOrgsWithSpontaneousDetailsDTO.FormTypeEnum.STANDARD;
    if (hasAmount && !hasUrl && !hasForm)
      return DebtPositionTypeOrgsWithSpontaneousDetailsDTO.FormTypeEnum.PRESET_AMOUNT;
    if (!hasAmount && hasUrl && !hasForm)
      return DebtPositionTypeOrgsWithSpontaneousDetailsDTO.FormTypeEnum.EXTERNAL_URL;
    if (hasForm && !hasAmount && !hasUrl)
      return DebtPositionTypeOrgsWithSpontaneousDetailsDTO.FormTypeEnum.CUSTOM;

    throw new IllegalStateException("Invalid combination of fields in DebtPositionTypeOrg");
  }

}
