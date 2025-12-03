package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.*;
import it.gov.pagopa.pu.debtpositions.dto.generated.BaseInstallment;
import it.gov.pagopa.pu.debtpositions.dto.generated.BasePaymentOption;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtorDebtPositionDTO;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface DebtorUnpaidDebtPositionMapper {

  @Mapping(target = "organizationId", source = "organization.organizationId")
  @Mapping(target = "orgName", source = "organization.orgName")
  @Mapping(target = "orgFiscalCode", source = "organization.orgFiscalCode")
  @Mapping(target = "iupd", source = "debtorDebtPosition.iupdOrg")
  @Mapping(target = "status", source = "debtorDebtPosition.status")
  @Mapping(target = "debtPositionDescription", source = "debtorDebtPosition.debtPositionDescription")
  @Mapping(target = "paymentOptions", expression = "java(mapPaymentOptions(debtorDebtPosition.getPaymentOptions()))")
  DebtorUnpaidDebtPositionOverviewDTO map(Organization organization, DebtorDebtPositionDTO debtorDebtPosition);

  @Mapping(target = "installments", expression = "java(mapInstallments(paymentOption.getInstallments()))")
  DebtorPaymentOptionOverviewDTO map(BasePaymentOption paymentOption);

  DebtorInstallmentsOverviewDTO map(BaseInstallment installment);

  default List<DebtorPaymentOptionOverviewDTO> mapPaymentOptions(List<BasePaymentOption> paymentOptions) {
    if (paymentOptions == null) {
      return Collections.emptyList();
    }

    return paymentOptions.stream()
      .map(this::map)
      .toList();
  }

  default List<DebtorInstallmentsOverviewDTO> mapInstallments(List<BaseInstallment> installments) {
    if (installments == null) {
      return Collections.emptyList();
    }

    return installments.stream()
      .map(this::map)
      .toList();
  }

}
