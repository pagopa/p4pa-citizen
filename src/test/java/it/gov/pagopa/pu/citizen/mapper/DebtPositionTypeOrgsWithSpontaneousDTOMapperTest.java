package it.gov.pagopa.pu.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDTO;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionTypeOrg;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DebtPositionTypeOrgsWithSpontaneousDTOMapperTest {

  DebtPositionTypeOrgsWithSpontaneousDTOMapper mapper = Mappers.getMapper(DebtPositionTypeOrgsWithSpontaneousDTOMapper.class);
  private static final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @Test
  void givenDebtPositionTypeOrgListWhenMapThenReturnDebtPositionTypeOrgsWithSpontaneousDTOList() {
    //given
    List<DebtPositionTypeOrg> debtPostionTypeOrgList = podamFactory.manufacturePojo(List.class, DebtPositionTypeOrg.class);

    //when
    List<DebtPositionTypeOrgsWithSpontaneousDTO> result = mapper.map(debtPostionTypeOrgList);
    //then
    assertNotNull(result);
    assertFalse(result.isEmpty());
    result.forEach(TestUtils::checkNotNullFields);
  }
}
