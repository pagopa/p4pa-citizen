package it.gov.pagopa.pu.citizen.controller;

import it.gov.pagopa.pu.auth.dto.generated.UserInfo;
import it.gov.pagopa.pu.citizen.dto.generated.DebtorUnpaidDebtPositionInstallmentsDTO;
import it.gov.pagopa.pu.citizen.security.SecurityUtilsTest;
import it.gov.pagopa.pu.citizen.service.installment.InstallmentRetrieverService;
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
class InstallmentsControllerTest {

  @Mock
  private InstallmentRetrieverService installmentRetrieverServiceMock;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();
  private final String accessToken = "fakeAccessToken";
  private UserInfo loggedUser;

  InstallmentsController installmentsController;

  @BeforeEach
  void setUp() {
    loggedUser = podamFactory.manufacturePojo(UserInfo.class);
    SecurityUtilsTest.configureSecurityContext(accessToken, loggedUser);
    installmentsController = new InstallmentsController(installmentRetrieverServiceMock);
  }

  @AfterEach
  void verifyMocks() {
    Mockito.verifyNoMoreInteractions(installmentRetrieverServiceMock);
  }

  @Test
  void givenValidParametersWhenGetDebtorUnpaidDebtPositionInstallmentsThenReturnOk() {
    // given
    Long brokerId = loggedUser.getBrokerId();
    Long debtPositionId = 10L;
    Long paymentOptionId = 20L;
    Long organizationId = 99L;
    String fiscalCode = "ABCDEF12G34H567I";

    List<DebtorUnpaidDebtPositionInstallmentsDTO> expectedResult =
      podamFactory.manufacturePojo(List.class, DebtorUnpaidDebtPositionInstallmentsDTO.class);

    Mockito.when(installmentRetrieverServiceMock.getDebtorInstallmentNoPII(
      brokerId,
      debtPositionId,
      paymentOptionId,
      fiscalCode,
      organizationId,
      accessToken
    )).thenReturn(expectedResult);

    // when
    ResponseEntity<List<DebtorUnpaidDebtPositionInstallmentsDTO>> result =
      installmentsController.getDebtorUnpaidDebtPositionInstallments(
        brokerId,
        debtPositionId,
        paymentOptionId,
        fiscalCode,
        organizationId
      );

    // then
    assertNotNull(result);
    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertEquals(expectedResult, result.getBody());
  }
}
