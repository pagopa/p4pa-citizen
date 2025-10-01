package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.citizen.connector.debtpositions.client.DebtPositionTypeOrgWithActiveSpontaneousCountSearchClient;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.CollectionModelDebtPositionTypeOrgWithActiveSpontaneousCount;
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

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  DebtPositionTypeOrgService debtPositionTypeOrgService;

  @BeforeEach
  void setUp() {
    debtPositionTypeOrgService = new DebtPositionTypeOrgServiceImpl(debtPositionTypeOrgWithActiveSpontaneousCountSearchClientMock);
  }

  @AfterEach
  void mockitoVerify(){
    Mockito.verifyNoMoreInteractions(debtPositionTypeOrgWithActiveSpontaneousCountSearchClientMock);
  }

  @Test
  void givenOrganizationIdsWhenGetDebtPositionTypeOrgWithActiveSpontaneousCountThenReturnCollectionModelDebtPositionTypeOrgWithActiveSpontaneousCount() {
    //given
    String accessToken = "ACCESS_TOKEN";
    List<Long> organizationsIds = List.of(1L, 2L);
    CollectionModelDebtPositionTypeOrgWithActiveSpontaneousCount expectedResult = podamFactory.manufacturePojo(CollectionModelDebtPositionTypeOrgWithActiveSpontaneousCount.class);

    Mockito.when(debtPositionTypeOrgWithActiveSpontaneousCountSearchClientMock.getDebtPositionTypeOrgWithActiveSpontaneousCount(organizationsIds, accessToken)).thenReturn(expectedResult);
    //when
    CollectionModelDebtPositionTypeOrgWithActiveSpontaneousCount result = debtPositionTypeOrgService.getDebtPositionTypeOrgWithActiveSpontaneousCount(organizationsIds, accessToken);
    //then
    Assertions.assertNotNull(result);
    Assertions.assertEquals(expectedResult, result);
  }
}
