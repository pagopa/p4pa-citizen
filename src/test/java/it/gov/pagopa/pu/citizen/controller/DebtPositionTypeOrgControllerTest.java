package it.gov.pagopa.pu.citizen.controller;

import it.gov.pagopa.pu.auth.dto.generated.UserInfo;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDetailsDTO;
import it.gov.pagopa.pu.citizen.security.SecurityUtilsTest;
import it.gov.pagopa.pu.citizen.service.debtpositiontypeorg.DebtPositionTypeOrgRetrieverService;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DebtPositionTypeOrgControllerTest {

  @Mock
  private DebtPositionTypeOrgRetrieverService debtPositionTypeOrgRetrieverServiceMock;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();
  private final String accessToken = "fakeAccessToken";
  private final UserInfo loggedUser = podamFactory.manufacturePojo(UserInfo.class);

  DebtPositionTypeOrgController debtPositionTypeOrgController;

  @BeforeEach
  void setUp() {
    SecurityUtilsTest.configureSecurityContext(accessToken, loggedUser);
    debtPositionTypeOrgController = new DebtPositionTypeOrgController(debtPositionTypeOrgRetrieverServiceMock);
  }

  @AfterEach
  void mockitoVerify(){
    Mockito.verifyNoMoreInteractions(debtPositionTypeOrgRetrieverServiceMock);
  }

  @Test
  void givenOrganizationIdWhenGetDebtPositionTypeOrgsWithSpontaneousThenOk() {
    //given
    Long brokerId = 1L;
    Long organizationId = 3L;
    List<DebtPositionTypeOrgsWithSpontaneousDTO> expectedResult = podamFactory.manufacturePojo(List.class, DebtPositionTypeOrgsWithSpontaneousDTO.class);

    Mockito.when(debtPositionTypeOrgRetrieverServiceMock.getDebtPositionTypeOrgsWithSpontaneous(brokerId,organizationId, accessToken)).thenReturn(expectedResult);
    //when
    ResponseEntity<List<DebtPositionTypeOrgsWithSpontaneousDTO>> result = debtPositionTypeOrgController.getDebtPositionTypeOrgsWithSpontaneous(brokerId, organizationId);
    //then
    assertNotNull(result);
    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertEquals(expectedResult, result.getBody());
  }

  @Test
  void givenDebtPositionTypeOrgIdWhenGetDebtPositionTypeOrgsWithSpontaneousDetailThenOk() {
    //given
    Long debtPositionTypeOrgId = 3L;
    Long organizationId = 3L;

    DebtPositionTypeOrgsWithSpontaneousDetailsDTO expectedResult = podamFactory.manufacturePojo(DebtPositionTypeOrgsWithSpontaneousDetailsDTO.class);

    Mockito.when(debtPositionTypeOrgRetrieverServiceMock.getDebtPositionTypeOrgsWithSpontaneousDetailsDTO(loggedUser.getBrokerId(), organizationId, debtPositionTypeOrgId, accessToken)).thenReturn(expectedResult);
    //when
    ResponseEntity<DebtPositionTypeOrgsWithSpontaneousDetailsDTO> result = debtPositionTypeOrgController.getDebtPositionTypeOrgsWithSpontaneousDetail(loggedUser.getBrokerId(), organizationId, debtPositionTypeOrgId);
    //then
    assertNotNull(result);
    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertEquals(expectedResult, result.getBody());
  }

  @Test
  void givenBrokerIdAndOrganizationIdWhenGetCurrentYearTopTenDebtPositionTypeOrgsWithSpontaneousThenOk() {
    // given
    Long brokerId = 1L;
    Long organizationId = 3L;

    List<DebtPositionTypeOrgsWithSpontaneousDTO> expectedResult =
      podamFactory.manufacturePojo(List.class, DebtPositionTypeOrgsWithSpontaneousDTO.class);

    Mockito.when(
      debtPositionTypeOrgRetrieverServiceMock
        .getCurrentYearTopTenDebtPositionTypeOrgsWithSpontaneous(
          brokerId, organizationId, accessToken)
    ).thenReturn(expectedResult);

    // when
    ResponseEntity<List<DebtPositionTypeOrgsWithSpontaneousDTO>> result =
      debtPositionTypeOrgController
        .getCurrentYearTopTenDebtPositionTypeOrgsWithSpontaneous(
          brokerId, organizationId);

    // then
    assertNotNull(result);
    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertEquals(expectedResult, result.getBody());
  }

}
