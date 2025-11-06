package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.citizen.connector.debtpositions.client.ReceiptClient;
import it.gov.pagopa.pu.citizen.connector.debtpositions.client.ReceiptNoPiiViewSearchClient;
import it.gov.pagopa.pu.debtpositions.dto.generated.PagedModelReceiptNoPIIView;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptDetailDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptOriginType;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReceiptServiceImpl implements ReceiptService {

  private final ReceiptNoPiiViewSearchClient receiptNoPiiViewSearchClient;
  private final ReceiptClient receiptClient;

  public ReceiptServiceImpl(ReceiptNoPiiViewSearchClient receiptNoPiiViewSearchClient, ReceiptClient receiptClient) {
    this.receiptNoPiiViewSearchClient = receiptNoPiiViewSearchClient;
    this.receiptClient = receiptClient;
  }

  @Override
  public PagedModelReceiptNoPIIView getPagedModelReceiptNoPIIView(String debtorFiscalCode, List<String> organizationsFiscalCode, List<ReceiptOriginType> receiptOrigins, Pageable pageable, String accessToken) {
    return receiptNoPiiViewSearchClient.getPagedModelReceiptNoPIIView(debtorFiscalCode, organizationsFiscalCode, receiptOrigins, pageable, accessToken);
  }

  @Override
  public ReceiptDetailDTO getReceiptDetail(Long receiptId, Long organizationId, String accessToken) {
    return receiptClient.getReceiptDetail(receiptId,organizationId,accessToken);
  }
}
