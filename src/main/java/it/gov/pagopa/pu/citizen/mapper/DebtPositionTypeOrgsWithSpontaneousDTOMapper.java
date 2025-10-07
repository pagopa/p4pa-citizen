package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionTypeOrg;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DebtPositionTypeOrgsWithSpontaneousDTOMapper {

  List<DebtPositionTypeOrgsWithSpontaneousDTO> map(List<DebtPositionTypeOrg> debtPositionTypeOrg);

}
