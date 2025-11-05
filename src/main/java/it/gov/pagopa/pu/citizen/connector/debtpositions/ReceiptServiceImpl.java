package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.citizen.connector.debtpositions.client.ReceiptNoPiiViewSearchClient;
import it.gov.pagopa.pu.debtpositions.dto.generated.PagedModelReceiptNoPIIView;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptOriginType;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReceiptServiceImpl implements ReceiptService {

  private final ReceiptNoPiiViewSearchClient receiptNoPiiViewSearchClient;

  public ReceiptServiceImpl(ReceiptNoPiiViewSearchClient receiptNoPiiViewSearchClient) {
    this.receiptNoPiiViewSearchClient = receiptNoPiiViewSearchClient;
  }

  @Override
  public PagedModelReceiptNoPIIView getPagedModelReceiptNoPIIView(String debtorFiscalCode, List<String> organizationsFiscalCode, List<ReceiptOriginType> receiptOrigins, Pageable pageable, String accessToken) {
    return receiptNoPiiViewSearchClient.getPagedModelReceiptNoPIIView(debtorFiscalCode, organizationsFiscalCode, receiptOrigins, pageable, accessToken);
  }
}
