package it.gov.pagopa.pu.citizen.dto;

import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptOriginType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DebtorReceiptsFiltersDTO {
  private String debtorFiscalCode;
  private String noticeNumberOrIuv;
  private OffsetDateTime paymentDateTimeFrom;
  private OffsetDateTime paymentDateTimeTo;
  private List<String> organizationsFiscalCode;
  private List<ReceiptOriginType> receiptOrigins;
}
