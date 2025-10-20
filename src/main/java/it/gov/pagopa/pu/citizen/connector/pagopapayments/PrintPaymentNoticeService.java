package it.gov.pagopa.pu.citizen.connector.pagopapayments;

import it.gov.pagopa.pu.citizen.dto.FileResourceDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;

public interface PrintPaymentNoticeService {
  FileResourceDTO generateNotice(String iuv, DebtPositionDTO debtPositionDTO, String accessToken);
}
