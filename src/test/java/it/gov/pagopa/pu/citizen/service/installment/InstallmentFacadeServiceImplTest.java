package it.gov.pagopa.pu.citizen.service.installment;

import it.gov.pagopa.pu.citizen.connector.debtpositions.InstallmentService;
import it.gov.pagopa.pu.citizen.dto.InstallmentDebtorExtendedDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtorUnpaidDebtPositionInstallmentsDTO;
import it.gov.pagopa.pu.citizen.exception.InvalidParamException;
import it.gov.pagopa.pu.citizen.mapper.DebtorUnpaidDebtPositionInstallmentsMapper;
import it.gov.pagopa.pu.citizen.mapper.InstallmentDebtorExtendedDTOMapper;
import it.gov.pagopa.pu.citizen.service.organization.OrganizationRetrieverService;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentDebtorDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentNoPII;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentStatus;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class InstallmentFacadeServiceImplTest {
  @Mock
  private OrganizationRetrieverService organizationRetrieverServiceMock;
  @Mock
  private InstallmentService installmentServiceMock;
  @Mock
  private InstallmentDebtorExtendedDTOMapper installmentDebtorExtendedDTOMapperMock;
  @Mock
  private DebtorUnpaidDebtPositionInstallmentsMapper debtorUnpaidDebtPositionInstallmentsMapperMock;
  private InstallmentFacadeService installmentFacadeService;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  @BeforeEach
  void setUp() {
    installmentFacadeService = new InstallmentFacadeServiceImpl(organizationRetrieverServiceMock,installmentServiceMock,installmentDebtorExtendedDTOMapperMock, debtorUnpaidDebtPositionInstallmentsMapperMock);
  }

  @AfterEach
  void mockitoVerify(){
    Mockito.verifyNoMoreInteractions(organizationRetrieverServiceMock,installmentServiceMock,installmentDebtorExtendedDTOMapperMock, debtorUnpaidDebtPositionInstallmentsMapperMock);
  }

  @Test
  void whenGetInstallmentsByIuvOrNavThenOk() {
    String accessToken = "accessToken";
    Long brokerId = 1L;
    String iuvOrNav = "iuvOrNav";
    String debtorFiscalCode = "debtorFiscalCode";
    String orgFiscalCode = "orgFiscalCode";
    Organization organization = podamFactory.manufacturePojo(Organization.class);
    organization.setOrgFiscalCode(orgFiscalCode);
    List<InstallmentStatus> statuses = List.of(InstallmentStatus.PAID, InstallmentStatus.REPORTED);
    List<InstallmentDebtorDTO> installments = podamFactory.manufacturePojo(List.class,InstallmentDebtorDTO.class);
    installments.getFirst().setStatus(InstallmentStatus.PAID);

    Map<Long, Organization> organizationMap = buildOrganizationMap(installments, organization);
    List<InstallmentDebtorExtendedDTO> expectedResult = podamFactory.manufacturePojo(List.class,InstallmentDebtorExtendedDTO.class);

    Mockito.when(organizationRetrieverServiceMock.getValidOrganization(orgFiscalCode,brokerId,accessToken)).thenReturn(organization);
    Mockito.when(installmentServiceMock.getInstallmentByIuvOrNav(iuvOrNav,debtorFiscalCode,organization.getOrganizationId(), statuses, accessToken)).thenReturn(installments);
    Mockito.when(organizationRetrieverServiceMock.getValidOrganization(Mockito.anyLong(),Mockito.eq(brokerId),Mockito.eq(accessToken)))
      .thenAnswer(invocation -> organizationMap.get(invocation.getArgument(0, Long.class)));
    Mockito.when(installmentDebtorExtendedDTOMapperMock.map(installments,organizationMap)).thenReturn(expectedResult);

    List<InstallmentDebtorExtendedDTO> result = installmentFacadeService.getInstallmentByIuvOrNav(brokerId,iuvOrNav, debtorFiscalCode, orgFiscalCode, statuses, accessToken);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(expectedResult,result);
    Mockito.verify(organizationRetrieverServiceMock, Mockito.times(organizationMap.size()-1)).getValidOrganization(Mockito.anyLong(),Mockito.eq(brokerId),Mockito.eq(accessToken));
  }

  @Test
  void givenNoOrgFiscalCodeWhenGetInstallmentsByIuvOrNavThenOk() {
    String accessToken = "accessToken";
    Long brokerId = 1L;
    String iuvOrNav = "iuvOrNav";
    String debtorFiscalCode = "debtorFiscalCode";
    Organization organization = podamFactory.manufacturePojo(Organization.class);
    List<InstallmentDebtorDTO> installments = podamFactory.manufacturePojo(List.class,InstallmentDebtorDTO.class);
    installments.getFirst().setStatus(InstallmentStatus.UNPAID);
    List<InstallmentStatus> statuses = List.of(InstallmentStatus.UNPAID);
    Map<Long, Organization> organizationMap = buildOrganizationMap(installments, organization);
    List<InstallmentDebtorExtendedDTO> expectedResult = podamFactory.manufacturePojo(List.class,InstallmentDebtorExtendedDTO.class);

    Mockito.when(installmentServiceMock.getInstallmentByIuvOrNav(iuvOrNav,debtorFiscalCode,null, statuses, accessToken)).thenReturn(installments);
    Mockito.when(organizationRetrieverServiceMock.getValidOrganization(Mockito.anyLong(),Mockito.eq(brokerId),Mockito.eq(accessToken)))
      .thenAnswer(invocation -> organizationMap.get(invocation.getArgument(0, Long.class)));
    Mockito.when(installmentDebtorExtendedDTOMapperMock.map(installments,organizationMap)).thenReturn(expectedResult);

    List<InstallmentDebtorExtendedDTO> result = installmentFacadeService.getInstallmentByIuvOrNav(brokerId,iuvOrNav, debtorFiscalCode, null, statuses, accessToken);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(expectedResult,result);
    Mockito.verify(organizationRetrieverServiceMock, Mockito.times(organizationMap.size())).getValidOrganization(Mockito.anyLong(),Mockito.eq(brokerId),Mockito.eq(accessToken));
  }

  private Map<Long, Organization> buildOrganizationMap(List<InstallmentDebtorDTO> installments, Organization organization) {
    installments.getFirst().setOrganizationId(organization.getOrganizationId());
    Map<Long, Organization> organizationMap = new HashMap<>();
    organizationMap.put(organization.getOrganizationId(), organization);
    installments.stream().skip(1).forEach(i->{
      Organization org = podamFactory.manufacturePojo(Organization.class);
      org.setOrganizationId(i.getOrganizationId());
      organizationMap.put(i.getOrganizationId(), org);
    });
    return organizationMap;
  }

  @Test
  void givenNoInstallmentsWhenGetInstallmentsByIuvOrNavThenOk() {
    String accessToken = "accessToken";
    Long brokerId = 1L;
    String iuvOrNav = "iuvOrNav";
    String debtorFiscalCode = "debtorFiscalCode";
    String orgFiscalCode = "orgFiscalCode";
    Organization organization = podamFactory.manufacturePojo(Organization.class);
    List<InstallmentStatus> statuses = new ArrayList<>(List.of(InstallmentStatus.PAID));
    List<InstallmentStatus> expectedStatuses = new ArrayList<>(statuses);
    expectedStatuses.add(InstallmentStatus.REPORTED);

    Mockito.when(organizationRetrieverServiceMock.getValidOrganization(orgFiscalCode,brokerId,accessToken)).thenReturn(organization);
    Mockito.when(installmentServiceMock.getInstallmentByIuvOrNav(iuvOrNav,debtorFiscalCode,organization.getOrganizationId(), expectedStatuses, accessToken)).thenReturn(Collections.emptyList());

    List<InstallmentDebtorExtendedDTO> result = installmentFacadeService.getInstallmentByIuvOrNav(brokerId,iuvOrNav, debtorFiscalCode, orgFiscalCode, statuses, accessToken);

    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isEmpty());
    Mockito.verifyNoInteractions(installmentDebtorExtendedDTOMapperMock);
  }

  @Test
  void givenNoDebtorFiscalCodeAndNoOrgFiscalCodeWhenGetInstallmentsByIuvOrNavThenInvalidParamException() {
    String accessToken = "accessToken";
    Long brokerId = 1L;
    String iuvOrNav = "iuvOrNav";

    Assertions.assertThrows(InvalidParamException.class,() -> installmentFacadeService.getInstallmentByIuvOrNav(brokerId,iuvOrNav, null, null,null, accessToken));

    Mockito.verifyNoInteractions(organizationRetrieverServiceMock,installmentServiceMock,installmentDebtorExtendedDTOMapperMock);
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
    Mockito.when(installmentServiceMock.getDebtorInstallmentNoPII(
        accessToken, debtPositionId, paymentOptionId, fiscalCode, organizationId))
      .thenReturn(installments);

    List<DebtorUnpaidDebtPositionInstallmentsDTO> expected = podamFactory.manufacturePojo(List.class, DebtorUnpaidDebtPositionInstallmentsDTO.class);
    Mockito.when(debtorUnpaidDebtPositionInstallmentsMapperMock.mapDebtorUnpaidDebtPositionInstallmentsList(org, installments, debtPositionId))
      .thenReturn(expected);

    // when
    List<DebtorUnpaidDebtPositionInstallmentsDTO> result =
      installmentFacadeService.getDebtorInstallmentNoPII(
        brokerId, debtPositionId, paymentOptionId, fiscalCode, organizationId, accessToken);

    // then
    assertNotNull(result);
    assertEquals(expected, result);
  }
}
