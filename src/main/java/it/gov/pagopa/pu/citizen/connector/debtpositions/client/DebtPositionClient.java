package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import it.gov.pagopa.pu.citizen.utils.PageUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionOrigin;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtorDebtPositionDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.PagedDebtorUnpaidDebtPositionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Slf4j
@Service
public class DebtPositionClient {

  private final DebtPositionsApisHolder debtPositionsApisHolder;

  public DebtPositionClient(DebtPositionsApisHolder debtPositionsApisHolder) {
    this.debtPositionsApisHolder = debtPositionsApisHolder;
  }

  public DebtPositionDTO createDebtPosition(DebtPositionDTO debtPositionDTO, Boolean massive, String accessToken){
    return debtPositionsApisHolder.getDebtPositionApi(accessToken).createDebtPosition(debtPositionDTO, massive);
  }

  public DebtPositionDTO getDebtPosition(Long debtPositionId,
      String accessToken) {
    try {
      return debtPositionsApisHolder.getDebtPositionApi(accessToken)
          .getDebtPosition(debtPositionId);
    } catch (HttpClientErrorException.NotFound e) {
      log.warn("DebtPosition with debtPositionId {} not found", debtPositionId);
      return null;
    }
  }

  public DebtPositionDTO getDebtPositionByInstallmentId(Long installmentId, String accessToken){
    try{
      return debtPositionsApisHolder.getDebtPositionApi(accessToken).getDebtPositionByInstallmentId(installmentId);

    } catch (HttpClientErrorException.NotFound e) {
      log.warn("DebtPosition having installment with Id {} not found", installmentId);
      return null;
    }
  }

  public List<DebtPositionDTO> getDebtPositionsByOrganizationIdAndIuv(Long organizationId, String iuv, List<DebtPositionOrigin> debtPositionOrigins, String accessToken){
    return debtPositionsApisHolder.getDebtPositionApi(accessToken).getDebtPositionsByOrganizationIdAndIuv(organizationId, iuv, debtPositionOrigins);
  }

  public List<DebtPositionDTO> getDebtPositionsByOrganizationIdAndIud(Long organizationId, String iud, List<DebtPositionOrigin> debtPositionOrigins, String accessToken){
    return debtPositionsApisHolder.getDebtPositionApi(accessToken).getDebtPositionsByOrganizationIdAndIud(organizationId, iud, debtPositionOrigins);
  }

  public PagedDebtorUnpaidDebtPositionDTO getPagedDebtorUnpaidDebtPosition(String debtorFiscalCode, List<Long> organizationIds, Pageable pageable, String accessToken){
    return debtPositionsApisHolder.getDebtPositionApi(accessToken).getPagedDebtorUnpaidDebtPositions(
      debtorFiscalCode,
      organizationIds,
      PageUtils.getPageNumber(pageable),
      PageUtils.getPageSize(pageable),
      PageUtils.getSortList(pageable));
  }

  public DebtorDebtPositionDTO getDebtorDebtPositionOverview(Long debtPositionId, String debtorFiscalCode, Long organizationId, String accessToken){
    try {
      return debtPositionsApisHolder.getDebtPositionApi(accessToken)
        .getDebtorUnpaidDebtPositionOverview(debtPositionId, debtorFiscalCode, organizationId);
    }catch (HttpClientErrorException.NotFound e) {
      log.warn("DebtorDebtPositionDTO having debtPositionId {}  and organizationId {} not found", debtPositionId, organizationId);
      return null;
    }
  }
}
