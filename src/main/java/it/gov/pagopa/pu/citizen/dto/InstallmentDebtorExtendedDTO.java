package it.gov.pagopa.pu.citizen.dto;

import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentDebtorDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class InstallmentDebtorExtendedDTO extends InstallmentDebtorDTO {
  private String orgFiscalCode;
  private String orgName;
  private Boolean allCCP;
}
