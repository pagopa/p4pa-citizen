package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.DebtorDebtPositionDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtorPaymentOptionDTO;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorDebtPositionDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionView;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentNoPII;
import it.gov.pagopa.pu.debtpositions.dto.generated.PagedModelDebtPositionView;
import it.gov.pagopa.pu.debtpositions.dto.generated.PaymentOption;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mapper(componentModel = "spring")
public interface PagedDebtorDebtPositionMapper {

  @Mapping(target = "content", expression = "java(pagedModelDebtPositionView.getEmbedded() != null ? mapDebtorDebtPositionDTOWithOrganizationAndPaymentOptions(organizationsMap, pagedModelDebtPositionView.getEmbedded().getDebtPositionViews(), paymentOptionsMap, installmentsMap) : java.util.Collections.emptyList())")
  @Mapping(target = "totalPages", source = "pagedModelDebtPositionView.page.totalPages")
  @Mapping(target = "size", source = "pagedModelDebtPositionView.page.size")
  @Mapping(target = "number", source = "pagedModelDebtPositionView.page.number")
  @Mapping(target = "totalElements", source = "pagedModelDebtPositionView.page.totalElements")
  PagedDebtorDebtPositionDTO map(Map<Long, Organization> organizationsMap, PagedModelDebtPositionView pagedModelDebtPositionView, Map<Long, List<PaymentOption>> paymentOptionsMap, Map<Long,List<InstallmentNoPII>> installmentsMap);

  @Mapping(target = "organizationId", source = "organization.organizationId")
  @Mapping(target = "orgName", source = "organization.orgName")
  @Mapping(target = "orgFiscalCode", source = "organization.orgFiscalCode")
  @Mapping(target = "debtPositionDescription", source = "debtPositionView.description")
  @Mapping(target = "paymentOptions", expression = "java(mapPaymentOptions(paymentOptions, installments))")
  DebtorDebtPositionDTO map(Organization organization, DebtPositionView debtPositionView, List<PaymentOption> paymentOptions, List<InstallmentNoPII> installments);

  @Mapping(target = "dueDate", expression = "java(calculateDueDate(List.of(paymentOption), installments))")
  @Mapping(target = "totalAmountCents", expression = "java(calculateTotalAmountCents(List.of(paymentOption), installments))")
  DebtorPaymentOptionDTO map(PaymentOption paymentOption, List<InstallmentNoPII> installments);

  default List<DebtorDebtPositionDTO> mapDebtorDebtPositionDTOWithOrganizationAndPaymentOptions(Map<Long, Organization> organizationsMap, List<DebtPositionView> debtPositionViews, Map<Long, List<PaymentOption>> paymentOptionsMap, Map<Long,List<InstallmentNoPII>> installmentsMap){
    return debtPositionViews.stream()
      .map(debtPositionView -> map(retrieveOrganization(organizationsMap, debtPositionView), debtPositionView, retrievePaymentOptions(paymentOptionsMap,debtPositionView), retrieveInstallments(installmentsMap,debtPositionView))).toList();
  }

  default Organization retrieveOrganization(Map<Long, Organization> organizationsMap, DebtPositionView debtPositionView){
    Organization organization = organizationsMap.get(debtPositionView.getOrganizationId());
    if (organization == null){
      throw new IllegalStateException("No Organization found for debtPosition with Id %d".formatted(debtPositionView.getDebtPositionId()));
    }
    return organization;
  }

  default List<PaymentOption> retrievePaymentOptions( Map<Long, List<PaymentOption>> paymentOptionsMap, DebtPositionView debtPositionView){
    List<PaymentOption> paymentOptions = paymentOptionsMap.get(debtPositionView.getDebtPositionId());
    if (paymentOptions == null){
      throw new IllegalStateException("No PaymentOptions found for debtPosition with Id %d".formatted(debtPositionView.getDebtPositionId()));
    }
    return paymentOptions;
  }

  default List<InstallmentNoPII> retrieveInstallments( Map<Long, List<InstallmentNoPII>> installmentsMap, DebtPositionView debtPositionView){
    List<InstallmentNoPII> installments = installmentsMap.get(debtPositionView.getDebtPositionId());
    if (installments == null){
      throw new IllegalStateException("No Installments found for debtPosition with Id %d".formatted(debtPositionView.getDebtPositionId()));
    }
    return installments;
  }

  default Long calculateTotalAmountCents(List<PaymentOption> paymentOptions, List<InstallmentNoPII> installments) {

    if (paymentOptions == null || paymentOptions.isEmpty()) {
      return null;
    }

    if (paymentOptions.size() == 1) {
      return installments.stream()
        .mapToLong(InstallmentNoPII::getAmountCents)
        .sum();
    }

    Long firstAmount = paymentOptions.getFirst().getTotalAmountCents();

    boolean allSameAmount = paymentOptions.stream()
      .allMatch(po -> Objects.equals(po.getTotalAmountCents(), firstAmount));

    return allSameAmount ? firstAmount : null;
  }

  default List<DebtorPaymentOptionDTO> mapPaymentOptions(List<PaymentOption> paymentOptions,
                                                         List<InstallmentNoPII> installments) {
    return paymentOptions.stream()
      .map(po -> map(po, installments))
      .toList();
  }

  default LocalDate calculateDueDate(List<PaymentOption> paymentOptions, List<InstallmentNoPII> installments) {

    if (installments == null || installments.isEmpty()) {
      return null;
    }

    return installments.stream()
      .map(InstallmentNoPII::getDueDate)
      .filter(Objects::nonNull)
      .min(LocalDate::compareTo)
      .orElse(null);
  }

}
