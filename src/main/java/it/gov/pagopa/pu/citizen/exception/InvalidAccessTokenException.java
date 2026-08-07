package it.gov.pagopa.pu.citizen.exception;

import it.gov.pagopa.pu.citizen.exception.common.BaseBusinessException;

public class InvalidAccessTokenException extends BaseBusinessException {
  public InvalidAccessTokenException(String code, String message) {
    super(code, message);
  }
}
