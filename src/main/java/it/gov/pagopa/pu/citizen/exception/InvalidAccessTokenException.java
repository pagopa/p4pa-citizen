package it.gov.pagopa.pu.citizen.exception;

public class InvalidAccessTokenException extends RuntimeException {
  public InvalidAccessTokenException(String message) {
    super(message);
  }
}
