package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.debtpositions.dto.generated.PostalIbanVerifyResponse;

import java.util.List;

public interface TransferService {
  PostalIbanVerifyResponse verifyPostalIban(List<Long> installmentIds, String accessToken);
}
