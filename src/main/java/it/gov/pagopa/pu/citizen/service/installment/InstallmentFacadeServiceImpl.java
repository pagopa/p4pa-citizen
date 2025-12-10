package it.gov.pagopa.pu.citizen.service.installment;

import io.micrometer.common.util.StringUtils;
import it.gov.pagopa.pu.citizen.connector.debtpositions.InstallmentService;
import it.gov.pagopa.pu.citizen.dto.InstallmentDebtorExtendedDTO;
import it.gov.pagopa.pu.citizen.exception.InvalidParamException;
import it.gov.pagopa.pu.citizen.mapper.InstallmentDebtorExtendedDTOMapper;
import it.gov.pagopa.pu.citizen.service.organization.OrganizationRetrieverService;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentDebtorDTO;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class InstallmentFacadeServiceImpl implements InstallmentFacadeService {

  private final OrganizationRetrieverService organizationRetrieverService;
  private final InstallmentService installmentService;
  private final InstallmentDebtorExtendedDTOMapper installmentDebtorExtendedDTOMapper;

  public InstallmentFacadeServiceImpl(OrganizationRetrieverService organizationRetrieverService, InstallmentService installmentService, InstallmentDebtorExtendedDTOMapper installmentDebtorExtendedDTOMapper) {
    this.organizationRetrieverService = organizationRetrieverService;
    this.installmentService = installmentService;
    this.installmentDebtorExtendedDTOMapper = installmentDebtorExtendedDTOMapper;
  }

  @Override
  public List<InstallmentDebtorExtendedDTO> getInstallmentByIuvOrNav(Long brokerId, String iuvOrNav,
      String debtorFiscalCode, String orgFiscalCode, String accessToken) {
    if (StringUtils.isBlank(debtorFiscalCode) && StringUtils.isBlank(orgFiscalCode)) {
      throw new InvalidParamException("Either debtorFiscalCode or orgFiscalCode must be provided");
    }
    Organization organization = resolveOrganization(orgFiscalCode, brokerId, accessToken);
    Long organizationId = organization != null ? organization.getOrganizationId() : null;

    List<InstallmentDebtorDTO> installments = installmentService.getInstallmentByIuvOrNav(iuvOrNav, debtorFiscalCode, organizationId, accessToken);
    if (CollectionUtils.isEmpty(installments)) {
      return Collections.emptyList();
    }

    return installmentDebtorExtendedDTOMapper.map(
      installments,
      buildOrganizationMap(installments, organization, brokerId, accessToken)
    );
  }

  private Organization resolveOrganization(String orgFiscalCode, Long brokerId, String accessToken) {
    if (StringUtils.isBlank(orgFiscalCode)) {
      return null;
    }
    return organizationRetrieverService.getValidOrganization(orgFiscalCode, brokerId, accessToken);
  }

  private Map<Long, Organization> buildOrganizationMap(List<InstallmentDebtorDTO> installments,
      Organization organization, Long brokerId, String accessToken) {

    Long organizationId = organization != null ? organization.getOrganizationId() : null;
    Set<Long> organizationIds = installments.stream()
      .map(InstallmentDebtorDTO::getOrganizationId)
      .collect(Collectors.toSet());
    Map<Long, Organization> organizationMap = organizationIds.stream()
      .filter(orgId -> !Objects.equals(orgId, organizationId))
      .collect(Collectors.toMap(
        Function.identity(),
        orgId -> organizationRetrieverService.getValidOrganization(orgId, brokerId, accessToken)
      ));
    if (organizationId != null) {
      organizationMap.put(organizationId, organization);
    }
    return organizationMap;
  }
}
