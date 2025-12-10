package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.citizen.connector.debtpositions.client.InstallmentNoPIISearchClient;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentNoPII;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstallmentNoPIIServiceImpl implements InstallmentNoPIIService{

  private final InstallmentNoPIISearchClient installmentNoPiiSearchClient;

  public InstallmentNoPIIServiceImpl(InstallmentNoPIISearchClient installmentNoPiiSearchClient) {
    this.installmentNoPiiSearchClient = installmentNoPiiSearchClient;
  }

  @Override
  public List<InstallmentNoPII> getDebtorInstallmentNoPII(String accessToken, Long debtPositionId, Long paymentOptionId, String debtorFiscalCode, Long organizationId) {
    return installmentNoPiiSearchClient.getDebtorInstallmentNoPII(accessToken, debtPositionId, paymentOptionId, debtorFiscalCode, organizationId);
  }
}
