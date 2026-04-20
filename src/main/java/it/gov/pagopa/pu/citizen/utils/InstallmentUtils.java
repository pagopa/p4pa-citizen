package it.gov.pagopa.pu.citizen.utils;

import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentStatus;
import it.gov.pagopa.pu.debtpositions.dto.generated.PostalIbanVerifyResponse;

public class InstallmentUtils {

  private InstallmentUtils() {}

  public static InstallmentStatus resolveInstallmentStatus(InstallmentStatus status){
    if(InstallmentStatus.REPORTED.equals(status)){
      return InstallmentStatus.PAID;
    }
    return status;
  }

  public static Boolean extractAllCCP(Long installmentId, PostalIbanVerifyResponse postalIbanVerifyResponse){
    return postalIbanVerifyResponse.getInstallmentPostalIbanCheck().get(String.valueOf(installmentId));
  }
}
