package it.gov.pagopa.pu.citizen.exception;

public class InvalidRequestBodyException extends BaseBusinessException {
  public InvalidRequestBodyException(String code, String message) {
    super(code, message);
  }
}

