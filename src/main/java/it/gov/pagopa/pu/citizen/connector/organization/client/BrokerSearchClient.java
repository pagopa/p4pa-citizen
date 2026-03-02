package it.gov.pagopa.pu.citizen.connector.organization.client;

import it.gov.pagopa.pu.citizen.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.organization.dto.generated.Broker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Service
public class BrokerSearchClient {

  private final OrganizationApisHolder organizationApisHolder;

  public BrokerSearchClient(OrganizationApisHolder organizationApisHolder) {
    this.organizationApisHolder = organizationApisHolder;
  }

  public Broker getBrokerByExternalId(String accessToken, String externalId){

    try {
      return organizationApisHolder.getBrokerSearchControllerApi(accessToken)
      .crudBrokersFindBrokerByExternalId(externalId);
    } catch (
      HttpClientErrorException.NotFound e) {
        log.warn("Broker with externalId {} not found",  externalId);
        return null;
    }
  }
}
