package it.gov.pagopa.pu.citizen.exception.common;

public class ForbiddenException extends BaseBusinessException {
  public ForbiddenException(String code, String message) {
    super(code, message);
  }
}
