package it.gov.pagopa.pu.citizen.controller;

import it.gov.pagopa.pu.citizen.controller.generated.OrganizationApi;
import it.gov.pagopa.pu.citizen.dto.generated.OrganizationLogoDTO;
import it.gov.pagopa.pu.citizen.dto.generated.OrganizationsWithSpontaneousDTO;
import it.gov.pagopa.pu.citizen.security.SecurityUtils;
import it.gov.pagopa.pu.citizen.service.organization.OrganizationRetrieverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class OrganizationController implements OrganizationApi {

  private final OrganizationRetrieverService organizationRetrieverService;

  public OrganizationController(OrganizationRetrieverService organizationRetrieverService) {
    this.organizationRetrieverService = organizationRetrieverService;
  }

  @Override
  public ResponseEntity<List<OrganizationsWithSpontaneousDTO>> getOrganizationsWithSpontaneous(Long brokerId) {
    log.info("getOrganizationsWithSpontaneous was requested with brokerId {}", brokerId);

    return ResponseEntity.ok(organizationRetrieverService.getOrganizationsWithSpontaneous(brokerId, SecurityUtils.getAccessToken()));
  }

  @Override
  public ResponseEntity<OrganizationLogoDTO> getOrganizationLogo(Long brokerId, String orgFiscalCode) {
    log.info("getOrganizationLogo was requested with brokerId {} and orgFiscalCode {}", brokerId, orgFiscalCode);
    return ResponseEntity.ofNullable(organizationRetrieverService.getOrganizationLogo(brokerId, orgFiscalCode, SecurityUtils.getAccessToken()));
  }
}
