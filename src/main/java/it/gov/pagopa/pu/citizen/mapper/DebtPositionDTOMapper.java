package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.InstallmentRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.PaymentOptionRequestDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.PaymentOptionDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DebtPositionDTOMapper {

  @Mapping(target = "flagPuPagoPaPayment", constant = "true")
  @Mapping(target = "debtPositionOrigin", constant = "SPONTANEOUS")
  @Mapping(target = "status", constant = "UNPAID")
  DebtPositionDTO mapSpontaneousDebtPositionDTO(DebtPositionRequestDTO debtPositionRequestDTO, @Context Integer dueDateOffsetDays);

  @Mapping(target = "paymentOptionType", constant = "SINGLE_INSTALLMENT")
  @Mapping(target = "status", constant = "UNPAID")
  @Mapping(target = "paymentOptionIndex", constant = "1")
  PaymentOptionDTO mapSpontaneousPaymentOptionDTO(PaymentOptionRequestDTO paymentOptionRequestDTO, @Context Integer dueDateOffsetDays);

  @Mapping(target = "generateNotice", constant = "true")
  @Mapping(target = "status", constant = "UNPAID")
  @Mapping(target = "dueDate", expression = "java(dueDateOffsetDays != null ? java.time.LocalDate.now().plusDays(dueDateOffsetDays) : java.time.LocalDate.now())")
  InstallmentDTO mapSpontaneousInstallmentDTO(InstallmentRequestDTO installmentRequestDTO, @Context Integer dueDateOffsetDays);

}
