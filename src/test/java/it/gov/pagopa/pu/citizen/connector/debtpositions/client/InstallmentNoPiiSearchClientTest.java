package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.controller.generated.InstallmentNoPiiSearchControllerApi;
import it.gov.pagopa.pu.debtpositions.dto.generated.CollectionModelInstallmentNoPII;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentNoPII;
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
class InstallmentNoPiiSearchClientTest {

  @Mock
  private DebtPositionsApisHolder debtPositionsApisHolderMock;
  @Mock
  private InstallmentNoPiiSearchControllerApi installmentNoPiiSearchControllerApiMock;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  InstallmentNoPiiSearchClient installmentNoPiiSearchClient;

  @BeforeEach
  void setUp() {
    installmentNoPiiSearchClient = new InstallmentNoPiiSearchClient(debtPositionsApisHolderMock);
  }

  @AfterEach
  void mockitoVerify() {
    Mockito.verifyNoMoreInteractions(
      debtPositionsApisHolderMock,
      installmentNoPiiSearchControllerApiMock
    );
  }

  @Test
  void givenDebtPositionIdWhenGetInstallmentsThenReturnInstallmentList() {
    // given
    String accessToken = "ACCESS_TOKEN";
    Long debtPositionId = 123L;
    String installmentStatuses = "PAID,UNPAID";

    CollectionModelInstallmentNoPII expectedResult =
      podamFactory.manufacturePojo(CollectionModelInstallmentNoPII.class);

    Mockito.when(debtPositionsApisHolderMock.getInstallmentNoPiiSearchControllerApi(accessToken))
      .thenReturn(installmentNoPiiSearchControllerApiMock);

    Mockito.when(installmentNoPiiSearchControllerApiMock
        .crudInstallmentsFindByDebtPositionIdAndStatuses(
          debtPositionId.toString(),
          installmentStatuses
        )
      )
      .thenReturn(expectedResult);

    // when
    List<InstallmentNoPII> result =
      installmentNoPiiSearchClient.getInstallments(debtPositionId, installmentStatuses, accessToken);

    // then
    Assertions.assertNotNull(result);
    Assertions.assertEquals(
      expectedResult.getEmbedded().getInstallmentNoPIIs(),
      result
    );
  }

  @Test
  void givenNullCollectionWhenGetInstallmentsThenReturnEmptyList() {
    String accessToken = "ACCESS_TOKEN";
    Long debtPositionId = 123L;
    String installmentStatuses = "PAID";

    Mockito.when(debtPositionsApisHolderMock.getInstallmentNoPiiSearchControllerApi(accessToken))
      .thenReturn(installmentNoPiiSearchControllerApiMock);

    Mockito.when(installmentNoPiiSearchControllerApiMock
        .crudInstallmentsFindByDebtPositionIdAndStatuses(
          debtPositionId.toString(),
          installmentStatuses
        )
      )
      .thenReturn(null);

    List<InstallmentNoPII> result =
      installmentNoPiiSearchClient.getInstallments(debtPositionId, installmentStatuses, accessToken);

    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  void givenNullEmbeddedWhenGetInstallmentsThenReturnEmptyList() {
    String accessToken = "ACCESS_TOKEN";
    Long debtPositionId = 123L;
    String installmentStatuses = "PAID";

    CollectionModelInstallmentNoPII collection =
      podamFactory.manufacturePojo(CollectionModelInstallmentNoPII.class);
    collection.setEmbedded(null);

    Mockito.when(debtPositionsApisHolderMock.getInstallmentNoPiiSearchControllerApi(accessToken))
      .thenReturn(installmentNoPiiSearchControllerApiMock);

    Mockito.when(installmentNoPiiSearchControllerApiMock
        .crudInstallmentsFindByDebtPositionIdAndStatuses(
          debtPositionId.toString(),
          installmentStatuses
        )
      )
      .thenReturn(collection);

    List<InstallmentNoPII> result =
      installmentNoPiiSearchClient.getInstallments(debtPositionId, installmentStatuses, accessToken);

    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isEmpty());
  }
}
