package it.gov.pagopa.pu.citizen.connector.organization;

import it.gov.pagopa.pu.organization.dto.generated.Broker;

public interface BrokerService {
  Broker getBroker(Long brokerId, String accessToken);
}
