package it.gov.pagopa.pu.citizen.connector.organization.client;

import it.gov.pagopa.pu.citizen.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.citizen.exception.common.RestInvokeNotFoundException;
import it.gov.pagopa.pu.organization.dto.generated.Broker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BrokerEntityClient {

  private final OrganizationApisHolder organizationApisHolder;

  public BrokerEntityClient(OrganizationApisHolder organizationApisHolder) {
    this.organizationApisHolder = organizationApisHolder;
  }

  public Broker getBroker(Long brokerId, String accessToken) {
    try {
      return organizationApisHolder.getBrokerEntityControllerApi(accessToken)
        .crudGetBroker(String.valueOf(brokerId));
    } catch (RestInvokeNotFoundException e) {
      log.warn("Broker with brokerId {} not found", brokerId);
      return null;
    }
  }

}
