package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.citizen.connector.debtpositions.client.ReceiptClient;
import it.gov.pagopa.pu.citizen.connector.debtpositions.client.ReceiptNoPiiSearchClient;
import it.gov.pagopa.pu.citizen.connector.debtpositions.client.ReceiptNoPiiViewSearchClient;
import it.gov.pagopa.pu.citizen.dto.DebtorReceiptsFiltersDTO;
import it.gov.pagopa.pu.citizen.dto.FileResourceDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.PagedModelReceiptNoPIIView;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptDetailDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ReceiptServiceImpl implements ReceiptService {

  private final ReceiptNoPiiViewSearchClient receiptNoPiiViewSearchClient;
  private final ReceiptClient receiptClient;
  private final ReceiptNoPiiSearchClient receiptNoPiiSearchClient;

  public ReceiptServiceImpl(ReceiptNoPiiViewSearchClient receiptNoPiiViewSearchClient, ReceiptClient receiptClient, ReceiptNoPiiSearchClient receiptNoPiiSearchClient) {
    this.receiptNoPiiViewSearchClient = receiptNoPiiViewSearchClient;
    this.receiptClient = receiptClient;
    this.receiptNoPiiSearchClient = receiptNoPiiSearchClient;
  }

  @Override
  public PagedModelReceiptNoPIIView getPagedModelReceiptNoPIIView(DebtorReceiptsFiltersDTO debtorReceiptsFiltersDTO, Pageable pageable, String accessToken) {
    return receiptNoPiiViewSearchClient.getPagedModelReceiptNoPIIView(debtorReceiptsFiltersDTO, pageable, accessToken);
  }

  @Override
  public ReceiptDetailDTO getReceiptDetail(Long receiptId, Long organizationId, String accessToken) {
    return receiptClient.getReceiptDetail(receiptId,organizationId,accessToken);
  }

  @Override
  public boolean isReceiptDebtorValid(Long receiptId, Long organizationId, String debtorFiscalCode, String accessToken) {
    return receiptNoPiiSearchClient.validateReceiptDebtor(receiptId, organizationId, debtorFiscalCode, accessToken) >= 1L;
  }

  @Override
  public FileResourceDTO getReceiptPdf(Long receiptId, Long organizationId, String accessToken) {
    return receiptClient.getReceiptPdf(receiptId, organizationId, accessToken);
  }
}
