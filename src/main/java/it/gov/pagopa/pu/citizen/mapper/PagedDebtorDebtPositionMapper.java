package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.DebtorPaymentOptionDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtorUnpaidDebtPositionDTO;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorDebtPositionDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.BaseInstallment;
import it.gov.pagopa.pu.debtpositions.dto.generated.BasePaymentOption;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtorDebtPositionDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.PagedDebtorUnpaidDebtPositionDTO;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mapper(componentModel = "spring")
public interface PagedDebtorDebtPositionMapper {

  @Mapping(target = "content", expression = "java(source.getContent() != null ? mapDebtorDebtPositionDTOWithOrganizationAndPagedDebtorUnpaidDebtPositionDTO(organizationsMap, source) : java.util.Collections.emptyList())")
  @Mapping(target = "totalPages", source = "source.totalPages")
  @Mapping(target = "size", source = "source.size")
  @Mapping(target = "number", source = "source.number")
  @Mapping(target = "totalElements", source = "source.totalElements")
  PagedDebtorDebtPositionDTO map(Map<Long, Organization> organizationsMap, PagedDebtorUnpaidDebtPositionDTO source);

  @Mapping(target = "organizationId", source = "organization.organizationId")
  @Mapping(target = "orgName", source = "organization.orgName")
  @Mapping(target = "orgFiscalCode", source = "organization.orgFiscalCode")
  @Mapping(target = "status", source = "debtorDebtPosition.status")
  @Mapping(target = "debtPositionDescription", source = "debtorDebtPosition.debtPositionDescription")
  @Mapping(target = "paymentOptions", expression = "java(mapPaymentOptions(debtorDebtPosition.getPaymentOptions()))")
  DebtorUnpaidDebtPositionDTO map(Organization organization, DebtorDebtPositionDTO debtorDebtPosition);

  @Mapping(target = "paymentOptionType", source = "paymentOptionType")
  @Mapping(target = "dueDate", expression = "java(calculateDueDate(paymentOption))")
  @Mapping(target = "totalAmountCents", expression = "java(calculateTotalAmountCents(paymentOption))")
  DebtorPaymentOptionDTO map(BasePaymentOption paymentOption);


  default List<DebtorUnpaidDebtPositionDTO> mapDebtorDebtPositionDTOWithOrganizationAndPagedDebtorUnpaidDebtPositionDTO(Map<Long, Organization> organizationsMap, PagedDebtorUnpaidDebtPositionDTO source){
    List<DebtorDebtPositionDTO> debtorDebtPositionDTOList = source.getContent();
    return debtorDebtPositionDTOList.stream()
      .map(debtPosition -> map(retrieveOrganization(organizationsMap, debtPosition), debtPosition)).toList();
  }

  default Organization retrieveOrganization(Map<Long, Organization> organizationsMap, DebtorDebtPositionDTO debtorDebtPositionDTO){
    Organization organization = organizationsMap.get(debtorDebtPositionDTO.getOrganizationId());
    if (organization == null){
      throw new IllegalStateException("No Organization found for debtPosition with Id %d".formatted(debtorDebtPositionDTO.getDebtPositionId()));
    }
    return organization;
  }

  default LocalDate calculateDueDate(BasePaymentOption paymentOption) {
    return calculateDueDate(List.of(paymentOption));
  }

  default Long calculateTotalAmountCents(BasePaymentOption paymentOption) {
    return calculateTotalAmountCents(List.of(paymentOption));
  }


  default Long calculateTotalAmountCents(List<BasePaymentOption> paymentOptions) {
    if (paymentOptions == null || paymentOptions.isEmpty()) {
      return null;
    }

    if (paymentOptions.size() == 1) {
      return paymentOptions.getFirst().getInstallments()
        .stream()
        .mapToLong(BaseInstallment::getAmountCents)
        .sum();
    }

    Long firstAmount = paymentOptions.getFirst().getTotalAmountCents();

    boolean allSameAmount = paymentOptions.stream()
      .allMatch(po -> Objects.equals(po.getTotalAmountCents(), firstAmount));

    return allSameAmount ? firstAmount : null;
  }

  default List<DebtorPaymentOptionDTO> mapPaymentOptions(List<BasePaymentOption> paymentOptions) {
    if (paymentOptions == null) {
      return Collections.emptyList();
    }

    return paymentOptions.stream()
      .map(this::map)
      .toList();
  }

  default LocalDate calculateDueDate(List<BasePaymentOption> paymentOptions) {
    if (paymentOptions == null || paymentOptions.isEmpty()) {
      return null;
    }

    return paymentOptions.stream()
      .flatMap(po -> po.getInstallments().stream())
      .map(BaseInstallment::getDueDate)
      .filter(Objects::nonNull)
      .min(LocalDate::compareTo)
      .orElse(null);
  }

}
