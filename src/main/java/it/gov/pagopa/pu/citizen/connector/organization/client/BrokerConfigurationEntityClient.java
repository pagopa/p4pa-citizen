package it.gov.pagopa.pu.citizen.connector.organization.client;

import it.gov.pagopa.pu.citizen.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.organization.dto.generated.BrokerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

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
    } catch (HttpClientErrorException.NotFound e) {
        log.warn("Broker with brokerId {} not found", brokerId);
        return null;
    }
  }


}
