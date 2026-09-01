package it.gov.pagopa.pu.citizen.exception;

import it.gov.pagopa.pu.citizen.exception.common.BaseBusinessException;

public class InvalidParamException extends BaseBusinessException {
  public InvalidParamException(String code, String message) {
    super(code, message);
  }
}

