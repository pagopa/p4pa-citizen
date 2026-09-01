package it.gov.pagopa.pu.citizen.exception;

import it.gov.pagopa.pu.citizen.exception.common.BaseBusinessException;

public class DebtPositionInvalidFieldValuesException extends BaseBusinessException {
  public DebtPositionInvalidFieldValuesException(String code, String message) {
    super(code, message);
  }
}

