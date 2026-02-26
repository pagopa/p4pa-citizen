package it.gov.pagopa.pu.citizen.controller;

import it.gov.pagopa.pu.citizen.controller.generated.BrokerApi;
import it.gov.pagopa.pu.citizen.dto.generated.BrokerInfoDTO;
import it.gov.pagopa.pu.citizen.exception.InvalidParamException;
import it.gov.pagopa.pu.citizen.security.SecurityUtils;
import it.gov.pagopa.pu.citizen.service.broker.BrokerRetrieverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class BrokerController implements BrokerApi {

  private final BrokerRetrieverService brokerRetrieverService;

  public BrokerController(BrokerRetrieverService brokerRetrieverService) {
    this.brokerRetrieverService = brokerRetrieverService;
  }

  @Override
  public ResponseEntity<BrokerInfoDTO> getBrokerInfo(Long brokerId, String externalId) {
    log.info("getBrokerInfo was requested with brokerId {} and externalId {}", brokerId, externalId);
    validateInputFields(brokerId, externalId);
    return ResponseEntity.ofNullable(brokerRetrieverService.getBrokerInfo(brokerId, null, SecurityUtils.getAccessToken()));
  }

  private static void validateInputFields(Long brokerId, String externalId) {
    if (brokerId == null && externalId == null) {
      throw new InvalidParamException("INVALID_FIELDS","Either brokerId or externalId must be provided");
    }
    if (brokerId != null && externalId != null) {
      throw new InvalidParamException("INVALID_FIELDS","Only one of brokerId or externalId must be provided");
    }
  }
}
