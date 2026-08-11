package it.gov.pagopa.pu.citizen.config.rest;

import it.gov.pagopa.pu.citizen.dto.generated.ErrorFieldDTO;

import java.util.List;

public record PuErrorDTO(
  String category,
  String code,
  String message,
  List<ErrorFieldDTO> fields
) {
}
