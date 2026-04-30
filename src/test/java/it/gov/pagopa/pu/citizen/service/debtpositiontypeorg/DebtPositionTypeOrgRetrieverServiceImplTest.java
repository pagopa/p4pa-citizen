package it.gov.pagopa.pu.citizen.service.debtpositiontypeorg;

import it.gov.pagopa.pu.citizen.connector.debtpositions.DebtPositionTypeOrgService;
import it.gov.pagopa.pu.citizen.connector.debtpositions.SpontaneousFormService;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDetailsDTO;
import it.gov.pagopa.pu.citizen.exception.ResourceNotFoundException;
import it.gov.pagopa.pu.citizen.mapper.DebtPositionTypeOrgsWithSpontaneousDTOMapper;
import it.gov.pagopa.pu.citizen.mapper.DebtPositionTypeOrgsWithSpontaneousDetailsDTOMapper;
import it.gov.pagopa.pu.citizen.service.organization.OrganizationRetrieverService;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtpositions.dto.generated.SpontaneousForm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import uk.co.jemos.podam.api.PodamFactory;

import java.time.OffsetDateTime;
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
  @Mock
  private OrganizationRetrieverService organizationRetrieverServiceMock;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  DebtPositionTypeOrgRetrieverService debtPositionTypeOrgRetrieverService;

  @BeforeEach
  void setUp() {
    debtPositionTypeOrgRetrieverService = new DebtPositionTypeOrgRetrieverServiceImpl(debtPositionTypeOrgServiceMock, spontaneousFormServiceMock, debtPositionTypeOrgsListWithSpontaneousDTOMapperMock, debtPositionTypeOrgsWithSpontaneousDetailsDTOMapperMock, organizationRetrieverServiceMock);
  }

  @AfterEach
  void mockitoVerify(){
    Mockito.verifyNoMoreInteractions(debtPositionTypeOrgServiceMock, spontaneousFormServiceMock, debtPositionTypeOrgsListWithSpontaneousDTOMapperMock, debtPositionTypeOrgsWithSpontaneousDetailsDTOMapperMock, organizationRetrieverServiceMock);
  }

  @Test
  void givenOrganizationIdWhenGetDebtPositionTypeOrgsWithSpontaneousThenReturnDebtPositionTypeOrgsWithSpontaneousDTOList() {
    //given
    Long brokerId = 1L;
    Long organizationId = 3L;
    String accessToken = "accessToken";

    Mockito.doNothing().when(organizationRetrieverServiceMock).validateOrganization(organizationId, brokerId, accessToken);
    List<DebtPositionTypeOrg> debtPostionTypeOrgList = podamFactory.manufacturePojo(List.class, DebtPositionTypeOrg.class);
    Mockito.when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrgsFindActiveDebtPositionTypeOrg(organizationId, accessToken)).thenReturn(debtPostionTypeOrgList);

    List<DebtPositionTypeOrgsWithSpontaneousDTO> expectedResult = podamFactory.manufacturePojo(List.class, DebtPositionTypeOrgsWithSpontaneousDTO.class);
    Mockito.when(debtPositionTypeOrgsListWithSpontaneousDTOMapperMock.map(debtPostionTypeOrgList)).thenReturn(expectedResult);
    //when
    List<DebtPositionTypeOrgsWithSpontaneousDTO> result = debtPositionTypeOrgRetrieverService.getDebtPositionTypeOrgsWithSpontaneous(brokerId, organizationId, accessToken);
    //then
    assertNotNull(result);
    assertEquals(expectedResult, result);
  }

  @Test
  void givenDebtPositionTypeOrgIdWhenGetDebtPositionTypeOrgsWithSpontaneousDetailsDTOThenDebtPositionTypeOrgsWithSpontaneousDetailsDTO() {
    //given
    Long debtPositionTypeOrgId = 3L;
    Long spontaneousFormId = 1L;
    Long organizationId = 3L;
    Long brokerId = 1L;
    String accessToken = "accessToken";

    DebtPositionTypeOrg debtPostionTypeOrg = podamFactory.manufacturePojo(DebtPositionTypeOrg.class);
    debtPostionTypeOrg.setSpontaneousFormId(spontaneousFormId);
    debtPostionTypeOrg.setOrganizationId(organizationId);
    Mockito.doNothing().when(organizationRetrieverServiceMock).validateOrganization(organizationId, brokerId, accessToken);
    Mockito.when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrg(debtPositionTypeOrgId, accessToken)).thenReturn(debtPostionTypeOrg);

    SpontaneousForm spontaneousForm = podamFactory.manufacturePojo(SpontaneousForm.class);
    spontaneousForm.setSpontaneousFormId(spontaneousFormId);
    Mockito.when(spontaneousFormServiceMock.getSpontaneousForm(spontaneousFormId, accessToken)).thenReturn(spontaneousForm);

    DebtPositionTypeOrgsWithSpontaneousDetailsDTO expectedResult = podamFactory.manufacturePojo(DebtPositionTypeOrgsWithSpontaneousDetailsDTO.class);
    Mockito.when(debtPositionTypeOrgsWithSpontaneousDetailsDTOMapperMock.map(debtPostionTypeOrg, spontaneousForm)).thenReturn(expectedResult);
    //when
    DebtPositionTypeOrgsWithSpontaneousDetailsDTO result = debtPositionTypeOrgRetrieverService.getDebtPositionTypeOrgsWithSpontaneousDetailsDTO(brokerId, organizationId, debtPositionTypeOrgId, accessToken);
    //then
    assertNotNull(result);
    assertEquals(expectedResult, result);
  }

  @Test
  void givenNullDebtPositionTypeOrgIdWhenGetDebtPositionTypeOrgsWithSpontaneousDetailsDTOThenThrowException() {
    //given
    Long organizationId = 3L;
    Long debtPositionTypeOrgId = 3L;
    Long brokerId = 1L;
    String accessToken = "accessToken";

    Mockito.doNothing().when(organizationRetrieverServiceMock).validateOrganization(organizationId, brokerId, accessToken);
    Mockito.when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrg(debtPositionTypeOrgId, accessToken)).thenReturn(null);

    ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
      debtPositionTypeOrgRetrieverService.getDebtPositionTypeOrgsWithSpontaneousDetailsDTO(brokerId, organizationId, debtPositionTypeOrgId, accessToken));

    assertEquals("DebtPositionTypeOrg with deptPositionTypeOrgId 3 and organizationId 3 not found", ex.getMessage());
  }

  @Test
  void givenNullSpontaneousFormIdWhenGetDebtPositionTypeOrgsWithSpontaneousDetailsDTOThenReturnNullSpontaneousForm() {
    Long organizationId = 10L;
    Long debtPositionTypeOrgId = 3L;
    Long brokerId = 1L;
    String accessToken = "accessToken";

    DebtPositionTypeOrg org = podamFactory.manufacturePojo(DebtPositionTypeOrg.class);
    org.setOrganizationId(organizationId);
    org.setSpontaneousFormId(null);

    Mockito.doNothing().when(organizationRetrieverServiceMock).validateOrganization(organizationId, brokerId, accessToken);
    Mockito.when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrg(debtPositionTypeOrgId, accessToken))
      .thenReturn(org);

    DebtPositionTypeOrgsWithSpontaneousDetailsDTO expected =
      podamFactory.manufacturePojo(DebtPositionTypeOrgsWithSpontaneousDetailsDTO.class);
    expected.setFormCustom(null);

    Mockito.when(debtPositionTypeOrgsWithSpontaneousDetailsDTOMapperMock.map(org, null))
      .thenReturn(expected);

    DebtPositionTypeOrgsWithSpontaneousDetailsDTO result =  debtPositionTypeOrgRetrieverService
      .getDebtPositionTypeOrgsWithSpontaneousDetailsDTO(brokerId, organizationId, debtPositionTypeOrgId, accessToken);

    assertNotNull(result);
    assertNull(result.getFormCustom());
    assertEquals(expected, result);
  }

  @Test
  void givenDifferentOrganizationIdWhenGetDebtPositionTypeOrgsWithSpontaneousDetailsDTOThenThrowException() {
    Long organizationId = 10L;
    Long debtPositionTypeOrgId = 3L;
    Long brokerId = 1L;
    String accessToken = "accessToken";

    DebtPositionTypeOrg org = podamFactory.manufacturePojo(DebtPositionTypeOrg.class);
    org.setOrganizationId(99L);
    Mockito.doNothing().when(organizationRetrieverServiceMock).validateOrganization(organizationId, brokerId, accessToken);
    Mockito.when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrg(debtPositionTypeOrgId, accessToken))
      .thenReturn(org);

    assertThrows(ResourceNotFoundException.class, () ->
      debtPositionTypeOrgRetrieverService.getDebtPositionTypeOrgsWithSpontaneousDetailsDTO(
        brokerId, organizationId, debtPositionTypeOrgId, accessToken));
  }

  @Test
  void givenBrokerIdAndOrganizationIdWhenGetMostUsedSpontaneousDebtPositionTypeOrgsThenReturnDTOList() {
    // given
    Long brokerId = 1L;
    Long organizationId = 3L;
    String accessToken = "accessToken";
    OffsetDateTime offsetDateTimeFrom = OffsetDateTime.now().minusYears(1);
    OffsetDateTime offsetDateTimeTo= OffsetDateTime.now();
    Pageable pageable = PageRequest.of(0, 10);

    Mockito.doNothing()
      .when(organizationRetrieverServiceMock)
      .validateOrganization(organizationId, brokerId, accessToken);

    List<DebtPositionTypeOrg> debtPositionTypeOrgList =
      podamFactory.manufacturePojo(List.class, DebtPositionTypeOrg.class);

    Mockito.when(
      debtPositionTypeOrgServiceMock
        .getMostUsedSpontaneousDebtPositionTypesForOrganizationByOrganizationIdAndDate(
          organizationId, offsetDateTimeFrom, offsetDateTimeTo, pageable, accessToken)
    ).thenReturn(debtPositionTypeOrgList);

    List<DebtPositionTypeOrgsWithSpontaneousDTO> expectedResult =
      podamFactory.manufacturePojo(List.class, DebtPositionTypeOrgsWithSpontaneousDTO.class);

    Mockito.when(
      debtPositionTypeOrgsListWithSpontaneousDTOMapperMock
        .map(debtPositionTypeOrgList)
    ).thenReturn(expectedResult);

    // when
    List<DebtPositionTypeOrgsWithSpontaneousDTO> result =
      debtPositionTypeOrgRetrieverService
        .getMostUsedSpontaneousDebtPositionTypeOrgs(
          brokerId, organizationId, offsetDateTimeFrom, offsetDateTimeTo, pageable, accessToken);

    // then
    assertNotNull(result);
    assertEquals(expectedResult, result);
  }

  @Test
  void whenGetDebtPositionTypeOrgCodeThenOk(){
    String accessToken = "accessToken";
    Long debtPositionTypeOrgId = 1L;
    Long organizationId = 2L;
    DebtPositionTypeOrg debtPositionTypeOrg = podamFactory.manufacturePojo(DebtPositionTypeOrg.class);
    debtPositionTypeOrg.setOrganizationId(organizationId);

    Mockito.when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrg(debtPositionTypeOrgId, accessToken)).thenReturn(debtPositionTypeOrg);

    String result = debtPositionTypeOrgRetrieverService.getDebtPositionTypeOrgCode(debtPositionTypeOrgId, organizationId, accessToken);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(debtPositionTypeOrg.getCode(),result);
  }

  @Test
  void givenNoDebtPositionTypeOrgWhenGetDebtPositionTypeOrgCodeThenResourceNotFoundException(){
    String accessToken = "accessToken";
    Long debtPositionTypeOrgId = 1L;
    Long organizationId = 2L;

    Mockito.when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrg(debtPositionTypeOrgId, accessToken))
      .thenReturn(null);

    ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class, () -> debtPositionTypeOrgRetrieverService.getDebtPositionTypeOrgCode(debtPositionTypeOrgId, organizationId, accessToken));

    Assertions.assertEquals("DEBT_POSITION_TYPE_ORG_NOT_FOUND",resourceNotFoundException.getCode());
  }

  @Test
  void givenNoMatchingOrganizationIdWhenGetDebtPositionTypeOrgCodeThenResourceNotFoundException(){
    String accessToken = "accessToken";
    Long debtPositionTypeOrgId = 1L;
    Long organizationId = 2L;
    DebtPositionTypeOrg debtPositionTypeOrg = podamFactory.manufacturePojo(DebtPositionTypeOrg.class);
    debtPositionTypeOrg.setOrganizationId(organizationId+1);

    Mockito.when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrg(debtPositionTypeOrgId, accessToken))
      .thenReturn(debtPositionTypeOrg);

    ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class,
      () -> debtPositionTypeOrgRetrieverService.getDebtPositionTypeOrgCode(debtPositionTypeOrgId, organizationId, accessToken));

    Assertions.assertEquals("DEBT_POSITION_TYPE_ORG_NOT_FOUND",resourceNotFoundException.getCode());
  }
}
