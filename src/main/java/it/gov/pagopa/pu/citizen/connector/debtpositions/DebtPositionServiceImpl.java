package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.citizen.connector.debtpositions.client.DebtPositionClient;
import it.gov.pagopa.pu.citizen.connector.debtpositions.client.DebtPositionViewSearchClient;
import it.gov.pagopa.pu.citizen.connector.debtpositions.client.InstallmentNoPiiSearchClient;
import it.gov.pagopa.pu.citizen.connector.debtpositions.client.PaymentOptionSearchClient;
import it.gov.pagopa.pu.debtpositions.dto.generated.*;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DebtPositionServiceImpl implements DebtPositionService{

  private final DebtPositionClient debtPositionClient;
  private final DebtPositionViewSearchClient debtPositionViewSearchClient;
  private final PaymentOptionSearchClient paymentOptionSearchClient;
  private final InstallmentNoPiiSearchClient installmentNoPiiSearchClient;

  public DebtPositionServiceImpl(DebtPositionClient debtPositionClient, DebtPositionViewSearchClient debtPositionViewSearchClient, PaymentOptionSearchClient paymentOptionSearchClient, InstallmentNoPiiSearchClient installmentNoPiiSearchClient) {
    this.debtPositionClient = debtPositionClient;
    this.debtPositionViewSearchClient = debtPositionViewSearchClient;
    this.paymentOptionSearchClient = paymentOptionSearchClient;
    this.installmentNoPiiSearchClient = installmentNoPiiSearchClient;
  }

  @Override
  public DebtPositionDTO createDebtPosition(DebtPositionDTO debtPositionDTO, Boolean massive, String accessToken){
    return debtPositionClient.createDebtPosition(debtPositionDTO, massive, accessToken);
  }

  @Override
  public DebtPositionDTO getDebtPosition(Long debtPositionId, String accessToken) {
    return debtPositionClient.getDebtPosition(debtPositionId, accessToken);
  }

  @Override
  public DebtPositionDTO getDebtPositionByInstallmentId(Long installmentId, String accessToken) {
    return debtPositionClient.getDebtPositionByInstallmentId(installmentId,accessToken);
  }

  @Override
  public List<DebtPositionDTO> getDebtPositionsByOrganizationIdAndIud(Long organizationId, String iud, List<DebtPositionOrigin> debtPositionOrigins, String accessToken) {
    return debtPositionClient.getDebtPositionsByOrganizationIdAndIud(organizationId, iud, debtPositionOrigins, accessToken);
  }

  @Override
  public List<DebtPositionDTO> getDebtPositionsByOrganizationIdAndIuv(Long organizationId, String iuv, List<DebtPositionOrigin> debtPositionOrigins, String accessToken) {
    return debtPositionClient.getDebtPositionsByOrganizationIdAndIuv(organizationId, iuv, debtPositionOrigins, accessToken);
  }

  @Override
  public PagedModelDebtPositionView getPagedModelDebtPositionView(List<Long> organizationIds, String debtorFiscalCode, String accessToken, Pageable pageable) {
    return debtPositionViewSearchClient.getPagedModelDebtPositionView(organizationIds, debtorFiscalCode, accessToken, pageable);
  }

  @Override
  public List<PaymentOption> getPaymentOptions(Long debtPositionId, String accessToken) {
    return paymentOptionSearchClient.getPaymentOptions(debtPositionId, accessToken);
  }

  @Override
  public List<InstallmentNoPII> getInstallments(Long debtPositionId, String installmentStatuses, String accessToken) {
    return installmentNoPiiSearchClient.getInstallments(debtPositionId, installmentStatuses, accessToken);
  }
}
