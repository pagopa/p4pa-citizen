package it.gov.pagopa.pu.citizen.utils;

import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionOrigin;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentStatus;

import java.util.List;

public class DebtPositionConstants {

  private DebtPositionConstants(){}

  public static final List<DebtPositionOrigin> ORDINARY_DEBTPOSITION_ORIGINS = List.of(
    DebtPositionOrigin.ORDINARY,
    DebtPositionOrigin.ORDINARY_SIL,
    DebtPositionOrigin.SPONTANEOUS,
    DebtPositionOrigin.SPONTANEOUS_SIL
  );

  public static final List<InstallmentStatus> PAYABLE_STATUSES = List.of(
    InstallmentStatus.UNPAID,
    InstallmentStatus.EXPIRED
  );

  public static final String INSTALLMENT_REMITTANCE_INFORMATION_PLACEHOLDER = "Pagamento on-the-fly";
}

