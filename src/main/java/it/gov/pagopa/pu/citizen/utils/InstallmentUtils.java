package it.gov.pagopa.pu.citizen.utils;

import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentStatus;

public class InstallmentUtils {

  private InstallmentUtils() {}

  public static InstallmentStatus resolveInstallmentStatus(InstallmentStatus status){
    if(InstallmentStatus.REPORTED.equals(status)){
      return InstallmentStatus.PAID;
    }
    return status;
  }
}
