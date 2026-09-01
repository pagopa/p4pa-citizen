package it.gov.pagopa.pu.citizen.connector.organization.client;

import it.gov.pagopa.pu.citizen.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.citizen.exception.common.RestInvokeNotFoundException;
import it.gov.pagopa.pu.organization.dto.generated.BrokerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BrokerConfigurationEntityClient {

  private final OrganizationApisHolder organizationApisHolder;

  public BrokerConfigurationEntityClient(OrganizationApisHolder organizationApisHolder) {
    this.organizationApisHolder = organizationApisHolder;
  }

  public BrokerConfiguration getBrokerConfiguration(Long brokerId, String accessToken) {
    try{
      return this.organizationApisHolder.getBrokerConfigurationEntityControllerApi(accessToken)
        .crudGetBrokerconfiguration(String.valueOf(brokerId));
    } catch (RestInvokeNotFoundException e) {
        log.warn("Broker configuration with brokerId {} not found", brokerId);
        return null;
    }
  }


}
