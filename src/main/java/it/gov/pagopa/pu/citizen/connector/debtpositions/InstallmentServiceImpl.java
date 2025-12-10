package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.citizen.connector.debtpositions.client.InstallmentClient;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentDebtorDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstallmentServiceImpl implements InstallmentService {

  private final InstallmentClient installmentClient;

  public InstallmentServiceImpl(InstallmentClient installmentClient) {
    this.installmentClient = installmentClient;
  }

  @Override
  public List<InstallmentDebtorDTO> getInstallmentByIuvOrNav(String iuvOrNav, String debtorFiscalCode, Long organizationId, String accessToken) {
    return installmentClient.getInstallmentByIuvOrNav(iuvOrNav, debtorFiscalCode, organizationId, accessToken);
  }
}
