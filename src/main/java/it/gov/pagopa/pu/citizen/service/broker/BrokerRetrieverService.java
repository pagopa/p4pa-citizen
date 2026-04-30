package it.gov.pagopa.pu.citizen.service.broker;

import it.gov.pagopa.pu.citizen.dto.generated.BrokerInfoDTO;

public interface BrokerRetrieverService {
  BrokerInfoDTO getBrokerInfo(Long brokerId, String externalId, String accessToken);
}
