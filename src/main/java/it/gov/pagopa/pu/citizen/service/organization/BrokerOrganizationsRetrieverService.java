package it.gov.pagopa.pu.citizen.service.organization;

import it.gov.pagopa.pu.citizen.connector.organization.OrganizationService;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.organization.dto.generated.PagedModelOrganization;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
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

  public List<Organization> getAllOrganizationsByBrokerId(
    Long brokerId,
    String orgName,
    String ipaCode,
    Set<Long> organizationIds,
    String accessToken) {

    AtomicInteger pageNumber = new AtomicInteger(0);

    return Stream.iterate(
        getPage(brokerId, orgName, ipaCode, organizationIds, accessToken, pageNumber.get()),
        Objects::nonNull,
        p -> {
          int nextPage = pageNumber.incrementAndGet();
          if (p.getPage() != null && p.getPage().getTotalPages() != null && nextPage < p.getPage().getTotalPages()) {
            return getPage(brokerId, orgName, ipaCode, organizationIds, accessToken, nextPage);
          }
          return null;
        })
      .filter(p -> p.getEmbedded() != null && p.getEmbedded().getOrganizations() != null)
      .flatMap(p -> p.getEmbedded().getOrganizations().stream())
      .toList();
  }

  private PagedModelOrganization getPage(Long brokerId, String orgName,  String ipaCode, Set<Long> organizationIds,  String accessToken, int pageNumber) {
    Pageable pageable = PageRequest.of(pageNumber, pageMaxSize);
    return organizationService.getOrganizationsByBrokerIdAndFilters(brokerId, orgName, ipaCode, organizationIds, pageable, accessToken);
  }
}

