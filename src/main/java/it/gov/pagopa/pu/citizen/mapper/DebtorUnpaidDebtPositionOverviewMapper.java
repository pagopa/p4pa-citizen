package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.DebtorInstallmentsOverviewDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtorPaymentOptionOverviewDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtorUnpaidDebtPositionOverviewDTO;
import it.gov.pagopa.pu.citizen.utils.InstallmentUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.BaseInstallment;
import it.gov.pagopa.pu.debtpositions.dto.generated.BasePaymentOption;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtorDebtPositionDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.PostalIbanVerifyResponse;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.OffsetDateTime;
import java.util.Map;

@Mapper(componentModel = "spring", imports = {InstallmentUtils.class})
public interface DebtorUnpaidDebtPositionOverviewMapper {

  @Mapping(target = "organizationId", source = "organization.organizationId")
  @Mapping(target = "orgName", source = "organization.orgName")
  @Mapping(target = "orgFiscalCode", source = "organization.orgFiscalCode")
  @Mapping(target = "iupd", source = "debtorDebtPosition.iupdOrg")
  @Mapping(target = "status", source = "debtorDebtPosition.status")
  @Mapping(target = "debtPositionDescription", source = "debtorDebtPosition.debtPositionDescription")
  DebtorUnpaidDebtPositionOverviewDTO map(Organization organization, DebtorDebtPositionDTO debtorDebtPosition, @Context Map<Long, OffsetDateTime> installmentIdAndPaymentDateTimeMap, @Context PostalIbanVerifyResponse postalIbanVerifyResponse);

  DebtorPaymentOptionOverviewDTO map(BasePaymentOption paymentOption, @Context Map<Long, OffsetDateTime> installmentIdAndPaymentDateTimeMap, @Context Map<String, Boolean> postalIbanVerifyResponse);

  @Mapping(
    target = "paymentDateTime", source = "installmentId", qualifiedByName = "extractPaymentDateTime")
  @Mapping(target = "status", expression = "java(InstallmentUtils.resolveInstallmentStatus(installment.getStatus()))")
  @Mapping(target = "allCCP", expression = "java(postalIbanVerifyResponse != null ? InstallmentUtils.extractAllCCP(installment.getInstallmentId(), postalIbanVerifyResponse) : null)")
  DebtorInstallmentsOverviewDTO map(BaseInstallment installment, @Context Map<Long, OffsetDateTime> installmentIdAndPaymentDateTimeMap, @Context PostalIbanVerifyResponse postalIbanVerifyResponse);

  @Named("extractPaymentDateTime")
  default OffsetDateTime extractPaymentDateTime(Long installmentId, @Context Map<Long, OffsetDateTime> installmentIdAndPaymentDateTimeMap) {
    if (installmentIdAndPaymentDateTimeMap == null){
      return null;
    }
    return installmentIdAndPaymentDateTimeMap.get(installmentId);
  }
}
