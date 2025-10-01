package it.gov.pagopa.pu.citizen.controller;

import it.gov.pagopa.pu.citizen.controller.generated.OrganizationApi;
import it.gov.pagopa.pu.citizen.dto.generated.OrganizationsWithSpontaneousDTO;
import it.gov.pagopa.pu.citizen.security.SecurityUtils;
import it.gov.pagopa.pu.citizen.service.organization.OrganizationRetrieverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class OrganizationApiController implements OrganizationApi {

  private final OrganizationRetrieverService organizationRetrieverService;

  public OrganizationApiController(OrganizationRetrieverService organizationRetrieverService) {
    this.organizationRetrieverService = organizationRetrieverService;
  }

  @Override
  public ResponseEntity<List<OrganizationsWithSpontaneousDTO>> getOrganizationsListWithSpontaneous(Long brokerId) {
    log.info("getOrganizationsListWithSpontaneous was requested with brokerId {}", brokerId);

    return ResponseEntity.ok(organizationRetrieverService.getOrganizationsListWithSpontaneous(brokerId, SecurityUtils.getAccessToken()));
  }
}
