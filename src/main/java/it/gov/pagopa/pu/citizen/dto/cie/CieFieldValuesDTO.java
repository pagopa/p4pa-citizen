package it.gov.pagopa.pu.citizen.dto.cie;

import it.gov.pagopa.pu.debtpositions.dto.generated.PersonEntityType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CieFieldValuesDTO {
  @NotNull
  private PersonEntityType payerEntityType;
  @NotNull
  private String payerFiscalCode;
  @NotNull
  private String payerFullName;
  @NotNull
  private String payerAddress;
  @NotNull
  private String payerCivic;
  @NotNull
  private String payerPostalCode;
  @NotNull
  private String payerLocation;
  @NotNull
  private String payerProvince;
  @NotNull
  private String payerNation;
  @NotNull
  private String payerEmail;
  @NotNull
  private String orgFiscalCode;
}
