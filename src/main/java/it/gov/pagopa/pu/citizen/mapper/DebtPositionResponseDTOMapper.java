package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionResponseDTO;
import it.gov.pagopa.pu.citizen.dto.generated.PaymentDetailsDTO;
import it.gov.pagopa.pu.citizen.utils.InstallmentUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.*;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.util.CollectionUtils;

import java.util.Optional;

@Mapper(componentModel = "spring", imports = {InstallmentUtils.class})
public interface DebtPositionResponseDTOMapper {

  @Mapping(target = "organizationId", source = "organization.organizationId")
  @Mapping(target = "orgFiscalCode", expression = "java(mapOrgFiscalCode(debtPositionDTO, organization, delegate))")
  @Mapping(target = "orgName", expression = "java(mapOrgName(debtPositionDTO, organization, delegate))")
  @Mapping(target = "paymentDetails", expression = "java(mapFromDebtPosition(debtPositionDTO, postalIbanVerifyResponse))")
  DebtPositionResponseDTO map(DebtPositionDTO debtPositionDTO, Organization organization, boolean delegate, @Context PostalIbanVerifyResponse postalIbanVerifyResponse);

  @Mapping(target = "allCCP", expression = "java(postalIbanVerifyResponse != null ? InstallmentUtils.extractAllCCP(installmentDTO.getInstallmentId(), postalIbanVerifyResponse) : null)")
  PaymentDetailsDTO map(InstallmentDTO installmentDTO, PostalIbanVerifyResponse postalIbanVerifyResponse);

  default PaymentDetailsDTO mapFromDebtPosition(DebtPositionDTO source, PostalIbanVerifyResponse postalIbanVerifyResponse) {
    return map(getFirstInstallment(source), postalIbanVerifyResponse);
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
