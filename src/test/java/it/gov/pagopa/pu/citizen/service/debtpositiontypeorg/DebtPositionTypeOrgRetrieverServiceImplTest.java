package it.gov.pagopa.pu.citizen.service.debtpositiontypeorg;

import it.gov.pagopa.pu.citizen.connector.debtpositions.DebtPositionTypeOrgService;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDTO;
import it.gov.pagopa.pu.citizen.mapper.DebtPositionTypeOrgsWithSpontaneousDTOMapper;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionTypeOrg;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DebtPositionTypeOrgRetrieverServiceImplTest {

  @Mock
  private DebtPositionTypeOrgService debtPositionTypeOrgServiceMock;
  @Mock
  private DebtPositionTypeOrgsWithSpontaneousDTOMapper debtPositionTypeOrgsListWithSpontaneousDTOMapperMock;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  DebtPositionTypeOrgRetrieverService debtPositionTypeOrgRetrieverService;

  @BeforeEach
  void setUp() {
    debtPositionTypeOrgRetrieverService = new DebtPositionTypeOrgRetrieverServiceImpl(debtPositionTypeOrgServiceMock, debtPositionTypeOrgsListWithSpontaneousDTOMapperMock);
  }

  @AfterEach
  void mockitoVerify(){
    Mockito.verifyNoMoreInteractions(debtPositionTypeOrgServiceMock, debtPositionTypeOrgsListWithSpontaneousDTOMapperMock);
  }

  @Test
  void givenOrganizationIdWhenGetDebtPositionTypeOrgsWithSpontaneousThenReturnDebtPositionTypeOrgsWithSpontaneousDTOList() {
    //given
    Long organizationId = 3L;
    String accessToken = "accessToken";

    List<DebtPositionTypeOrg> debtPostionTypeOrgList = podamFactory.manufacturePojo(List.class, DebtPositionTypeOrg.class);
    Mockito.when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrgsFindActiveDebtPositionTypeOrg(organizationId, accessToken)).thenReturn(debtPostionTypeOrgList);

    List<DebtPositionTypeOrgsWithSpontaneousDTO> expectedResult = podamFactory.manufacturePojo(List.class, DebtPositionTypeOrgsWithSpontaneousDTO.class);
    Mockito.when(debtPositionTypeOrgsListWithSpontaneousDTOMapperMock.map(debtPostionTypeOrgList)).thenReturn(expectedResult);
    //when
    List<DebtPositionTypeOrgsWithSpontaneousDTO> result = debtPositionTypeOrgRetrieverService.getDebtPositionTypeOrgsWithSpontaneous(organizationId, accessToken);
    //then
    assertNotNull(result);
    assertEquals(expectedResult, result);
  }
}
