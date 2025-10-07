package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.citizen.connector.debtpositions.client.SpontaneousFormEntityClient;
import it.gov.pagopa.pu.debtpositions.dto.generated.SpontaneousForm;
import org.springframework.stereotype.Service;

@Service
public class SpontaneousFormServiceImpl implements SpontaneousFormService {

  private final SpontaneousFormEntityClient spontaneousFormEntityClient;

  public SpontaneousFormServiceImpl(SpontaneousFormEntityClient spontaneousFormEntityClient) {
    this.spontaneousFormEntityClient = spontaneousFormEntityClient;
  }

  @Override
  public SpontaneousForm getSpontaneousForm(Long spontaneousFormId, String accessToken) {
    return spontaneousFormEntityClient.getSpontaneousForm(spontaneousFormId, accessToken);
  }
}
