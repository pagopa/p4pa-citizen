package it.gov.pagopa.pu.citizen.service.debtpositiontypeorg;

import it.gov.pagopa.pu.citizen.connector.debtpositions.DebtPositionTypeOrgService;
import it.gov.pagopa.pu.citizen.connector.debtpositions.SpontaneousFormService;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDetailsDTO;
import it.gov.pagopa.pu.citizen.exception.ResourceNotFoundException;
import it.gov.pagopa.pu.citizen.mapper.DebtPositionTypeOrgsWithSpontaneousDTOMapper;
import it.gov.pagopa.pu.citizen.mapper.DebtPositionTypeOrgsWithSpontaneousDetailsDTOMapper;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtpositions.dto.generated.SpontaneousForm;
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
  @Mock
  private SpontaneousFormService spontaneousFormServiceMock;
  @Mock
  private DebtPositionTypeOrgsWithSpontaneousDetailsDTOMapper debtPositionTypeOrgsWithSpontaneousDetailsDTOMapperMock;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  DebtPositionTypeOrgRetrieverService debtPositionTypeOrgRetrieverService;

  @BeforeEach
  void setUp() {
    debtPositionTypeOrgRetrieverService = new DebtPositionTypeOrgRetrieverServiceImpl(debtPositionTypeOrgServiceMock, spontaneousFormServiceMock, debtPositionTypeOrgsListWithSpontaneousDTOMapperMock, debtPositionTypeOrgsWithSpontaneousDetailsDTOMapperMock);
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

  @Test
  void givenDebtPositionTypeOrgIdWhenGetDebtPositionTypeOrgsWithSpontaneousDetailsDTOThenDebtPositionTypeOrgsWithSpontaneousDetailsDTO() {
    //given
    Long debtPositionTypeOrgId = 3L;
    Long spontaneousFormId = 1L;
    String accessToken = "accessToken";

    DebtPositionTypeOrg debtPostionTypeOrg = podamFactory.manufacturePojo(DebtPositionTypeOrg.class);
    debtPostionTypeOrg.setSpontaneousFormId(spontaneousFormId);
    Mockito.when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrg(debtPositionTypeOrgId, accessToken)).thenReturn(debtPostionTypeOrg);

    SpontaneousForm spontaneousForm = podamFactory.manufacturePojo(SpontaneousForm.class);
    spontaneousForm.setSpontaneousFormId(spontaneousFormId);
    Mockito.when(spontaneousFormServiceMock.getSpontaneousForm(spontaneousFormId, accessToken)).thenReturn(spontaneousForm);

    DebtPositionTypeOrgsWithSpontaneousDetailsDTO expectedResult = podamFactory.manufacturePojo(DebtPositionTypeOrgsWithSpontaneousDetailsDTO.class);
    Mockito.when(debtPositionTypeOrgsWithSpontaneousDetailsDTOMapperMock.map(debtPostionTypeOrg, spontaneousForm)).thenReturn(expectedResult);
    //when
    DebtPositionTypeOrgsWithSpontaneousDetailsDTO result = debtPositionTypeOrgRetrieverService.getDebtPositionTypeOrgsWithSpontaneousDetailsDTO(debtPositionTypeOrgId, accessToken);
    //then
    assertNotNull(result);
    assertEquals(expectedResult, result);
  }

  @Test
  void givenNullDebtPositionTypeOrgIdWhenGetDebtPositionTypeOrgsWithSpontaneousDetailsDTOThenThrowException() {
    //given
    Long debtPositionTypeOrgId = 3L;
    String accessToken = "accessToken";

    Mockito.when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrg(debtPositionTypeOrgId, accessToken)).thenReturn(null);

    ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
      debtPositionTypeOrgRetrieverService.getDebtPositionTypeOrgsWithSpontaneousDetailsDTO(debtPositionTypeOrgId, accessToken));

    assertEquals("DebtPositionTypeOrg with deptPositionTypeOrgId 3 not found", ex.getMessage());
  }

  @Test
  void givenNullSpontaneousFormIdWhenGetDebtPositionTypeOrgsWithSpontaneousDetailsDTOThenReturnNullSpontaneousForm() {
    //given
    Long debtPositionTypeOrgId = 3L;
    String accessToken = "accessToken";

    DebtPositionTypeOrg debtPostionTypeOrg = podamFactory.manufacturePojo(DebtPositionTypeOrg.class);
    debtPostionTypeOrg.setSpontaneousFormId(null);

    Mockito.when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrg(debtPositionTypeOrgId, accessToken)).thenReturn(debtPostionTypeOrg);

    DebtPositionTypeOrgsWithSpontaneousDetailsDTO expectedResult = podamFactory.manufacturePojo(DebtPositionTypeOrgsWithSpontaneousDetailsDTO.class);
    expectedResult.setFormCustom(null);
    Mockito.when(debtPositionTypeOrgsWithSpontaneousDetailsDTOMapperMock.map(debtPostionTypeOrg, null)).thenReturn(expectedResult);
    //when
    DebtPositionTypeOrgsWithSpontaneousDetailsDTO result = debtPositionTypeOrgRetrieverService.getDebtPositionTypeOrgsWithSpontaneousDetailsDTO(debtPositionTypeOrgId, accessToken);
    //then
    assertNotNull(result);
    assertEquals(expectedResult, result);
    assertNull(result.getFormCustom());

  }


}
