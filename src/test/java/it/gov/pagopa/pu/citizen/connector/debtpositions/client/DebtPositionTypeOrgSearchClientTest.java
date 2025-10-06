package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.controller.generated.DebtPositionTypeOrgSearchControllerApi;
import it.gov.pagopa.pu.debtpositions.dto.generated.CollectionModelDebtPositionTypeOrg;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class DebtPositionTypeOrgSearchClientTest {

  @Mock
  private DebtPositionsApisHolder debtPositionsApisHolderMock;
  @Mock
  private DebtPositionTypeOrgSearchControllerApi debtPositionTypeOrgSearchControllerApiMock;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  DebtPositionTypeOrgSearchClient debtPositionTypeOrgSearchClient;

  @BeforeEach
  void setUp() {
    debtPositionTypeOrgSearchClient = new DebtPositionTypeOrgSearchClient(debtPositionsApisHolderMock);
  }

  @AfterEach
  void mockitoVerify(){
    Mockito.verifyNoMoreInteractions(debtPositionsApisHolderMock, debtPositionTypeOrgSearchControllerApiMock);
  }

  @Test
  void givenOrganizationIdWhenGetDebtPositionTypeOrgsFindActiveDebtPositionTypeOrgThenReturnDebtPositionTypeOrgList() {
    //given
    String accessToken = "ACCESS_TOKEN";
    Long organizationId = 1L;

    CollectionModelDebtPositionTypeOrg expectedResult = podamFactory.manufacturePojo(CollectionModelDebtPositionTypeOrg.class);

    Mockito.when(debtPositionsApisHolderMock.getDebtPositionTypeOrgSearchControllerApi(accessToken)).thenReturn(debtPositionTypeOrgSearchControllerApiMock);
    Mockito.when(debtPositionTypeOrgSearchControllerApiMock.crudDebtPositionTypeOrgsFindActiveDebtPositionTypeOrg(organizationId)).thenReturn(expectedResult);

    //when
    List<DebtPositionTypeOrg> result = debtPositionTypeOrgSearchClient.getDebtPositionTypeOrgsFindActiveDebtPositionTypeOrg(organizationId, accessToken);
    //then

    assertNotNull(result);
    assertEquals(expectedResult.getEmbedded().getDebtPositionTypeOrgs(), result);
  }

}
