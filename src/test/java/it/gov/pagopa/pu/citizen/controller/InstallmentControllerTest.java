package it.gov.pagopa.pu.citizen.controller;

import it.gov.pagopa.pu.auth.dto.generated.UserInfo;
import it.gov.pagopa.pu.citizen.dto.InstallmentDebtorExtendedDTO;
import it.gov.pagopa.pu.citizen.security.SecurityUtilsTest;
import it.gov.pagopa.pu.citizen.service.installment.InstallmentFacadeService;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class InstallmentControllerTest {

  @Mock
  private InstallmentFacadeService installmentFacadeServiceMock;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();
  private final String accessToken = "fakeAccessToken";
  private final UserInfo loggedUser = podamFactory.manufacturePojo(UserInfo.class);

  private InstallmentController installmentController;

  @BeforeEach
  void setUp() {
    installmentController = new InstallmentController(installmentFacadeServiceMock);
  }

  @AfterEach
  void mockitoVerify() {
    SecurityUtilsTest.configureSecurityContext(accessToken, loggedUser);
    Mockito.verifyNoMoreInteractions(installmentFacadeServiceMock);
  }

  @Test
  void givenFiltersWhenGetPagedDebtorReceiptsThenOk() {
    Long brokerId = 1L;
    String iuvOrNav = "iuvOrNav";
    String debtorFiscalCode = "debtorFiscalCode";
    String orgFiscalCode = "orgFiscalCode";
    List<InstallmentDebtorExtendedDTO> expectedResult = podamFactory.manufacturePojo(List.class,InstallmentDebtorExtendedDTO.class);

    Mockito.when(installmentFacadeServiceMock.getInstallmentByIuvOrNav(brokerId,iuvOrNav,debtorFiscalCode,orgFiscalCode, accessToken)).thenReturn(expectedResult);

    ResponseEntity<List<InstallmentDebtorExtendedDTO>> result = installmentController.getInstallmentsByIuvOrNav(brokerId, iuvOrNav, debtorFiscalCode, orgFiscalCode);

    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertNotNull(result);
    assertEquals(expectedResult, result.getBody());
  }
}
