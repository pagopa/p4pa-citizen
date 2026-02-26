package it.gov.pagopa.pu.citizen.connector.organization;

import it.gov.pagopa.pu.citizen.connector.organization.client.BrokerEntityClient;
import it.gov.pagopa.pu.citizen.connector.organization.client.BrokerSearchClient;
import it.gov.pagopa.pu.organization.dto.generated.Broker;
import org.springframework.stereotype.Service;

@Service
public class BrokerServiceImpl implements BrokerService {
  private final BrokerEntityClient brokerEntityClient;
  private final BrokerSearchClient brokerSearchClient;

  public BrokerServiceImpl(BrokerEntityClient brokerEntityClient, BrokerSearchClient brokerSearchClient) {
    this.brokerEntityClient = brokerEntityClient;
    this.brokerSearchClient = brokerSearchClient;
  }

  @Override
  public Broker getBroker(Long brokerId, String accessToken) {
    return brokerEntityClient.getBroker(brokerId, accessToken);
  }

  @Override
  public Broker getBrokerByExternalId(String accessToken, String externalId) {
    return brokerSearchClient.getBrokerByExternalId(accessToken, externalId);
  }
}
