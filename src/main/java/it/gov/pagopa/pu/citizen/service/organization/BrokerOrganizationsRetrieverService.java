package it.gov.pagopa.pu.citizen.service.organization;

import it.gov.pagopa.pu.citizen.connector.organization.OrganizationService;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationStatus;
import it.gov.pagopa.pu.organization.dto.generated.PagedModelOrganization;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntFunction;
import java.util.stream.Stream;

@Service
public class BrokerOrganizationsRetrieverService {

  private final int pageMaxSize;
  private final OrganizationService organizationService;

  public BrokerOrganizationsRetrieverService(
    @Value("${rest.page.request-max-page-size}") int pageMaxSize,
    OrganizationService organizationService) {
    this.pageMaxSize = pageMaxSize;
    this.organizationService = organizationService;
  }

  public List<Organization> getAllActiveOrganizationsByBrokerId(Long brokerId,String accessToken) {
    AtomicInteger pageNumber = new AtomicInteger(0);
    return getOrganizationList( page -> getPage(brokerId, OrganizationStatus.ACTIVE, accessToken, page), pageNumber);
  }

  public List<Organization> getAllOrganizationsByBrokerIdAndOrgName(Long brokerId, String orgName, String accessToken) {
    AtomicInteger pageNumber = new AtomicInteger(0);
    return getOrganizationList( page -> getPage(brokerId, orgName, accessToken, page), pageNumber);
  }

  public List<Organization> getAllOrganizationsByBrokerIdAndOrgNameAndOrgFiscalCode(Long brokerId, String orgName, String orgFiscalCode, String accessToken) {
    AtomicInteger pageNumber = new AtomicInteger(0);
    return getOrganizationList( page -> getPage(brokerId, orgName,orgFiscalCode, accessToken, page), pageNumber);
  }

  private List<Organization> getOrganizationList(IntFunction<PagedModelOrganization> function, AtomicInteger pageNumber) {
    return Stream.iterate(
        function.apply(pageNumber.get()),
        Objects::nonNull,
        p -> {
          int nextPage = pageNumber.incrementAndGet();
          if (p.getPage() != null && p.getPage().getTotalPages() != null && nextPage < p.getPage().getTotalPages()) {
            return function.apply(nextPage);
          }
          return null;
        })
      .filter(p -> p.getEmbedded() != null && p.getEmbedded().getOrganizations() != null)
      .flatMap(p -> p.getEmbedded().getOrganizations().stream())
      .toList();
  }

  private PagedModelOrganization getPage(Long brokerId, OrganizationStatus status, String accessToken, int pageNumber) {
    Pageable pageable = PageRequest.of(pageNumber, pageMaxSize);
    return organizationService.getPagedOrganizationsByBrokerIdAndStatus(brokerId, status, pageable, accessToken);
  }

  private PagedModelOrganization getPage(Long brokerId, String orgName, String accessToken, int pageNumber) {
  Pageable pageable = PageRequest.of(pageNumber, pageMaxSize);
  return organizationService.getOrganizationsListByBrokerIdAndOrgName(brokerId, orgName, pageable, accessToken);
  }

  private PagedModelOrganization getPage(Long brokerId, String orgName, String orgFiscalCode, String accessToken, int pageNumber) {
    Pageable pageable = PageRequest.of(pageNumber, pageMaxSize);
    return organizationService.getOrganizationsByBrokerIdAndOrgNameAndOrgFiscalCode(brokerId, orgName, orgFiscalCode, pageable, accessToken);
  }

}

