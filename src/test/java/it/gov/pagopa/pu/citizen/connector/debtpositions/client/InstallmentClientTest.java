package it.gov.pagopa.pu.citizen.connector.debtpositions.client;

import it.gov.pagopa.pu.citizen.connector.debtpositions.config.DebtPositionsApisHolder;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.controller.generated.InstallmentApi;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentDebtorDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstallmentClientTest {

  @Mock
  private DebtPositionsApisHolder debtPositionApisHolderMock;
  @Mock
  private InstallmentApi installmentApiMock;
  private InstallmentClient installmentClient;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @BeforeEach
  void setUp() {
    installmentClient = new InstallmentClient(debtPositionApisHolderMock);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      debtPositionApisHolderMock,
      installmentApiMock
    );
  }

  @Test
  void whenGetInstallmentByIuvOrNavThenInvokeWithAccessToken() {
    String accessToken = "ACCESSTOKEN";
    String iuvOrNav = "iuvOrNav";
    String debtorFiscalCode = "debtorFiscalCode";
    Long organizationId = 1L;
    List<InstallmentDebtorDTO> expectedResult = podamFactory.manufacturePojo(List.class,InstallmentDebtorDTO.class);

    when(debtPositionApisHolderMock.getInstallmentApi(accessToken))
      .thenReturn(installmentApiMock);
    when(installmentApiMock.getInstallmentsByIuvOrNav(iuvOrNav,debtorFiscalCode,organizationId))
      .thenReturn(expectedResult);

    List<InstallmentDebtorDTO> result = installmentClient.getInstallmentByIuvOrNav(iuvOrNav, debtorFiscalCode, organizationId, accessToken);

    assertSame(expectedResult, result);
  }
}

