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
  private PersonEntityType payerEntityType;
  private String payerFiscalCode;
  private String payerFullName;
  private String payerAddress;
  private String payerCivic;
  private String payerPostalCode;
  private String payerLocation;
  private String payerProvince;
  private String payerNation;
  private String payerEmail;
  @NotNull
  private String orgFiscalCode;
}
