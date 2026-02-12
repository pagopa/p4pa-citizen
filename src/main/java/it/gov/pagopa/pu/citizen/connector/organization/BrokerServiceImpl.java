package it.gov.pagopa.pu.citizen.connector.organization;

import it.gov.pagopa.pu.citizen.connector.organization.client.BrokerEntityClient;
import it.gov.pagopa.pu.organization.dto.generated.Broker;
import org.springframework.stereotype.Service;

@Service
public class BrokerServiceImpl implements BrokerService {
  private final BrokerEntityClient brokerEntityClient;

  public BrokerServiceImpl(BrokerEntityClient brokerEntityClient) {
    this.brokerEntityClient = brokerEntityClient;
  }

  @Override
  public Broker getBroker(Long brokerId, String accessToken) {
    return brokerEntityClient.getBroker(brokerId, accessToken);
  }
}
