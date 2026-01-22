package it.gov.pagopa.pu.citizen.exception;

public class InvalidAccessTokenException extends BaseBusinessException{
  public InvalidAccessTokenException(String code, String message) {
    super(code, message);
  }
}
