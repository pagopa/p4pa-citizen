package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.controller.generated.DebtPositionTypeOrgWithActiveSpontaneousCountSearchControllerApi;
import it.gov.pagopa.pu.debtpositions.dto.generated.CollectionModelDebtPositionTypeOrgWithActiveSpontaneousCount;
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
class DebtPositionTypeOrgWithActiveSpontaneousCountSearchClientTest {

  @Mock
  private DebtPositionsApisHolder debtPositionsApisHolderMock;
  @Mock
  private DebtPositionTypeOrgWithActiveSpontaneousCountSearchControllerApi debtPositionTypeOrgWithActiveSpontaneousCountSearchControllerApiMock;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  DebtPositionTypeOrgWithActiveSpontaneousCountSearchClient debtPositionTypeOrgWithActiveSpontaneousCountSearchClient;

  @BeforeEach
  void setUp() {
    debtPositionTypeOrgWithActiveSpontaneousCountSearchClient = new DebtPositionTypeOrgWithActiveSpontaneousCountSearchClient(debtPositionsApisHolderMock);
  }

  @AfterEach
  void mockitoVerify(){
    Mockito.verifyNoMoreInteractions(debtPositionsApisHolderMock, debtPositionTypeOrgWithActiveSpontaneousCountSearchControllerApiMock);
  }

  @Test
  void givenOrganizationIdsWhenGetDebtPositionTypeOrgWithActiveSpontaneousCountThenReturnDebtPositionTypeOrgWithActiveSpontaneousCountList() {
    //given
    String accessToken = "ACCESS_TOKEN";
    List<Long> organizationsIds = List.of(1L, 2L);
    CollectionModelDebtPositionTypeOrgWithActiveSpontaneousCount expectedResult = podamFactory.manufacturePojo(CollectionModelDebtPositionTypeOrgWithActiveSpontaneousCount.class);

    Mockito.when(debtPositionsApisHolderMock.getDebtPositionTypeOrgWithActiveSpontaneousCountSearchControllerApi(accessToken)).thenReturn(debtPositionTypeOrgWithActiveSpontaneousCountSearchControllerApiMock);
    Mockito.when(debtPositionTypeOrgWithActiveSpontaneousCountSearchControllerApiMock.crudDebtPositionTypeOrgWithActiveSpontaneousCountCountByOrganizationIds(organizationsIds)).thenReturn(expectedResult);
    //when
    List<DebtPositionTypeOrgWithActiveSpontaneousCount> result = debtPositionTypeOrgWithActiveSpontaneousCountSearchClient.getDebtPositionTypeOrgWithActiveSpontaneousCount(organizationsIds, accessToken);
    //then
    Assertions.assertNotNull(result);
    Assertions.assertEquals(expectedResult.getEmbedded().getDebtPositionTypeOrgWithActiveSpontaneousCounts(), result);
  }

  @Test
  void givenNullCollectionWhenGetDebtPositionTypeOrgWithActiveSpontaneousCountThenReturnEmptyList() {
    String accessToken = "ACCESS_TOKEN";
    List<Long> organizationsIds = List.of(1L, 2L);

    Mockito.when(debtPositionsApisHolderMock.getDebtPositionTypeOrgWithActiveSpontaneousCountSearchControllerApi(accessToken))
      .thenReturn(debtPositionTypeOrgWithActiveSpontaneousCountSearchControllerApiMock);
    Mockito.when(debtPositionTypeOrgWithActiveSpontaneousCountSearchControllerApiMock
        .crudDebtPositionTypeOrgWithActiveSpontaneousCountCountByOrganizationIds(organizationsIds))
      .thenReturn(null);

    List<DebtPositionTypeOrgWithActiveSpontaneousCount> result =
      debtPositionTypeOrgWithActiveSpontaneousCountSearchClient
        .getDebtPositionTypeOrgWithActiveSpontaneousCount(organizationsIds, accessToken);

    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  void givenNullEmbeddedWhenGetDebtPositionTypeOrgWithActiveSpontaneousCountThenReturnEmptyList() {
    String accessToken = "ACCESS_TOKEN";
    List<Long> organizationsIds = List.of(1L, 2L);

    CollectionModelDebtPositionTypeOrgWithActiveSpontaneousCount collection = podamFactory.manufacturePojo(CollectionModelDebtPositionTypeOrgWithActiveSpontaneousCount.class);
    collection.setEmbedded(null);

    Mockito.when(debtPositionsApisHolderMock.getDebtPositionTypeOrgWithActiveSpontaneousCountSearchControllerApi(accessToken))
      .thenReturn(debtPositionTypeOrgWithActiveSpontaneousCountSearchControllerApiMock);
    Mockito.when(debtPositionTypeOrgWithActiveSpontaneousCountSearchControllerApiMock
        .crudDebtPositionTypeOrgWithActiveSpontaneousCountCountByOrganizationIds(organizationsIds))
      .thenReturn(collection);

    List<DebtPositionTypeOrgWithActiveSpontaneousCount> result =
      debtPositionTypeOrgWithActiveSpontaneousCountSearchClient
        .getDebtPositionTypeOrgWithActiveSpontaneousCount(organizationsIds, accessToken);

    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isEmpty());
  }

}
