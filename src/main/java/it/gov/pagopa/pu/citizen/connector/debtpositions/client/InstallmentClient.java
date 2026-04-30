package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentDebtorDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class InstallmentClient {

  private final DebtPositionsApisHolder debtPositionsApisHolder;

  public InstallmentClient(DebtPositionsApisHolder debtPositionsApisHolder) {
    this.debtPositionsApisHolder = debtPositionsApisHolder;
  }

  public List<InstallmentDebtorDTO> getInstallmentByIuvOrNav(String iuvOrNav, String debtorFiscalCode, Long organizationId, List<InstallmentStatus> statuses, String accessToken){
      return debtPositionsApisHolder.getInstallmentApi(accessToken)
        .getInstallmentsByIuvOrNav(iuvOrNav,debtorFiscalCode,organizationId, statuses);
  }
}
