package it.gov.pagopa.pu.citizen.exception;

import it.gov.pagopa.pu.citizen.dto.generated.ErrorDTO;
import it.gov.pagopa.pu.citizen.exception.common.CommonExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ControllerExceptionHandler extends CommonExceptionHandler {

  @ExceptionHandler({InvalidAccessTokenException.class})
  public ResponseEntity<ErrorDTO> handleInvalidAccessTokenException(InvalidAccessTokenException ex, HttpServletRequest request) {
    return handleException(ex, request, HttpStatus.BAD_REQUEST, ErrorDTO.CategoryEnum.BAD_REQUEST);
  }

  @ExceptionHandler(ZipFileException.class)
  public ResponseEntity<ErrorDTO> handleZipFileException(ZipFileException ex, HttpServletRequest request) {
    return handleException(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, ErrorDTO.CategoryEnum.GENERIC_ERROR);
  }

  @ExceptionHandler({InvalidParamException.class})
  public ResponseEntity<ErrorDTO> handleInvalidParamException(InvalidParamException ex, HttpServletRequest request) {
    return handleException(ex, request, HttpStatus.BAD_REQUEST, ErrorDTO.CategoryEnum.BAD_REQUEST);
  }

  @ExceptionHandler({DebtPositionInvalidFieldValuesException.class})
  public ResponseEntity<ErrorDTO> handleInvalidParamException(DebtPositionInvalidFieldValuesException ex, HttpServletRequest request) {
    return handleException(ex, request, HttpStatus.BAD_REQUEST, ErrorDTO.CategoryEnum.BAD_REQUEST);
  }

  @ExceptionHandler({InvalidRequestBodyException.class})
  public ResponseEntity<ErrorDTO> handleInvalidRequestBodyException(InvalidRequestBodyException ex, HttpServletRequest request) {
    return handleException(ex, request, HttpStatus.BAD_REQUEST, ErrorDTO.CategoryEnum.BAD_REQUEST);
  }

}
