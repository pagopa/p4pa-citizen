package it.gov.pagopa.pu.citizen.service.installment;

import it.gov.pagopa.pu.citizen.connector.debtpositions.InstallmentNoPIIService;
import it.gov.pagopa.pu.citizen.dto.generated.DebtorUnpaidDebtPositionInstallmentsDTO;
import it.gov.pagopa.pu.citizen.mapper.DebtorUnpaidDebtPositionInstallmentsMapper;
import it.gov.pagopa.pu.citizen.service.organization.OrganizationRetrieverService;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentNoPII;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
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
class InstallmentRetrieverServiceImplTest {

  @Mock
  private InstallmentNoPIIService installmentNoPIIServiceMock;
  @Mock
  private OrganizationRetrieverService organizationRetrieverServiceMock;
  @Mock
  private DebtorUnpaidDebtPositionInstallmentsMapper mapperMock;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  InstallmentRetrieverService installmentRetrieverService;

  @BeforeEach
  void setUp() {
    installmentRetrieverService = new InstallmentRetrieverServiceImpl(
      installmentNoPIIServiceMock,
      organizationRetrieverServiceMock,
      mapperMock
    );
  }

  @AfterEach
  void mockitoVerify() {
    Mockito.verifyNoMoreInteractions(
      installmentNoPIIServiceMock,
      organizationRetrieverServiceMock,
      mapperMock
    );
  }

  @Test
  void givenValidInputsWhenGetDebtorInstallmentNoPIIThenReturnMappedDTOList() {
    // given
    Long brokerId = 10L;
    Long debtPositionId = 20L;
    Long paymentOptionId = 30L;
    Long organizationId = 40L;
    String fiscalCode = "debtorFiscalCode";
    String accessToken = "accessToken";

    Organization org = podamFactory.manufacturePojo(Organization.class);
    Mockito.when(organizationRetrieverServiceMock.getValidOrganization(organizationId, brokerId, accessToken))
      .thenReturn(org);

    List<InstallmentNoPII> installments = podamFactory.manufacturePojo(List.class, InstallmentNoPII.class);
    Mockito.when(installmentNoPIIServiceMock.getDebtorInstallmentNoPII(
        accessToken, debtPositionId, paymentOptionId, fiscalCode, organizationId))
      .thenReturn(installments);

    List<DebtorUnpaidDebtPositionInstallmentsDTO> expected = podamFactory.manufacturePojo(List.class, DebtorUnpaidDebtPositionInstallmentsDTO.class);
    Mockito.when(mapperMock.mapDebtorUnpaidDebtPositionInstallmentsList(org, installments, debtPositionId))
      .thenReturn(expected);

    // when
    List<DebtorUnpaidDebtPositionInstallmentsDTO> result =
      installmentRetrieverService.getDebtorInstallmentNoPII(
        brokerId, debtPositionId, paymentOptionId, fiscalCode, organizationId, accessToken);

    // then
    assertNotNull(result);
    assertEquals(expected, result);

    Mockito.verify(organizationRetrieverServiceMock)
      .getValidOrganization(organizationId, brokerId, accessToken);
    Mockito.verify(installmentNoPIIServiceMock)
      .getDebtorInstallmentNoPII(accessToken, debtPositionId, paymentOptionId, fiscalCode, organizationId);
    Mockito.verify(mapperMock)
      .mapDebtorUnpaidDebtPositionInstallmentsList(org, installments, debtPositionId);
  }

}
