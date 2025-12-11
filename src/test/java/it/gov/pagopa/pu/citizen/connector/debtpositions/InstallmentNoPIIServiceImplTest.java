package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.citizen.connector.debtpositions.client.InstallmentNoPIISearchClient;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
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
class InstallmentNoPIIServiceImplTest {

  @Mock
  private InstallmentNoPIISearchClient installmentNoPIISearchClientMock;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  InstallmentNoPIIService installmentNoPiiService;

  @BeforeEach
  void setUp() {
    installmentNoPiiService = new InstallmentNoPIIServiceImpl(installmentNoPIISearchClientMock);
  }

  @AfterEach
  void mockitoVerify() {
    Mockito.verifyNoMoreInteractions(installmentNoPIISearchClientMock);
  }

  @Test
  void whenGetDebtorInstallmentNoPIIThenInvokeClient() {
    // given
    String accessToken = "ACCESS_TOKEN";
    Long debtPositionId = 10L;
    Long paymentOptionId = 20L;
    String fiscalCode = "ABCDEF12G34H567I";
    Long organizationId = 100L;

    List<InstallmentNoPII> expectedResult = podamFactory.manufacturePojo(List.class, InstallmentNoPII.class);

    Mockito.when(installmentNoPIISearchClientMock.getDebtorInstallmentNoPII(
      accessToken,
      debtPositionId,
      paymentOptionId,
      fiscalCode,
      organizationId
    )).thenReturn(expectedResult);

    // when
    List<InstallmentNoPII> result =
      installmentNoPiiService.getDebtorInstallmentNoPII(
        accessToken,
        debtPositionId,
        paymentOptionId,
        fiscalCode,
        organizationId
      );

    // then
    Assertions.assertNotNull(result);
    Assertions.assertSame(expectedResult, result);
  }
}
