package it.gov.pagopa.pu.citizen.service.receipt;

import it.gov.pagopa.pu.citizen.connector.debtpositions.ReceiptService;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorReceiptsDTO;
import it.gov.pagopa.pu.citizen.exception.ResourceNotFoundException;
import it.gov.pagopa.pu.citizen.mapper.PagedDebtorReceiptsDTOMapper;
import it.gov.pagopa.pu.citizen.service.organization.BrokerOrganizationsRetrieverService;
import it.gov.pagopa.pu.citizen.service.organization.OrganizationRetrieverService;
import it.gov.pagopa.pu.debtpositions.dto.generated.PagedModelReceiptNoPIIView;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptDetailDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptOriginType;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authorization.AuthorizationDeniedException;
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
  private final OrganizationRetrieverService organizationRetrieverService;

  public ReceiptFacadeServiceImpl(ReceiptService receiptService, BrokerOrganizationsRetrieverService brokerOrganizationsRetrieverService, PagedDebtorReceiptsDTOMapper pagedDebtorReceiptsDTOMapper, OrganizationRetrieverService organizationRetrieverService) {
    this.receiptService = receiptService;
    this.brokerOrganizationsRetrieverService = brokerOrganizationsRetrieverService;
    this.pagedDebtorReceiptsDTOMapper = pagedDebtorReceiptsDTOMapper;
    this.organizationRetrieverService = organizationRetrieverService;
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

  @Override
  public ReceiptDetailDTO getReceiptDetail(String fiscalCode, Long brokerId, Long organizationId, Long receiptId, String accessToken) {
    organizationRetrieverService.validateOrganization(organizationId,brokerId,accessToken);
    ReceiptDetailDTO receiptDetailDTO = receiptService.getReceiptDetail(receiptId, organizationId, accessToken);
    if(receiptDetailDTO!=null){
      validateReceiptDebtor(fiscalCode,receiptDetailDTO);
    }
    return receiptDetailDTO;
  }

  private static void validateReceiptDebtor(String fiscalCode, ReceiptDetailDTO receipt) {
    if(!fiscalCode.equals(receipt.getDebtor().getFiscalCode())){
      throw new AuthorizationDeniedException("User cannot access Receipt having id "+ receipt.getReceiptId());
    }
  }
}
