package it.gov.pagopa.pu.citizen.dto;

import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentDTO;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class InstallmentExtendedDTO extends InstallmentDTO {
  @Nullable
  private Boolean allCCP;
}
