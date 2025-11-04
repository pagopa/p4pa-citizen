package it.gov.pagopa.pu.citizen.service.receipt;

import it.gov.pagopa.pu.citizen.connector.debtpositions.ReceiptNoPiiViewSearchService;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorReceiptsDTO;
import it.gov.pagopa.pu.citizen.exception.ResourceNotFoundException;
import it.gov.pagopa.pu.citizen.mapper.PagedDebtorReceiptsDTOMapper;
import it.gov.pagopa.pu.citizen.service.organization.BrokerOrganizationsRetrieverService;
import it.gov.pagopa.pu.debtpositions.dto.generated.PagedModelReceiptNoPIIView;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptOriginType;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReceiptFacadeServiceImpl implements ReceiptFacadeService{

  private final ReceiptNoPiiViewSearchService receiptNoPiiViewSearchService;
  private final BrokerOrganizationsRetrieverService brokerOrganizationsRetrieverService;
  private final PagedDebtorReceiptsDTOMapper pagedDebtorReceiptsDTOMapper;

  public ReceiptFacadeServiceImpl(ReceiptNoPiiViewSearchService receiptNoPiiViewSearchService, BrokerOrganizationsRetrieverService brokerOrganizationsRetrieverService, PagedDebtorReceiptsDTOMapper pagedDebtorReceiptsDTOMapper) {
    this.receiptNoPiiViewSearchService = receiptNoPiiViewSearchService;
    this.brokerOrganizationsRetrieverService = brokerOrganizationsRetrieverService;
    this.pagedDebtorReceiptsDTOMapper = pagedDebtorReceiptsDTOMapper;
  }

  @Override
  public PagedDebtorReceiptsDTO getPagedDebtorReceipts(Long brokerId, String orgName, String debtorFiscalCode, String accessToken, Pageable pageable) {
    List<Organization> organizations = retrieveOrganizations(brokerId, orgName, accessToken);
    PagedModelReceiptNoPIIView pagedModelReceiptNoPIIView = receiptNoPiiViewSearchService.getPagedModelReceiptNoPIIView(debtorFiscalCode, retrieveOrganizationsFiscalCode(organizations), List.of(ReceiptOriginType.RECEIPT_PAGOPA), pageable, accessToken);
    return pagedDebtorReceiptsDTOMapper.map(organizations, pagedModelReceiptNoPIIView);
  }

  private List<Organization> retrieveOrganizations(Long brokerId, String orgName, String accessToken){
    List<Organization> organizations = brokerOrganizationsRetrieverService.getAllOrganizationsByBrokerIdAndOrgName(brokerId, orgName, accessToken);
    if (organizations.isEmpty()){
      throw new ResourceNotFoundException("Organizations not found with brokerId %s and orgName %s".formatted(brokerId, orgName));
    }
    return organizations;
  }

  private List<String> retrieveOrganizationsFiscalCode(List<Organization> organizations){
    return organizations.stream().map(Organization::getOrgFiscalCode).toList();
  }
}
