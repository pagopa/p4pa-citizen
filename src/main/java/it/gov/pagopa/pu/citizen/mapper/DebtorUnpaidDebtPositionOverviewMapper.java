package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.DebtorInstallmentsOverviewDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtorPaymentOptionOverviewDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtorUnpaidDebtPositionOverviewDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.BaseInstallment;
import it.gov.pagopa.pu.debtpositions.dto.generated.BasePaymentOption;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtorDebtPositionDTO;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DebtorUnpaidDebtPositionOverviewMapper {

  @Mapping(target = "organizationId", source = "organization.organizationId")
  @Mapping(target = "orgName", source = "organization.orgName")
  @Mapping(target = "orgFiscalCode", source = "organization.orgFiscalCode")
  @Mapping(target = "iupd", source = "debtorDebtPosition.iupdOrg")
  @Mapping(target = "status", source = "debtorDebtPosition.status")
  @Mapping(target = "debtPositionDescription", source = "debtorDebtPosition.debtPositionDescription")
  DebtorUnpaidDebtPositionOverviewDTO map(Organization organization, DebtorDebtPositionDTO debtorDebtPosition);

  DebtorPaymentOptionOverviewDTO map(BasePaymentOption paymentOption);

  DebtorInstallmentsOverviewDTO map(BaseInstallment installment);

}
