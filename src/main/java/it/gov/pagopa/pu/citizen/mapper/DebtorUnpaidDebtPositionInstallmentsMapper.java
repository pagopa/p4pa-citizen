package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.DebtorUnpaidDebtPositionInstallmentsDTO;
import it.gov.pagopa.pu.citizen.utils.InstallmentUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentNoPII;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = "spring", imports = {InstallmentUtils.class})
public interface DebtorUnpaidDebtPositionInstallmentsMapper {

  @Mapping(target = "organizationId", source = "organization.organizationId")
  @Mapping(target = "orgName", source = "organization.orgName")
  @Mapping(target = "orgFiscalCode", source = "organization.orgFiscalCode")
  @Mapping(target = "status", expression = "java(InstallmentUtils.resolveInstallmentStatus(installment.getStatus()))")
  DebtorUnpaidDebtPositionInstallmentsDTO map(Organization organization, InstallmentNoPII installment, Long debtPositionId);

  default List<DebtorUnpaidDebtPositionInstallmentsDTO> mapDebtorUnpaidDebtPositionInstallmentsList(Organization organization, List<InstallmentNoPII> installments, Long debtPositionId){
    return installments.stream().map(installmentNoPII -> map(organization, installmentNoPII, debtPositionId))
      .sorted(Comparator.comparing(DebtorUnpaidDebtPositionInstallmentsDTO::getDueDate,
        Comparator.nullsLast(Comparator.naturalOrder())))
      .toList();
  }

}

