package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.InstallmentRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.PaymentOptionRequestDTO;
import it.gov.pagopa.pu.citizen.utils.DebtPositionConstants;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.PaymentOptionDTO;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface DebtPositionDTOMapper {

  @Mapping(target = "flagPuPagoPaPayment", constant = "true")
  @Mapping(target = "debtPositionOrigin", constant = "SPONTANEOUS")
  @Mapping(target = "status", constant = "UNPAID")
  @Mapping(target = "description", expression = ("java(it.gov.pagopa.pu.citizen.utils.DebtPositionConstants.DEBT_POSITION_DESCRIPTION_PLACEHOLDER)"))
  DebtPositionDTO mapSpontaneousDebtPositionDTO(DebtPositionRequestDTO debtPositionRequestDTO, @Context Integer expirationDays);

  @Mapping(target = "paymentOptionType", constant = "SINGLE_INSTALLMENT")
  @Mapping(target = "status", constant = "UNPAID")
  @Mapping(target = "paymentOptionIndex", constant = "1")
  PaymentOptionDTO mapSpontaneousPaymentOptionDTO(PaymentOptionRequestDTO paymentOptionRequestDTO, @Context Integer expirationDays);

  @Mapping(target = "generateNotice", constant = "true")
  @Mapping(target = "status", constant = "UNPAID")
  @Mapping(target = "switchToExpired", constant = "true")
  @Mapping(target = "dueDate", expression = "java(expirationDays != null ? java.time.LocalDate.now().plusDays(expirationDays) : java.time.LocalDate.now())")
  @Mapping(target = "originalRemittanceInformation", source = "remittanceInformation")
  @Mapping(target = "remittanceInformation", source = "userRemittanceInformation", qualifiedByName = "userRemittanceInformation")
  InstallmentDTO mapSpontaneousInstallmentDTO(InstallmentRequestDTO installmentRequestDTO, @Context Integer expirationDays);

  @Named("userRemittanceInformation")
  default String mapUserRemittanceInformation(String userRemittanceInformation){
    return StringUtils.isNotBlank(userRemittanceInformation)?
      DebtPositionConstants.INSTALLMENT_REMITTANCE_INFORMATION_PLACEHOLDER + " " + userRemittanceInformation
      : DebtPositionConstants.INSTALLMENT_REMITTANCE_INFORMATION_PLACEHOLDER;
  }
}
