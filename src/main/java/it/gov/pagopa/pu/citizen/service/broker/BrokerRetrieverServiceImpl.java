package it.gov.pagopa.pu.citizen.service.broker;

import it.gov.pagopa.pu.citizen.connector.organization.OrganizationService;
import it.gov.pagopa.pu.citizen.dto.generated.BrokerInfoDTO;
import it.gov.pagopa.pu.citizen.mapper.BrokerInfoDTOMapper;
import org.springframework.stereotype.Service;

@Service
public class BrokerRetrieverServiceImpl implements BrokerRetrieverService {
  private final OrganizationService organizationService;
  private final BrokerInfoDTOMapper brokerInfoDTOMapper;

  public BrokerRetrieverServiceImpl(OrganizationService organizationService, BrokerInfoDTOMapper brokerInfoDTOMapper) {
    this.organizationService = organizationService;
    this.brokerInfoDTOMapper = brokerInfoDTOMapper;
  }

  @Override
  public BrokerInfoDTO getBrokerInfo(Long brokerId, String accessToken) {
    return brokerInfoDTOMapper.mapFromOrganization(organizationService.getBrokerOrganization(brokerId,accessToken));
  }
}
