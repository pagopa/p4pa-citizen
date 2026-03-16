package it.gov.pagopa.pu.citizen.service.organization;

import it.gov.pagopa.pu.citizen.connector.debtpositions.DebtPositionTypeOrgService;
import it.gov.pagopa.pu.citizen.connector.organization.BrokerService;
import it.gov.pagopa.pu.citizen.connector.organization.OrganizationService;
import it.gov.pagopa.pu.citizen.dto.generated.OrganizationsWithSpontaneousDTO;
import it.gov.pagopa.pu.citizen.exception.ResourceNotFoundException;
import it.gov.pagopa.pu.citizen.mapper.OrganizationsWithSpontaneousDTOMapper;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionTypeOrgWithActiveSpontaneousCount;
import it.gov.pagopa.pu.organization.dto.generated.Broker;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrganizationRetrieverServiceImpl implements OrganizationRetrieverService{

  private final BrokerOrganizationsRetrieverService brokerOrganizationsRetrieverService;
  private final DebtPositionTypeOrgService debtPositionTypeOrgService;
  private final OrganizationsWithSpontaneousDTOMapper organizationsWithSpontaneousDTOMapper;
  private final OrganizationService organizationService;
  private final String cieOrgFiscalCode;
  private final BrokerService brokerService;
  private Organization cieOrganization = null;

  public OrganizationRetrieverServiceImpl(BrokerOrganizationsRetrieverService brokerOrganizationsRetrieverService, DebtPositionTypeOrgService debtPositionTypeOrgService,
                                          OrganizationsWithSpontaneousDTOMapper organizationsWithSpontaneousDTOMapper, OrganizationService organizationService,
                                          @Value("${cie.organization.fiscal-code}") String cieOrgFiscalCode, BrokerService brokerService
  ) {
    this.brokerOrganizationsRetrieverService = brokerOrganizationsRetrieverService;
    this.debtPositionTypeOrgService = debtPositionTypeOrgService;
    this.organizationsWithSpontaneousDTOMapper = organizationsWithSpontaneousDTOMapper;
      this.organizationService = organizationService;
    this.cieOrgFiscalCode = cieOrgFiscalCode;
    this.brokerService = brokerService;
  }

  @Override
  public List<OrganizationsWithSpontaneousDTO> getOrganizationsWithSpontaneous(Long brokerId, String accessToken) {

    List<Organization> organizations = brokerOrganizationsRetrieverService.getAllActiveOrganizationsByBrokerId(brokerId, accessToken);

    if (organizations.isEmpty()) {
      return Collections.emptyList();
    }

    List<DebtPositionTypeOrgWithActiveSpontaneousCount> debtPositionTypeOrgWithActiveSpontaneousCount = getDebtPositionTypeOrgWithActiveSpontaneousCounts(accessToken, organizations);

    return organizationsWithSpontaneousDTOMapper.map(getOrganizationsFiltered(organizations, debtPositionTypeOrgWithActiveSpontaneousCount));
  }

  private List<DebtPositionTypeOrgWithActiveSpontaneousCount> getDebtPositionTypeOrgWithActiveSpontaneousCounts(String accessToken, List<Organization> organizations) {
    List<Long> organizationsIds = organizations.stream().map(Organization::getOrganizationId).toList();
    return debtPositionTypeOrgService.getDebtPositionTypeOrgWithActiveSpontaneousCount(organizationsIds, accessToken);
  }

  private List<Organization> getOrganizationsFiltered( List<Organization> organizationList, List<DebtPositionTypeOrgWithActiveSpontaneousCount> debtPositionTypeOrgWithActiveSpontaneousCounts) {

    Set<Long> validIds = debtPositionTypeOrgWithActiveSpontaneousCounts
      .stream()
      .map(DebtPositionTypeOrgWithActiveSpontaneousCount::getOrganizationId)
      .collect(Collectors.toSet());

    return organizationList.stream()
      .filter(organization -> validIds.contains(organization.getOrganizationId()))
      .toList();
  }

  public void validateOrganization(Long organizationId, Long brokerId, String accessToken) {
    getValidOrganization(organizationId, brokerId, accessToken);
  }

  public Organization getValidOrganization(Long organizationId, Long brokerId, String accessToken){
    Organization organization = organizationService.getOrganizationByOrganizationId(organizationId, accessToken);
    if(organization==null || !brokerId.equals(organization.getBrokerId())){
      throw new ResourceNotFoundException("ORGANIZATION_NOT_FOUND", "Organization having id "+organizationId+" and brokerId "+brokerId+" not found");
    }
    return organization;
  }

  public Organization getValidOrganization(String orgFiscalCode, Long brokerId, String accessToken) {
    Organization organization = organizationService.findByOrgFiscalCode(orgFiscalCode, accessToken);
    if(organization==null || !brokerId.equals(organization.getBrokerId())){
      throw new ResourceNotFoundException("ORGANIZATION_NOT_FOUND","Organization having orgFiscalCode "+orgFiscalCode+" and brokerId "+brokerId+" not found");
    }
    return organization;
  }

  @Override
  public boolean isCieBroker(Long brokerId, String accessToken) {
    if(brokerId == null){
      return false;
    }
    return brokerId.equals(getCieOrganization(accessToken).getBrokerId());
  }

  @Override
  public Organization getCieOrganization(String accessToken){
    if(cieOrganization == null) {
      setCieOrganization(accessToken);
    }
    return cieOrganization;
  }

  private synchronized void setCieOrganization(String accessToken){
    if(cieOrganization == null) {
      Organization organization = organizationService.findByOrgFiscalCode(cieOrgFiscalCode, accessToken);
      if (organization == null) {
        throw new ResourceNotFoundException("ORGANIZATION_NOT_FOUND", "Cie organization not found");
      }
      cieOrganization = organization;
    }
  }

  @Override
  public boolean isDelegateBroker(Long brokerId, String accessToken) {
    if(brokerId == null){
      return false;
    }
    Broker broker = brokerService.getBroker(brokerId, accessToken);
    return broker !=null && broker.getFlagDelegate();
  }
}
