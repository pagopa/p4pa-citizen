package it.gov.pagopa.pu.citizen.exception;

public class InvalidParamException extends BaseBusinessException {
  public InvalidParamException(String code, String message) {
    super(code, message);
  }
}

