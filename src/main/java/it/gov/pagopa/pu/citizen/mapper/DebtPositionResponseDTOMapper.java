package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionResponseDTO;
import it.gov.pagopa.pu.citizen.dto.generated.PaymentDetailsDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.PaymentOptionDTO;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DebtPositionResponseDTOMapper {

  @Mapping(target = "organizationId", source = "organization.organizationId")
  @Mapping(target = "orgFiscalCode", source = "organization.orgFiscalCode")
  @Mapping(target = "orgName", source = "organization.orgName")
  @Mapping(target = "paymentDetails", expression = "java(mapFromDebtPosition(debtPositionDTO))")
  DebtPositionResponseDTO map(DebtPositionDTO debtPositionDTO, Organization organization);

  PaymentDetailsDTO map(InstallmentDTO installmentDTO);

  default PaymentDetailsDTO mapFromDebtPosition(DebtPositionDTO source) {
    if (source == null || source.getPaymentOptions().isEmpty()) {
      return null;
    }

    PaymentOptionDTO firstOption = source.getPaymentOptions().getFirst();
    if (firstOption.getInstallments().isEmpty()) {
      return null;
    }

    InstallmentDTO firstInstallment = firstOption.getInstallments().getFirst();
    return map(firstInstallment);
  }
}
