package it.gov.pagopa.pu.citizen.service.debtposition;

import it.gov.pagopa.pu.citizen.connector.debtpositions.DebtPositionService;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.mapper.DebtPositionDTOMapper;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DebtPositionRetrieverServiceImpl implements DebtPositionRetrieverService{

  private final DebtPositionService debtPositionService;
  private final DebtPositionDTOMapper debtPositionDTOMapper;
  private final Integer dueDateOffsetDays;

  public DebtPositionRetrieverServiceImpl(DebtPositionService debtPositionService,
                                          DebtPositionDTOMapper debtPositionDTOMapper,
                                          @Value("${rest.spontaneous.dueDateOffsetDays}")Integer dueDateOffsetDays
  ) {
    this.debtPositionDTOMapper = debtPositionDTOMapper;
    this.debtPositionService = debtPositionService;
    this.dueDateOffsetDays =dueDateOffsetDays;
  }

  @Override
  public DebtPositionDTO createSpontaneousDebtPosition(DebtPositionRequestDTO debtPositionRequestDTO, String accessToken) {
    return debtPositionService.createDebtPosition(debtPositionDTOMapper.mapSpontaneousDebtPositionDTO(debtPositionRequestDTO, dueDateOffsetDays), false, accessToken);
  }

}
