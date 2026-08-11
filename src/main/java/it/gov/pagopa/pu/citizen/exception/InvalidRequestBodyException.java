package it.gov.pagopa.pu.citizen.exception;

import it.gov.pagopa.pu.citizen.exception.common.BaseBusinessException;

public class InvalidRequestBodyException extends BaseBusinessException {
  public InvalidRequestBodyException(String code, String message) {
    super(code, message);
  }
}

