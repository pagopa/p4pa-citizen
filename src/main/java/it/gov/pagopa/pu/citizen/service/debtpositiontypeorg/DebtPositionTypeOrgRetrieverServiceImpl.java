package it.gov.pagopa.pu.citizen.service.debtpositiontypeorg;

import it.gov.pagopa.pu.citizen.connector.debtpositions.DebtPositionTypeOrgService;
import it.gov.pagopa.pu.citizen.connector.debtpositions.SpontaneousFormService;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDetailsDTO;
import it.gov.pagopa.pu.citizen.exception.ResourceNotFoundException;
import it.gov.pagopa.pu.citizen.mapper.DebtPositionTypeOrgsWithSpontaneousDTOMapper;
import it.gov.pagopa.pu.citizen.mapper.DebtPositionTypeOrgsWithSpontaneousDetailsDTOMapper;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtpositions.dto.generated.SpontaneousForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DebtPositionTypeOrgRetrieverServiceImpl implements DebtPositionTypeOrgRetrieverService{

  private final DebtPositionTypeOrgService debtPositionTypeOrgService;
  private final SpontaneousFormService spontaneousFormService;
  private final DebtPositionTypeOrgsWithSpontaneousDTOMapper debtPositionTypeOrgsListWithSpontaneousDTOMapper;
  private final DebtPositionTypeOrgsWithSpontaneousDetailsDTOMapper debtPositionTypeOrgsWithSpontaneousDetailsDTOMapper;

  public DebtPositionTypeOrgRetrieverServiceImpl(DebtPositionTypeOrgService debtPositionTypeOrgService, SpontaneousFormService spontaneousFormService, DebtPositionTypeOrgsWithSpontaneousDTOMapper debtPositionTypeOrgsListWithSpontaneousDTOMapper, DebtPositionTypeOrgsWithSpontaneousDetailsDTOMapper debtPositionTypeOrgsWithSpontaneousDetailsDTOMapper) {
    this.debtPositionTypeOrgService = debtPositionTypeOrgService;
    this.spontaneousFormService = spontaneousFormService;
    this.debtPositionTypeOrgsListWithSpontaneousDTOMapper = debtPositionTypeOrgsListWithSpontaneousDTOMapper;
    this.debtPositionTypeOrgsWithSpontaneousDetailsDTOMapper = debtPositionTypeOrgsWithSpontaneousDetailsDTOMapper;
  }

  @Override
  public List<DebtPositionTypeOrgsWithSpontaneousDTO> getDebtPositionTypeOrgsWithSpontaneous(Long organizationId, String accessToken) {
    return debtPositionTypeOrgsListWithSpontaneousDTOMapper.map(debtPositionTypeOrgService.getDebtPositionTypeOrgsFindActiveDebtPositionTypeOrg(organizationId, accessToken));
  }

  @Override
  public DebtPositionTypeOrgsWithSpontaneousDetailsDTO getDebtPositionTypeOrgsWithSpontaneousDetailsDTO(Long debtPositionTypeOrgId, String accessToken) {
    DebtPositionTypeOrg debtPositionTypeOrg = debtPositionTypeOrgService.getDebtPositionTypeOrg(debtPositionTypeOrgId, accessToken);

    if (debtPositionTypeOrg == null){
      throw new ResourceNotFoundException("DebtPositionTypeOrg with deptPositionTypeOrgId %d not found".formatted(debtPositionTypeOrgId));
    }

    return debtPositionTypeOrgsWithSpontaneousDetailsDTOMapper.map(debtPositionTypeOrg, extractSpontaneousForm(accessToken, debtPositionTypeOrg));
  }

  private SpontaneousForm extractSpontaneousForm(String accessToken, DebtPositionTypeOrg debtPositionTypeOrg) {
    if (debtPositionTypeOrg.getSpontaneousFormId() != null){
      return spontaneousFormService.getSpontaneousForm(debtPositionTypeOrg.getSpontaneousFormId(), accessToken);
    }
    return null;
  }
}
