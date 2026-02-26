package it.gov.pagopa.pu.citizen.connector.organization.client;

import it.gov.pagopa.pu.citizen.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.organization.dto.generated.Broker;
import org.springframework.stereotype.Service;

@Service
public class BrokerSearchClient {

  private final OrganizationApisHolder organizationApisHolder;

  public BrokerSearchClient(OrganizationApisHolder organizationApisHolder) {
    this.organizationApisHolder = organizationApisHolder;
  }

  public Broker getBrokerByExternalId(String accessToken, String externalId){
    return organizationApisHolder.getBrokerSearchControllerApi(accessToken)
      .crudBrokersFindBrokerByExternalId(externalId);
  }
}
