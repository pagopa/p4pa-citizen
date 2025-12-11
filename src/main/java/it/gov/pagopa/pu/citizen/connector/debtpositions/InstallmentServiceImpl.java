package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.citizen.connector.debtpositions.client.InstallmentClient;
import it.gov.pagopa.pu.citizen.connector.debtpositions.client.InstallmentNoPIISearchClient;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentDebtorDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentNoPII;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstallmentServiceImpl implements InstallmentService {

  private final InstallmentClient installmentClient;

  private final InstallmentNoPIISearchClient installmentNoPiiSearchClient;

  public InstallmentServiceImpl(InstallmentClient installmentClient, InstallmentNoPIISearchClient installmentNoPiiSearchClient) {
    this.installmentClient = installmentClient;
    this.installmentNoPiiSearchClient = installmentNoPiiSearchClient;
  }

  @Override
  public List<InstallmentDebtorDTO> getInstallmentByIuvOrNav(String iuvOrNav, String debtorFiscalCode, Long organizationId, String accessToken) {
    return installmentClient.getInstallmentByIuvOrNav(iuvOrNav, debtorFiscalCode, organizationId, accessToken);
  }

  @Override
  public List<InstallmentNoPII> getDebtorInstallmentNoPII(String accessToken, Long debtPositionId, Long paymentOptionId, String debtorFiscalCode, Long organizationId) {
    return installmentNoPiiSearchClient.getDebtorInstallmentNoPII(accessToken, debtPositionId, paymentOptionId, debtorFiscalCode, organizationId);
  }
}
