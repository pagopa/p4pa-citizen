package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.citizen.connector.debtpositions.client.InstallmentClient;
import it.gov.pagopa.pu.citizen.connector.debtpositions.client.InstallmentNoPIISearchClient;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentDebtorDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentNoPII;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentStatus;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class InstallmentServiceImplTest {

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @Mock
  private InstallmentClient installmentClientMock;
  @Mock
  private InstallmentNoPIISearchClient installmentNoPIISearchClientMock;

  private InstallmentService installmentService;

  @BeforeEach
  void setUp() {
    installmentService = new InstallmentServiceImpl(installmentClientMock, installmentNoPIISearchClientMock);
  }

  @AfterEach
  void mockitoVerify() {
    Mockito.verifyNoMoreInteractions(installmentClientMock, installmentNoPIISearchClientMock);
  }

  @Test
  void givenFiltersWhenGetPagedModelReceiptNoPIIViewThenReturnPagedModelReceiptNoPIIView() {
    String accessToken = "ACCESSTOKEN";
    String iuvOrNav = "iuvOrNav";
    String debtorFiscalCode = "debtorFiscalCode";
    Long organizationId = 1L;
    List<InstallmentStatus> statuses = List.of(InstallmentStatus.PAID);

    List<InstallmentDebtorDTO> expectedResult = podamFactory.manufacturePojo(List.class,InstallmentDebtorDTO.class);
    Mockito.when(installmentClientMock.getInstallmentByIuvOrNav(iuvOrNav,debtorFiscalCode,organizationId, statuses, accessToken)).thenReturn(expectedResult);

    List<InstallmentDebtorDTO> result = installmentService.getInstallmentByIuvOrNav(iuvOrNav,debtorFiscalCode,organizationId, statuses, accessToken);

    assertNotNull(result);
    assertEquals(expectedResult, result);
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
      installmentService.getDebtorInstallmentNoPII(
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
