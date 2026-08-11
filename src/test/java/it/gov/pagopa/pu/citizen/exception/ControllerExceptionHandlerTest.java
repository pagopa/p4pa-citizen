package it.gov.pagopa.pu.citizen.exception;

import it.gov.pagopa.pu.citizen.exception.common.CommonExceptionHandlerTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.doThrow;

class ControllerExceptionHandlerTest extends CommonExceptionHandlerTest {

  @Test
  void handleZipFileException() throws Exception {
    doThrow(new ZipFileException("ZIPPING_ERROR", "Error")).when(testControllerSpy).testEndpoint(DATA, BODY);

    performRequest(DATA, MediaType.APPLICATION_JSON)
      .andExpect(MockMvcResultMatchers.status().isInternalServerError())
      .andExpect(MockMvcResultMatchers.jsonPath("$.category").value("GENERIC_ERROR"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("ZIPPING_ERROR"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Error"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.fields").doesNotExist())
      .andExpect(MockMvcResultMatchers.jsonPath("$.traceId").value(traceId));
  }

  @Test
  void handleInvalidParamException() throws Exception {
    doThrow(new InvalidParamException("INVALID_PARAM", "Error")).when(testControllerSpy).testEndpoint(DATA, BODY);

    performRequest(DATA, MediaType.APPLICATION_JSON)
      .andExpect(MockMvcResultMatchers.status().isBadRequest())
      .andExpect(MockMvcResultMatchers.jsonPath("$.category").value("BAD_REQUEST"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("INVALID_PARAM"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Error"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.fields").doesNotExist())
      .andExpect(MockMvcResultMatchers.jsonPath("$.traceId").value(traceId));
  }

  @Test
  void handleInvalidAccessTokenException() throws Exception {
    doThrow(new InvalidAccessTokenException("INVALID_ACCESS_TOKEN", "Error")).when(testControllerSpy).testEndpoint(DATA, BODY);

    performRequest(DATA, MediaType.APPLICATION_JSON)
      .andExpect(MockMvcResultMatchers.status().isBadRequest())
      .andExpect(MockMvcResultMatchers.jsonPath("$.category").value("BAD_REQUEST"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Error"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("INVALID_ACCESS_TOKEN"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.fields").doesNotExist())
      .andExpect(MockMvcResultMatchers.jsonPath("$.traceId").value(traceId));
  }

  @Test
  void handleDebtPositionInvalidFieldValuesException() throws Exception {
    String errorCode = "INVALID_FIELD_VALUES";
    doThrow(new DebtPositionInvalidFieldValuesException(errorCode, "Error")).when(testControllerSpy).testEndpoint(DATA, BODY);

    performRequest(DATA, MediaType.APPLICATION_JSON)
      .andExpect(MockMvcResultMatchers.status().isBadRequest())
      .andExpect(MockMvcResultMatchers.jsonPath("$.category").value("BAD_REQUEST"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Error"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(errorCode))
      .andExpect(MockMvcResultMatchers.jsonPath("$.traceId").value(traceId));
  }

  @Test
  void handleInvalidRequestBodyExceptionException() throws Exception {
    String errorCode = "INVALID_REQUEST_BODY";
    doThrow(new InvalidRequestBodyException(errorCode, "Error")).when(testControllerSpy).testEndpoint(DATA, BODY);

    performRequest(DATA, MediaType.APPLICATION_JSON)
      .andExpect(MockMvcResultMatchers.status().isBadRequest())
      .andExpect(MockMvcResultMatchers.jsonPath("$.category").value("BAD_REQUEST"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Error"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(errorCode))
      .andExpect(MockMvcResultMatchers.jsonPath("$.fields").doesNotExist())
      .andExpect(MockMvcResultMatchers.jsonPath("$.traceId").value(traceId));
  }
}
