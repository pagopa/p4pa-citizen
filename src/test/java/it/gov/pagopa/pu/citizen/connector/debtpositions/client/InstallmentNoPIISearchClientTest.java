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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InstallmentNoPIISearchClientTest {

  @Mock
  private DebtPositionsApisHolder debtPositionsApisHolderMock;

  @Mock
  private InstallmentNoPiiSearchControllerApi installmentNoPiiSearchControllerApiMock;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  private InstallmentNoPIISearchClient installmentNoPiiSearchClient;

  @BeforeEach
  void setUp() {
    installmentNoPiiSearchClient = new InstallmentNoPIISearchClient(debtPositionsApisHolderMock);
  }

  @AfterEach
  void verifyMocks() {
    Mockito.verifyNoMoreInteractions(debtPositionsApisHolderMock, installmentNoPiiSearchControllerApiMock);
  }

  @Test
  void givenValidParamsWhenGetDebtorInstallmentNoPIIThenReturnList() {
    // given
    String accessToken = "ACCESS_TOKEN";
    Long debtPositionId = 10L;
    Long paymentOptionId = 20L;
    String fiscalCode = "ABCDEF12G34H567I";
    Long organizationId = 1L;

    CollectionModelInstallmentNoPII expected = podamFactory.manufacturePojo(CollectionModelInstallmentNoPII.class);

    Mockito.when(debtPositionsApisHolderMock.getInstallmentNoPiiSearchControllerApi(accessToken))
      .thenReturn(installmentNoPiiSearchControllerApiMock);

    Mockito.when(installmentNoPiiSearchControllerApiMock
        .crudInstallmentsFindDebtorUnpaidOrPaidByDebtPositionIdAndPaymentOptionId(
          debtPositionId, paymentOptionId, fiscalCode, organizationId))
      .thenReturn(expected);

    // when
    List<InstallmentNoPII> result = installmentNoPiiSearchClient.getDebtorInstallmentNoPII(
      accessToken, debtPositionId, paymentOptionId, fiscalCode, organizationId);

    // then
    assertNotNull(result);
    assertEquals(expected.getEmbedded().getInstallmentNoPIIs(), result);
  }

  @Test
  void givenNullCollectionModelWhenGetDebtorInstallmentNoPIIThenReturnEmptyList() {
    // given
    String accessToken = "ACCESS_TOKEN";
    Long debtPositionId = 10L;
    Long paymentOptionId = 20L;
    String fiscalCode = "ABCDEF12G34H567I";
    Long organizationId = 1L;

    Mockito.when(debtPositionsApisHolderMock.getInstallmentNoPiiSearchControllerApi(accessToken))
      .thenReturn(installmentNoPiiSearchControllerApiMock);

    Mockito.when(installmentNoPiiSearchControllerApiMock
        .crudInstallmentsFindDebtorUnpaidOrPaidByDebtPositionIdAndPaymentOptionId(
          debtPositionId, paymentOptionId, fiscalCode, organizationId))
      .thenReturn(null);

    // when
    List<InstallmentNoPII> result = installmentNoPiiSearchClient.getDebtorInstallmentNoPII(
      accessToken, debtPositionId, paymentOptionId, fiscalCode, organizationId);

    // then
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void givenNullEmbeddedWhenGetDebtorInstallmentNoPIIThenReturnEmptyList() {
    // given
    String accessToken = "ACCESS_TOKEN";
    Long debtPositionId = 10L;
    Long paymentOptionId = 20L;
    String fiscalCode = "ABCDEF12G34H567I";
    Long organizationId = 1L;

    CollectionModelInstallmentNoPII collection = podamFactory.manufacturePojo(CollectionModelInstallmentNoPII.class);
    collection.setEmbedded(null);

    Mockito.when(debtPositionsApisHolderMock.getInstallmentNoPiiSearchControllerApi(accessToken))
      .thenReturn(installmentNoPiiSearchControllerApiMock);

    Mockito.when(installmentNoPiiSearchControllerApiMock
        .crudInstallmentsFindDebtorUnpaidOrPaidByDebtPositionIdAndPaymentOptionId(
          debtPositionId, paymentOptionId, fiscalCode, organizationId))
      .thenReturn(collection);

    // when
    List<InstallmentNoPII> result = installmentNoPiiSearchClient.getDebtorInstallmentNoPII(
      accessToken, debtPositionId, paymentOptionId, fiscalCode, organizationId);

    // then
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  void givenNullCollectionModelInstallmentNoPIIWhenGetDebtorInstallmentNoPIIThenReturnEmptyList() {
    // given
    String accessToken = "ACCESS_TOKEN";
    Long debtPositionId = 10L;
    Long paymentOptionId = 20L;
    String fiscalCode = "ABCDEF12G34H567I";
    Long organizationId = 1L;

    Mockito.when(debtPositionsApisHolderMock.getInstallmentNoPiiSearchControllerApi(accessToken))
      .thenReturn(installmentNoPiiSearchControllerApiMock);

    Mockito.when(installmentNoPiiSearchControllerApiMock
        .crudInstallmentsFindDebtorUnpaidOrPaidByDebtPositionIdAndPaymentOptionId(
          debtPositionId, paymentOptionId, fiscalCode, organizationId))
      .thenReturn(null);

    // when
    List<InstallmentNoPII> result = installmentNoPiiSearchClient.getDebtorInstallmentNoPII(
      accessToken, debtPositionId, paymentOptionId, fiscalCode, organizationId);

    // then
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isEmpty());
  }
}
