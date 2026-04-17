package it.gov.pagopa.pu.citizen.dto;

import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
public class InstallmentExtendedDTO extends InstallmentDTO {
  private Boolean allCCP;
}
