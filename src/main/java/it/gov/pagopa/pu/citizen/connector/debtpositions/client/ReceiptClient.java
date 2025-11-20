package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import it.gov.pagopa.pu.citizen.dto.FileResourceDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptDetailDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Service
public class ReceiptClient {

  private final DebtPositionsApisHolder debtPositionsApisHolder;

  public ReceiptClient(DebtPositionsApisHolder debtPositionsApisHolder) {
    this.debtPositionsApisHolder = debtPositionsApisHolder;
  }

  public ReceiptDetailDTO getReceiptDetail(Long receiptId, Long organizationId, String accessToken) {
    try {
      return debtPositionsApisHolder.getReceiptApi(accessToken)
        .getReceiptDetail(receiptId,organizationId,null, null);
    } catch (HttpClientErrorException.NotFound e) {
      log.warn("ReceiptDetail with receiptId {} and organizationId {} not found", receiptId, organizationId);
      return null;
    }
  }

  public FileResourceDTO getReceiptPdf(Long receiptId, Long organizationId, String accessToken) {
    try {
      ResponseEntity<Resource> resourceResponseEntity = debtPositionsApisHolder.getReceiptApi(accessToken)
        .getReceiptPdfWithHttpInfo(receiptId, organizationId);
      return FileResourceDTO.builder()
        .resource(resourceResponseEntity.getBody())
        .fileName(resourceResponseEntity.getHeaders().getContentDisposition().getFilename())
        .build();
    } catch (HttpClientErrorException.NotFound e) {
      log.warn("Receipt PDF with receiptId {} and organizationId {} not found", receiptId, organizationId);
      return null;
    }
  }
}
