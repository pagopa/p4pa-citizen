package it.gov.pagopa.pu.citizen.service.installment;

import io.micrometer.common.util.StringUtils;
import it.gov.pagopa.pu.citizen.connector.debtpositions.InstallmentService;
import it.gov.pagopa.pu.citizen.connector.debtpositions.TransferService;
import it.gov.pagopa.pu.citizen.connector.organization.OrganizationService;
import it.gov.pagopa.pu.citizen.dto.InstallmentDebtorExtendedDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtorUnpaidDebtPositionInstallmentsDTO;
import it.gov.pagopa.pu.citizen.exception.InvalidParamException;
import it.gov.pagopa.pu.citizen.exception.ResourceNotFoundException;
import it.gov.pagopa.pu.citizen.mapper.DebtorUnpaidDebtPositionInstallmentsMapper;
import it.gov.pagopa.pu.citizen.mapper.InstallmentDebtorExtendedDTOMapper;
import it.gov.pagopa.pu.citizen.service.organization.OrganizationRetrieverService;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentDebtorDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentNoPII;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentStatus;
import it.gov.pagopa.pu.debtpositions.dto.generated.PostalIbanVerifyResponse;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class InstallmentFacadeServiceImpl implements InstallmentFacadeService {

  private final OrganizationRetrieverService organizationRetrieverService;
  private final InstallmentService installmentService;
  private final InstallmentDebtorExtendedDTOMapper installmentDebtorExtendedDTOMapper;
  private final DebtorUnpaidDebtPositionInstallmentsMapper debtorUnpaidDebtPositionInstallmentsMapper;
  private final OrganizationService organizationService;
  private final TransferService transferService;

  public InstallmentFacadeServiceImpl(OrganizationRetrieverService organizationRetrieverService, InstallmentService installmentService, InstallmentDebtorExtendedDTOMapper installmentDebtorExtendedDTOMapper, DebtorUnpaidDebtPositionInstallmentsMapper debtorUnpaidDebtPositionInstallmentsMapper, OrganizationService organizationService, TransferService transferService) {
    this.organizationRetrieverService = organizationRetrieverService;
    this.installmentService = installmentService;
    this.installmentDebtorExtendedDTOMapper = installmentDebtorExtendedDTOMapper;
    this.debtorUnpaidDebtPositionInstallmentsMapper = debtorUnpaidDebtPositionInstallmentsMapper;
    this.organizationService = organizationService;
    this.transferService = transferService;
  }

  @Override
  public List<InstallmentDebtorExtendedDTO> getInstallmentByIuvOrNav(Long brokerId, String iuvOrNav,
                                                                     String debtorFiscalCode, String orgFiscalCode, List<InstallmentStatus> statuses, String accessToken) {
    if (StringUtils.isBlank(debtorFiscalCode) && StringUtils.isBlank(orgFiscalCode)) {
      throw new InvalidParamException("MISSING_FISCAL_CODE","Either debtorFiscalCode or orgFiscalCode must be provided");
    }
    Organization organization = resolveOrganization(orgFiscalCode, brokerId, accessToken);
    Long organizationId = organization != null ? organization.getOrganizationId() : null;

    if (statuses!= null && statuses.contains(InstallmentStatus.PAID)
      && !statuses.contains(InstallmentStatus.REPORTED)){
      statuses.add(InstallmentStatus.REPORTED);
    }

    List<InstallmentDebtorDTO> installments = installmentService.getInstallmentByIuvOrNav(iuvOrNav, debtorFiscalCode, organizationId, statuses, accessToken);
    if (CollectionUtils.isEmpty(installments)) {
      return Collections.emptyList();
    }

    PostalIbanVerifyResponse postalIbanVerifyResponse = extractPostalIbanVerifyResponse(installments, InstallmentDebtorDTO::getInstallmentId, accessToken);
    return installmentDebtorExtendedDTOMapper.map(
      installments,
      buildOrganizationMap(installments, organization, brokerId, accessToken),
      postalIbanVerifyResponse
    );
  }

  @Override
  public List<DebtorUnpaidDebtPositionInstallmentsDTO> getDebtorInstallmentNoPII(Long brokerId, Long debtPositionId, Long paymentOptionId, String xFiscalCode, Long organizationId, String accessToken) {
    Organization organization = organizationRetrieverService.getValidOrganization(organizationId, brokerId, accessToken);
    List<InstallmentNoPII> debtorInstallmentNoPII = installmentService.getDebtorInstallmentNoPII(accessToken, debtPositionId, paymentOptionId, xFiscalCode, organizationId);

    PostalIbanVerifyResponse postalIbanVerifyResponse = extractPostalIbanVerifyResponse(debtorInstallmentNoPII, InstallmentNoPII::getInstallmentId, accessToken);
    return debtorUnpaidDebtPositionInstallmentsMapper.mapDebtorUnpaidDebtPositionInstallmentsList(organization, debtorInstallmentNoPII, debtPositionId, postalIbanVerifyResponse);
  }

  private Organization resolveOrganization(String orgFiscalCode, Long brokerId, String accessToken) {
    if (StringUtils.isBlank(orgFiscalCode)) {
      return null;
    }

    boolean delegateBroker = organizationRetrieverService.isDelegateBroker(brokerId, accessToken);
    if (delegateBroker) {
      Organization brokerOrganization = organizationService.getBrokerOrganization(brokerId, accessToken);
      if(brokerOrganization==null){
        throw new ResourceNotFoundException("ORGANIZATION_NOT_FOUND","Broker's organization having brokerId "+brokerId+ " not found");
      }
      return brokerOrganization;
    }

    return organizationRetrieverService.getValidOrganization(orgFiscalCode, brokerId, accessToken);
  }

  private Map<Long, Organization> buildOrganizationMap(List<InstallmentDebtorDTO> installments,
      Organization organization, Long brokerId, String accessToken) {

    Long organizationId = organization != null ? organization.getOrganizationId() : null;
    Set<Long> organizationIds = installments.stream()
      .map(InstallmentDebtorDTO::getOrganizationId)
      .collect(Collectors.toSet());
    Map<Long, Organization> organizationMap = organizationIds.stream()
      .filter(orgId -> !Objects.equals(orgId, organizationId))
      .collect(Collectors.toMap(
        Function.identity(),
        orgId -> organizationRetrieverService.getValidOrganization(orgId, brokerId, accessToken)
      ));
    if (organizationId != null) {
      organizationMap.put(organizationId, organization);
    }
    return organizationMap;
  }

  @Override
  public <T> PostalIbanVerifyResponse extractPostalIbanVerifyResponse(List<T> installments, Function<T, Long> idExtractor, String accessToken ) {
    List<Long> installmentIds = installments.stream()
      .map(idExtractor)
      .toList();

    return transferService.verifyPostalIban(installmentIds, accessToken);
  }
}
