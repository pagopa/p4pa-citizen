package it.gov.pagopa.pu.citizen.service.debtpositiontypeorg;

import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDetailsDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DebtPositionTypeOrgRetrieverService {
  List<DebtPositionTypeOrgsWithSpontaneousDTO> getDebtPositionTypeOrgsWithSpontaneous(Long brokerId, Long organizationId, String accessToken);
  DebtPositionTypeOrgsWithSpontaneousDetailsDTO getDebtPositionTypeOrgsWithSpontaneousDetailsDTO(Long brokerId, Long organizationId, Long debtPositionTypeOrgId, String accessToken);
  List<DebtPositionTypeOrgsWithSpontaneousDTO> getMostUsedSpontaneousDebtPositionTypeOrgsForCurrentYear(Long brokerId, Long organizationId, Pageable pageable, String accessToken);
}
