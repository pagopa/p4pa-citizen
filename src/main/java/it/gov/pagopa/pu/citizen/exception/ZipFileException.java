package it.gov.pagopa.pu.citizen.exception;

import it.gov.pagopa.pu.citizen.exception.common.BaseBusinessException;

public class ZipFileException extends BaseBusinessException {
  public ZipFileException(String code, String message) {
    super(code, message);
  }
}
