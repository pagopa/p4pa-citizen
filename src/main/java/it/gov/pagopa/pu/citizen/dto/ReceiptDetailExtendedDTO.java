package it.gov.pagopa.pu.citizen.dto;

import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class ReceiptDetailExtendedDTO extends ReceiptDetailDTO {

  private Long organizationId;
  private String orgName;
  private String orgFiscalCode;
}
