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
  String debtorFiscalCode;
  String noticeNumberOrIuv;
  OffsetDateTime paymentDateTimeFrom;
  OffsetDateTime paymentDateTimeTo;
  List<String> organizationsFiscalCode;
  List<ReceiptOriginType> receiptOrigins;
}
