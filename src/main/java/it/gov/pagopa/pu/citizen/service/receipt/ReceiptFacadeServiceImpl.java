package it.gov.pagopa.pu.citizen.service.receipt;

import it.gov.pagopa.pu.citizen.connector.debtpositions.ReceiptService;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorReceiptsDTO;
import it.gov.pagopa.pu.citizen.exception.ResourceNotFoundException;
import it.gov.pagopa.pu.citizen.mapper.PagedDebtorReceiptsDTOMapper;
import it.gov.pagopa.pu.citizen.service.organization.BrokerOrganizationsRetrieverService;
import it.gov.pagopa.pu.debtpositions.dto.generated.PagedModelReceiptNoPIIView;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptOriginType;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReceiptFacadeServiceImpl implements ReceiptFacadeService{

  private final ReceiptService receiptService;
  private final BrokerOrganizationsRetrieverService brokerOrganizationsRetrieverService;
  private final PagedDebtorReceiptsDTOMapper pagedDebtorReceiptsDTOMapper;

  public ReceiptFacadeServiceImpl(ReceiptService receiptService, BrokerOrganizationsRetrieverService brokerOrganizationsRetrieverService, PagedDebtorReceiptsDTOMapper pagedDebtorReceiptsDTOMapper) {
    this.receiptService = receiptService;
    this.brokerOrganizationsRetrieverService = brokerOrganizationsRetrieverService;
    this.pagedDebtorReceiptsDTOMapper = pagedDebtorReceiptsDTOMapper;
  }

  @Override
  public PagedDebtorReceiptsDTO getPagedDebtorReceipts(Long brokerId, String orgName, String debtorFiscalCode, String accessToken, Pageable pageable) {
    Map<String,Organization> organizationsMap = retrieveOrganizations(brokerId, orgName, accessToken);
    List<String> organizationsFiscalCodes = new ArrayList<>(organizationsMap.keySet());
    PagedModelReceiptNoPIIView pagedModelReceiptNoPIIView = receiptService.getPagedModelReceiptNoPIIView(debtorFiscalCode, organizationsFiscalCodes, List.of(ReceiptOriginType.RECEIPT_PAGOPA), pageable, accessToken);
    return pagedDebtorReceiptsDTOMapper.map(organizationsMap, pagedModelReceiptNoPIIView);
  }

  private Map<String,Organization> retrieveOrganizations(Long brokerId, String orgName, String accessToken){
    List<Organization> organizations = brokerOrganizationsRetrieverService.getAllOrganizationsByBrokerIdAndOrgName(brokerId, orgName, accessToken);
    if (organizations.isEmpty()){
      throw new ResourceNotFoundException("Organizations not found with brokerId %s and orgName %s".formatted(brokerId, orgName));
    }

    return organizations.stream()
      .collect(Collectors.toMap(Organization::getOrgFiscalCode, org -> org));
  }

}
