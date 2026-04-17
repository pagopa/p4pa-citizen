package it.gov.pagopa.pu.citizen.dto;

import lombok.Data;

import java.util.List;

@Data
public class PaymentOptionExtendedDTO {
  private List<InstallmentExtendedDTO> installments;
}
