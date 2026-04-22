package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.citizen.connector.debtpositions.client.TransferSearchClient;
import it.gov.pagopa.pu.debtpositions.dto.generated.PostalIbanVerifyResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransferServiceImpl implements TransferService{

  private final TransferSearchClient transferSearchClient;

  public TransferServiceImpl(TransferSearchClient transferSearchClient) {
    this.transferSearchClient = transferSearchClient;
  }

  @Override
  public PostalIbanVerifyResponse verifyPostalIban(List<Long> installmentIds, String accessToken) {
    return transferSearchClient.verifyPostalIban(installmentIds, accessToken);
  }
}
