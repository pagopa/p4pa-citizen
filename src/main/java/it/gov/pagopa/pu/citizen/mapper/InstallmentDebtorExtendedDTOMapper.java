package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.InstallmentDebtorExtendedDTO;
import it.gov.pagopa.pu.citizen.utils.InstallmentUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentDebtorDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.PostalIbanVerifyResponse;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring", imports = {InstallmentUtils.class})
public interface InstallmentDebtorExtendedDTOMapper {

  List<InstallmentDebtorExtendedDTO> map(List<InstallmentDebtorDTO> installments, @Context Map<Long, Organization> organizationMap, @Context PostalIbanVerifyResponse postalIbanVerifyResponse);

  @Mapping(target = "orgName", expression = "java(resolveOrgName(installment,organizationMap))")
  @Mapping(target = "orgFiscalCode", expression = "java(resolveOrgFiscalCode(installment,organizationMap))")
  @Mapping(target = "status", expression = "java(InstallmentUtils.resolveInstallmentStatus(installment.getStatus()))")
  @Mapping(target = "allCCP", expression = "java(postalIbanVerifyResponse != null ? InstallmentUtils.extractAllCCP(installment.getInstallmentId(), postalIbanVerifyResponse) : null)")
  InstallmentDebtorExtendedDTO map(InstallmentDebtorDTO installment, @Context Map<Long, Organization> organizationMap, @Context PostalIbanVerifyResponse postalIbanVerifyResponse);

  default String resolveOrgName(InstallmentDebtorDTO installment, @Context Map<Long,Organization> organizationMap){
    return getOrganization(installment.getOrganizationId(),organizationMap).getOrgName();
  }

  default String resolveOrgFiscalCode(InstallmentDebtorDTO installment, @Context Map<Long,Organization> organizationMap){
    return getOrganization(installment.getOrganizationId(),organizationMap).getOrgFiscalCode();
  }

  private Organization getOrganization(Long organizationId, Map<Long, Organization> organizationMap) {
    if (organizationMap == null) {
      throw new IllegalArgumentException("[INVALID_ARGUMENT] organizationMap must not be null");
    }
    Organization organization = organizationMap.get(organizationId);
    if (organization == null) {
      throw new IllegalStateException("[ORGANIZATION_NOT_FOUND] Missing Organization for organizationId " + organizationId);
    }
    return organization;
  }
}
