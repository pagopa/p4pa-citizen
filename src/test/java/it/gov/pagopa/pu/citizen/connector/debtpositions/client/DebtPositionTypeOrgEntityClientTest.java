package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import it.gov.pagopa.pu.debtpositions.controller.generated.DebtPositionTypeOrgEntityControllerApi;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionTypeOrg;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DebtPositionTypeOrgEntityClientTest {

  @Mock
  private DebtPositionTypeOrgEntityControllerApi debtPositionTypeOrgEntityControllerApiMock;
  @Mock
  private DebtPositionsApisHolder debtPositionsApisHolderMock;

  DebtPositionTypeOrgEntityClient debtPositionTypeOrgEntityClient;

  @BeforeEach
  void setUp() {
    debtPositionTypeOrgEntityClient = new DebtPositionTypeOrgEntityClient(debtPositionsApisHolderMock);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
            debtPositionsApisHolderMock,
            debtPositionTypeOrgEntityControllerApiMock
    );
  }

  @Test
  void givenExistingDebtPositionTypeOrgWhenGetDebtPositionTypeOrgThenInvokeWithAccessToken() {
    Long debtPositionTypeOrgId = 1L;
    String accessToken = "ACCESSTOKEN";
    DebtPositionTypeOrg expectedResult = new DebtPositionTypeOrg();

    when(debtPositionsApisHolderMock.getDebtPositionTypeOrgEntityControllerApi(accessToken))
            .thenReturn(debtPositionTypeOrgEntityControllerApiMock);

    when(debtPositionTypeOrgEntityControllerApiMock.crudGetDebtpositiontypeorg(
            String.valueOf(debtPositionTypeOrgId)))
            .thenReturn(expectedResult);

    DebtPositionTypeOrg result = debtPositionTypeOrgEntityClient.getDebtPositionTypeOrg(debtPositionTypeOrgId, accessToken);

    assertSame(expectedResult, result);
  }

  @Test
  void givenNoDebtPositionTypeOrgWhenGetDebtPositionTypeOrgThenReturnNull() {
    Long debtPositionTypeOrgId = 1L;
    String accessToken = "ACCESSTOKEN";

    when(debtPositionsApisHolderMock.getDebtPositionTypeOrgEntityControllerApi(accessToken))
            .thenReturn(debtPositionTypeOrgEntityControllerApiMock);
    when(debtPositionTypeOrgEntityControllerApiMock.crudGetDebtpositiontypeorg(
            String.valueOf(debtPositionTypeOrgId)))
            .thenThrow(
                    HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

    DebtPositionTypeOrg result = debtPositionTypeOrgEntityClient.getDebtPositionTypeOrg(debtPositionTypeOrgId, accessToken);

    assertNull(result);
  }
}
