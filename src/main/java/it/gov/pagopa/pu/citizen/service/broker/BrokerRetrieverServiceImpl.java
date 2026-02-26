package it.gov.pagopa.pu.citizen.service.broker;

import it.gov.pagopa.pu.citizen.connector.organization.BrokerService;
import it.gov.pagopa.pu.citizen.connector.organization.OrganizationService;
import it.gov.pagopa.pu.citizen.dto.generated.BrokerInfoDTO;
import it.gov.pagopa.pu.citizen.exception.ResourceNotFoundException;
import it.gov.pagopa.pu.citizen.mapper.BrokerInfoDTOMapper;
import it.gov.pagopa.pu.organization.dto.generated.Broker;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class BrokerRetrieverServiceImpl implements BrokerRetrieverService {
  private final OrganizationService organizationService;
  private final BrokerInfoDTOMapper brokerInfoDTOMapper;
  private final BrokerService brokerService;

  public BrokerRetrieverServiceImpl(OrganizationService organizationService, BrokerInfoDTOMapper brokerInfoDTOMapper, BrokerService brokerService) {
    this.organizationService = organizationService;
    this.brokerInfoDTOMapper = brokerInfoDTOMapper;
    this.brokerService = brokerService;
  }

  @Override
  public BrokerInfoDTO getBrokerInfo(Long brokerId, String externalId, String accessToken) {
    Broker broker;
    if (brokerId != null){
      broker = brokerService.getBroker(brokerId, accessToken);
    }else {
      broker = brokerService.getBrokerByExternalId(externalId, accessToken);
    }

    if(Objects.isNull(broker)){
      throw new ResourceNotFoundException("BROKER_NOT_FOUND","Broker having id "+brokerId+" and " + externalId + "not found");
    }
    return brokerInfoDTOMapper.map(organizationService.getBrokerOrganization(broker.getBrokerId(),accessToken), broker.getArpuConfig());
  }
}
