package it.gov.pagopa.pu.citizen.service.installment;

import it.gov.pagopa.pu.citizen.connector.debtpositions.InstallmentNoPIIService;
import it.gov.pagopa.pu.citizen.dto.generated.DebtorUnpaidDebtPositionInstallmentsDTO;
import it.gov.pagopa.pu.citizen.mapper.DebtorUnpaidDebtPositionInstallmentsMapper;
import it.gov.pagopa.pu.citizen.service.organization.OrganizationRetrieverService;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentNoPII;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstallmentRetrieverServiceImpl implements InstallmentRetrieverService{

  private final InstallmentNoPIIService installmentNoPIIService;
  private final OrganizationRetrieverService organizationRetrieverService;
  private final DebtorUnpaidDebtPositionInstallmentsMapper debtorUnpaidDebtPositionInstallmentsMapper;

  public InstallmentRetrieverServiceImpl(InstallmentNoPIIService installmentNoPIIService, OrganizationRetrieverService organizationRetrieverService, DebtorUnpaidDebtPositionInstallmentsMapper debtorUnpaidDebtPositionInstallmentsMapper) {
    this.installmentNoPIIService = installmentNoPIIService;
    this.organizationRetrieverService = organizationRetrieverService;
    this.debtorUnpaidDebtPositionInstallmentsMapper = debtorUnpaidDebtPositionInstallmentsMapper;
  }

  @Override
  public List<DebtorUnpaidDebtPositionInstallmentsDTO> getDebtorInstallmentNoPII(Long brokerId, Long debtPositionId, Long paymentOptionId, String xFiscalCode, Long organizationId, String accessToken) {
    Organization organization = organizationRetrieverService.getValidOrganization(organizationId, brokerId, accessToken);
    List<InstallmentNoPII> debtorInstallmentNoPII = installmentNoPIIService.getDebtorInstallmentNoPII(accessToken, debtPositionId, paymentOptionId, xFiscalCode, organizationId);
    return debtorUnpaidDebtPositionInstallmentsMapper.mapDebtorUnpaidDebtPositionInstallmentsList(organization, debtorInstallmentNoPII, debtPositionId);
  }
}
