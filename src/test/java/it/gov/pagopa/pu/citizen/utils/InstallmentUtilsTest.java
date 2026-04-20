package it.gov.pagopa.pu.citizen.utils;

import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentStatus;
import it.gov.pagopa.pu.debtpositions.dto.generated.PostalIbanVerifyResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

class InstallmentUtilsTest {

  @ParameterizedTest
  @MethodSource("installmentStatusSource")
  void whenResolveInstallmentStatusThenOk(InstallmentStatus status, InstallmentStatus expectedStatus) {
    InstallmentStatus result = InstallmentUtils.resolveInstallmentStatus(status);

    Assertions.assertEquals(expectedStatus, result);
  }

  static Stream<Arguments> installmentStatusSource() {
    return Stream.of(
      Arguments.of(InstallmentStatus.UNPAID, InstallmentStatus.UNPAID),
      Arguments.of(InstallmentStatus.REPORTED, InstallmentStatus.PAID),
      Arguments.of(null, null)
    );
  }

  @ParameterizedTest
  @CsvSource({
    "1, false",
    "2, true"
  })
  void whenExtractAllCCPThenOk(Long installmentId, Boolean expectedResult) {
    // given
    PostalIbanVerifyResponse postalIbanVerifyResponse = new PostalIbanVerifyResponse();
    postalIbanVerifyResponse.setInstallmentPostalIbanCheck(
      Map.of("1", false, "2", true)
    );

    // when
    Boolean result = InstallmentUtils.extractAllCCP(installmentId, postalIbanVerifyResponse);

    // then
    Assertions.assertEquals(expectedResult, result);
  }
}
