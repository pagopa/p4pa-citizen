package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.citizen.connector.debtpositions.client.DebtPositionTypeOrgEntityClient;
import it.gov.pagopa.pu.citizen.connector.debtpositions.client.DebtPositionTypeOrgSearchClient;
import it.gov.pagopa.pu.citizen.connector.debtpositions.client.DebtPositionTypeOrgWithActiveSpontaneousCountSearchClient;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionTypeOrgWithActiveSpontaneousCount;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class DebtPositionTypeOrgServiceImplTest {

  @Mock
  private DebtPositionTypeOrgWithActiveSpontaneousCountSearchClient debtPositionTypeOrgWithActiveSpontaneousCountSearchClientMock;
  @Mock
  private DebtPositionTypeOrgSearchClient debtPositionTypeOrgSearchClientMock;
  @Mock
  private DebtPositionTypeOrgEntityClient debtPositionTypeOrgEntityClient;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  DebtPositionTypeOrgService debtPositionTypeOrgService;

  @BeforeEach
  void setUp() {
    debtPositionTypeOrgService = new DebtPositionTypeOrgServiceImpl(debtPositionTypeOrgWithActiveSpontaneousCountSearchClientMock, debtPositionTypeOrgSearchClientMock, debtPositionTypeOrgEntityClient);
  }

  @AfterEach
  void mockitoVerify(){
    Mockito.verifyNoMoreInteractions(debtPositionTypeOrgWithActiveSpontaneousCountSearchClientMock, debtPositionTypeOrgSearchClientMock);
  }

  @Test
  void givenOrganizationIdsWhenGetDebtPositionTypeOrgWithActiveSpontaneousCountThenReturnDebtPositionTypeOrgWithActiveSpontaneousCountList() {
    //given
    String accessToken = "ACCESS_TOKEN";
    List<Long> organizationsIds = List.of(1L, 2L);
    List<DebtPositionTypeOrgWithActiveSpontaneousCount> expectedResult = podamFactory.manufacturePojo(List.class, DebtPositionTypeOrgWithActiveSpontaneousCount.class);

    Mockito.when(debtPositionTypeOrgWithActiveSpontaneousCountSearchClientMock.getDebtPositionTypeOrgWithActiveSpontaneousCount(organizationsIds, accessToken)).thenReturn(expectedResult);
    //when
    List<DebtPositionTypeOrgWithActiveSpontaneousCount> result = debtPositionTypeOrgService.getDebtPositionTypeOrgWithActiveSpontaneousCount(organizationsIds, accessToken);
    //then
    Assertions.assertNotNull(result);
    Assertions.assertEquals(expectedResult, result);
  }

  @Test
  void givenOrganizationIdWhenGetDebtPositionTypeOrgsFindActiveDebtPositionTypeOrgThenReturnDebtPositionTypeOrgList() {
    //given
    String accessToken = "ACCESS_TOKEN";
    Long organizationId = 2L;

    List<DebtPositionTypeOrg> expectedResult = podamFactory.manufacturePojo(List.class, DebtPositionTypeOrg.class);

    Mockito.when(debtPositionTypeOrgSearchClientMock.getDebtPositionTypeOrgsFindActiveDebtPositionTypeOrg(organizationId, accessToken)).thenReturn(expectedResult);
    //when
    List<DebtPositionTypeOrg> result = debtPositionTypeOrgService.getDebtPositionTypeOrgsFindActiveDebtPositionTypeOrg(organizationId, accessToken);
    //then

    Assertions.assertNotNull(result);
    Assertions.assertEquals(expectedResult, result);
  }

  @Test
  void givenDebtPositionTypeOrgIdWhenGetDebtPositionTypeOrgThenReturnDebtPositionTypeOrg() {
    //given
    String accessToken = "ACCESS_TOKEN";
    Long debtPositionTypeOrgId = 2L;

    DebtPositionTypeOrg expectedResult = podamFactory.manufacturePojo(DebtPositionTypeOrg.class);
    Mockito.when(debtPositionTypeOrgEntityClient.getDebtPositionTypeOrg(debtPositionTypeOrgId, accessToken)).thenReturn(expectedResult);
    //when
    DebtPositionTypeOrg result = debtPositionTypeOrgService.getDebtPositionTypeOrg(debtPositionTypeOrgId, accessToken);
    //then
    Assertions.assertNotNull(result);
    Assertions.assertEquals(expectedResult, result);
  }
}
