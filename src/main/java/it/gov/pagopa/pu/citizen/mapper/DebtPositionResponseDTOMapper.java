package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionResponseDTO;
import it.gov.pagopa.pu.citizen.dto.generated.PaymentDetailsDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.PaymentOptionDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.TransferDTO;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.util.CollectionUtils;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface DebtPositionResponseDTOMapper {

  @Mapping(target = "organizationId", expression = "java(organization!=null?organization.getOrganizationId():debtPositionDTO.getOrganizationId())")
  @Mapping(target = "orgFiscalCode", expression = "java(mapOrgFiscalCode(debtPositionDTO, organization, delegate))")
  @Mapping(target = "orgName", expression = "java(mapOrgName(debtPositionDTO, organization, delegate))")
  @Mapping(target = "paymentDetails", expression = "java(mapFromDebtPosition(debtPositionDTO))")
  DebtPositionResponseDTO map(DebtPositionDTO debtPositionDTO, Organization organization, boolean delegate);

  PaymentDetailsDTO map(InstallmentDTO installmentDTO);

  default PaymentDetailsDTO mapFromDebtPosition(DebtPositionDTO source) {
    return map(getFirstInstallment(source));
  }

  private static InstallmentDTO getFirstInstallment(DebtPositionDTO source) {
    if (source == null || source.getPaymentOptions().isEmpty()) {
      return null;
    }

    PaymentOptionDTO firstOption = source.getPaymentOptions().getFirst();
    if (firstOption.getInstallments().isEmpty()) {
      return null;
    }

    return firstOption.getInstallments().getFirst();
  }

  private static TransferDTO findOwnerTransfer(DebtPositionDTO debtPositionDTO){
    InstallmentDTO installment = getFirstInstallment(debtPositionDTO);
    return Optional.ofNullable(installment)
      .map(InstallmentDTO::getTransfers)
      .filter(t -> !CollectionUtils.isEmpty(t))
      .flatMap(transfers -> transfers.stream()
        .filter(t -> Boolean.TRUE.equals(t.getFlagOwner()))
        .findFirst())
      .orElseThrow(()->new IllegalStateException("[TRANSFER_OWNER_NOT_FOUND] Could not find owner's transfer"));
  }

  default String mapOrgFiscalCode(DebtPositionDTO debtPositionDTO, Organization organization, boolean delegate){
    if (!delegate) {
      return organization.getOrgFiscalCode();
    }
    return findOwnerTransfer(debtPositionDTO).getOrgFiscalCode();
  }

  default String mapOrgName(DebtPositionDTO debtPositionDTO, Organization organization, boolean delegate){
    if (!delegate) {
      return organization.getOrgName();
    }
    return findOwnerTransfer(debtPositionDTO).getOrgName();
  }
}
