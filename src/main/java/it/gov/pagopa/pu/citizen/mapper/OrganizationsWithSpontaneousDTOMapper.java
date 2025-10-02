package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.OrganizationsWithSpontaneousDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.CollectionModelDebtPositionTypeOrgWithActiveSpontaneousCount;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionTypeOrgWithActiveSpontaneousCount;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.organization.dto.generated.PagedModelOrganization;
import org.mapstruct.Mapper;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrganizationsWithSpontaneousDTOMapper {

  List<OrganizationsWithSpontaneousDTO> map(List<Organization> organizations);

  default List<OrganizationsWithSpontaneousDTO> map(
    PagedModelOrganization source,
    CollectionModelDebtPositionTypeOrgWithActiveSpontaneousCount collection) {

    if (source == null || source.getEmbedded() == null || source.getEmbedded().getOrganizations() == null) {
      return Collections.emptyList();
    }

    List<Organization> filtered = getOrganizationsFiltered(
      source.getEmbedded().getOrganizations(),
      collection
    );

    return map(filtered);
  }

  default List<Organization> getOrganizationsFiltered(
    List<Organization> organizationList,
    CollectionModelDebtPositionTypeOrgWithActiveSpontaneousCount collectionModelDebtPositionTypeOrgWithActiveSpontaneousCount
  ) {

    if (collectionModelDebtPositionTypeOrgWithActiveSpontaneousCount == null ||
      collectionModelDebtPositionTypeOrgWithActiveSpontaneousCount.getEmbedded() == null ||
      collectionModelDebtPositionTypeOrgWithActiveSpontaneousCount.getEmbedded().getDebtPositionTypeOrgWithActiveSpontaneousCounts() == null) {
      return java.util.Collections.emptyList();
    }

    List<Long> validIds = collectionModelDebtPositionTypeOrgWithActiveSpontaneousCount
      .getEmbedded()
      .getDebtPositionTypeOrgWithActiveSpontaneousCounts()
      .stream()
      .map(DebtPositionTypeOrgWithActiveSpontaneousCount::getOrganizationId)
      .toList();

    return organizationList.stream()
      .filter(organization -> validIds.contains(organization.getOrganizationId()))
      .toList();
  }

}
