package it.gov.pagopa.pu.citizen.connector.debtpositions;

import it.gov.pagopa.pu.citizen.connector.debtpositions.client.InstallmentClient;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class InstallmentServiceImplTest {

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @Mock
  private InstallmentClient installmentClientMock;
  private InstallmentService installmentService;

  @BeforeEach
  void setUp() {
    installmentService = new InstallmentServiceImpl(installmentClientMock);
  }

  @AfterEach
  void mockitoVerify() {
    Mockito.verifyNoMoreInteractions(installmentClientMock);
  }

  @Test
  void givenFiltersWhenGetPagedModelReceiptNoPIIViewThenReturnPagedModelReceiptNoPIIView() {
    String accessToken = "ACCESSTOKEN";
    String iuvOrNav = "iuvOrNav";
    String debtorFiscalCode = "debtorFiscalCode";
    Long organizationId = 1L;
    List<InstallmentDebtorDTO> expectedResult = podamFactory.manufacturePojo(List.class,InstallmentDebtorDTO.class);
    Mockito.when(installmentClientMock.getInstallmentByIuvOrNav(iuvOrNav,debtorFiscalCode,organizationId,accessToken)).thenReturn(expectedResult);

    List<InstallmentDebtorDTO> result = installmentService.getInstallmentByIuvOrNav(iuvOrNav,debtorFiscalCode,organizationId, accessToken);

    assertNotNull(result);
    assertEquals(expectedResult, result);
  }
}
