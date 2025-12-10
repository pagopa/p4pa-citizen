package it.gov.pagopa.pu.citizen.connector.organization.client;

import it.gov.pagopa.pu.citizen.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.citizen.utils.PageUtils;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationStatus;
import it.gov.pagopa.pu.organization.dto.generated.PagedModelOrganization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
@Slf4j
public class OrganizationSearchClient {

  private final OrganizationApisHolder organizationApisHolder;

  public OrganizationSearchClient(OrganizationApisHolder organizationApisHolder) {
    this.organizationApisHolder = organizationApisHolder;
  }

  public PagedModelOrganization getPagedOrganizationsByBrokerIdAndStatus(Long brokerId, OrganizationStatus status, Pageable pageable, String accessToken){
    return organizationApisHolder.getOrganizationSearchControllerApi(accessToken).crudOrganizationsFindPagedOrganizationsByBrokerIdAndStatus(
      brokerId,
      status,
      PageUtils.getPageNumber(pageable),
      PageUtils.getPageSize(pageable),
      PageUtils.getSortList(pageable));
  }

  public PagedModelOrganization getOrganizationsListByBrokerIdAndOrgName(Long brokerId, String orgName, Pageable pageable, String accessToken){
    return organizationApisHolder.getOrganizationSearchControllerApi(accessToken)
      .crudOrganizationsFindByBrokerIdAndOrgName(
        String.valueOf(brokerId),
        orgName,
        PageUtils.getPageNumber(pageable),
        PageUtils.getPageSize(pageable),
        PageUtils.getSortList(pageable)
      );
  }

  public PagedModelOrganization getOrganizationsByBrokerIdAndOrgNameAndOrgFiscalCode(Long brokerId, String orgName, String orgFiscalCode, Pageable pageable, String accessToken){
    return organizationApisHolder.getOrganizationSearchControllerApi(accessToken).
      crudOrganizationsFindByBrokerIdAndOrgNameAndOrgFiscalCode(
        String.valueOf(brokerId),
        orgName,
        orgFiscalCode,
        PageUtils.getPageNumber(pageable),
        PageUtils.getPageSize(pageable),
        PageUtils.getSortList(pageable));
  }

  public Organization getBrokerOrganization(Long brokerId, String accessToken){
    try {
      return organizationApisHolder.getOrganizationSearchControllerApi(accessToken)
        .crudOrganizationsGetBrokerOrganization(brokerId);
    } catch (HttpClientErrorException.NotFound e) {
      log.warn("Organization for Broker with brokerId {} not found", brokerId);
      return null;
    }
  }

  public Organization findByOrgFiscalCode(String orgFiscalCode, String accessToken){
    try {
      return organizationApisHolder.getOrganizationSearchControllerApi(accessToken)
        .crudOrganizationsFindByOrgFiscalCode(orgFiscalCode);
    } catch (HttpClientErrorException.NotFound e) {
      log.warn("Organization with orgFiscalCode {} not found", orgFiscalCode);
      return null;
    }
  }
}
