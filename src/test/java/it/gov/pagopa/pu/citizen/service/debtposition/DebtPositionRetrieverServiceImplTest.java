package it.gov.pagopa.pu.citizen.service.debtposition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.gov.pagopa.pu.auth.dto.generated.UserInfo;
import it.gov.pagopa.pu.citizen.connector.debtpositions.DebtPositionService;
import it.gov.pagopa.pu.citizen.connector.organization.OrganizationService;
import it.gov.pagopa.pu.citizen.connector.pagopapayments.PrintPaymentNoticeService;
import it.gov.pagopa.pu.citizen.dto.FileResourceDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionResponseDTO;
import it.gov.pagopa.pu.citizen.exception.ResourceNotFoundException;
import it.gov.pagopa.pu.citizen.mapper.DebtPositionDTOMapper;
import it.gov.pagopa.pu.citizen.mapper.DebtPositionResponseDTOMapper;
import it.gov.pagopa.pu.citizen.service.ZipFileService;
import it.gov.pagopa.pu.citizen.service.organization.OrganizationRetrieverService;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.InstallmentStatus;
import it.gov.pagopa.pu.debtpositions.dto.generated.PaymentOptionDTO;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.security.authorization.AuthorizationDeniedException;
import uk.co.jemos.podam.api.PodamFactory;

@ExtendWith(MockitoExtension.class)
class DebtPositionRetrieverServiceImplTest {

  @Mock
  private DebtPositionService debtPositionServiceMock;
  @Mock
  private DebtPositionDTOMapper debtPositionDTOMapperMock;
  @Mock
  private OrganizationService organizationServiceMock;
  @Mock
  private DebtPositionResponseDTOMapper debtPositionResponseDTOMapperMock;
  @Mock
  private PrintPaymentNoticeService printPaymentNoticeServiceMock;
  @Mock
  private ZipFileService zipFileServiceMock;
  @Mock
  private OrganizationRetrieverService organizationRetrieverServiceMock;

  private DebtPositionRetrieverService debtPositionRetrieverService;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  private final String accessToken = "TOKEN";

  @BeforeEach
  void setUp() {
    debtPositionRetrieverService = new DebtPositionRetrieverServiceImpl(debtPositionServiceMock, debtPositionDTOMapperMock, 1, organizationServiceMock,
        debtPositionResponseDTOMapperMock, printPaymentNoticeServiceMock, zipFileServiceMock, organizationRetrieverServiceMock);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      debtPositionServiceMock,
      debtPositionDTOMapperMock,
      organizationServiceMock,
      debtPositionResponseDTOMapperMock,
      printPaymentNoticeServiceMock,
      zipFileServiceMock,
      organizationRetrieverServiceMock
    );
  }

  @Test
  void givenValidDebtPositionRequestDTOWhenCreateDebtPositionThenOk() {
    DebtPositionRequestDTO requestDTO = podamFactory.manufacturePojo(DebtPositionRequestDTO.class);
    UserInfo loggedUser = new UserInfo();
    loggedUser.setMappedExternalUserId("mappedExternalUserId");
    DebtPositionDTO debtPosition = podamFactory.manufacturePojo(DebtPositionDTO.class);
    Organization organization = podamFactory.manufacturePojo(Organization.class);
    DebtPositionResponseDTO expectedResult = podamFactory.manufacturePojo(DebtPositionResponseDTO.class);

    Mockito.when(debtPositionDTOMapperMock.mapSpontaneousDebtPositionDTO(requestDTO, 1)).thenReturn(debtPosition);
    Mockito.when(debtPositionServiceMock.createDebtPosition(debtPosition, false, accessToken))
        .thenReturn(debtPosition);
    Mockito.when(organizationServiceMock.getOrganizationByOrganizationId(requestDTO.getOrganizationId(), accessToken)).thenReturn(organization);
    Mockito.when(debtPositionResponseDTOMapperMock.map(debtPosition, organization)).thenReturn(expectedResult);

    DebtPositionResponseDTO result = debtPositionRetrieverService.createSpontaneousDebtPosition(requestDTO, accessToken);

    assertNotNull(result);
    assertSame(expectedResult, result);
  }

  @Test
  void givenInvalidOrganizationIdWhenCreateDebtPositionThenThrowException() {
    DebtPositionRequestDTO requestDTO = podamFactory.manufacturePojo(DebtPositionRequestDTO.class);
    UserInfo loggedUser = new UserInfo();
    loggedUser.setMappedExternalUserId("mappedExternalUserId");
    DebtPositionDTO debtPosition = podamFactory.manufacturePojo(DebtPositionDTO.class);

    Mockito.when(debtPositionDTOMapperMock.mapSpontaneousDebtPositionDTO(requestDTO, 1)).thenReturn(debtPosition);
    Mockito.when(debtPositionServiceMock.createDebtPosition(debtPosition, false, accessToken))
      .thenReturn(debtPosition);
    Mockito.when(organizationServiceMock.getOrganizationByOrganizationId(requestDTO.getOrganizationId(), accessToken)).thenReturn(null);

    ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> debtPositionRetrieverService.createSpontaneousDebtPosition(requestDTO, accessToken));
    assertEquals("Organization with id %d not found".formatted(requestDTO.getOrganizationId()), ex.getMessage());
    Mockito.verifyNoInteractions(debtPositionResponseDTOMapperMock);
  }

  @Test
  void whenGetDebtPositionNoticesZipThenOk() {
    UserInfo loggedUser = new UserInfo();
    loggedUser.setMappedExternalUserId("mappedExternalUserId");
    Long brokerId = 1L;
    Long debtPositionId = 2L;
    Long organizationId = 3L;
    String fiscalCode = "fiscalCode";
    String iuv = "1";

    DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
    debtPositionDTO.setOrganizationId(organizationId);
    PaymentOptionDTO paymentOptionDTO = new PaymentOptionDTO();
    PaymentOptionDTO paymentOptionDTO1 = new PaymentOptionDTO();
    InstallmentDTO installmentDTOUNPAID = podamFactory.manufacturePojo(InstallmentDTO.class);
    installmentDTOUNPAID.setIuv(iuv);
    installmentDTOUNPAID.setStatus(InstallmentStatus.UNPAID);
    InstallmentDTO installmentDTOUNPAYABLE = podamFactory.manufacturePojo(InstallmentDTO.class);
    installmentDTOUNPAYABLE.setIuv(iuv);
    installmentDTOUNPAYABLE.setStatus(InstallmentStatus.UNPAYABLE);
    installmentDTOUNPAYABLE.getDebtor().setFiscalCode(fiscalCode);
    InstallmentDTO installmentDTOPAID = podamFactory.manufacturePojo(InstallmentDTO.class);
    installmentDTOPAID.setIuv(iuv);
    installmentDTOPAID.setStatus(InstallmentStatus.PAID);
    InstallmentDTO installmentDTOWithNullStatus = podamFactory.manufacturePojo(InstallmentDTO.class);
    installmentDTOWithNullStatus.setIuv(iuv);
    installmentDTOWithNullStatus.setStatus(null);
    paymentOptionDTO.setInstallments(List.of(installmentDTOUNPAID, installmentDTOUNPAYABLE));
    paymentOptionDTO1.setInstallments(List.of(installmentDTOPAID, installmentDTOWithNullStatus));
    debtPositionDTO.setPaymentOptions(List.of(paymentOptionDTO, paymentOptionDTO1));

    ByteArrayResource expectedResult = new ByteArrayResource("PDF-DATA".getBytes());

    FileResourceDTO fileResourceDTO = new FileResourceDTO(expectedResult, "filename");

    Mockito.doNothing().when(organizationRetrieverServiceMock).validateOrganization(organizationId,brokerId,accessToken);
    Mockito.when(debtPositionServiceMock.getDebtPosition(debtPositionId, accessToken)).thenReturn(debtPositionDTO);
    Mockito.when(printPaymentNoticeServiceMock.generateNotice(iuv, debtPositionDTO, accessToken)).thenReturn(fileResourceDTO);

    Mockito.when(zipFileServiceMock.zipper(List.of(fileResourceDTO, fileResourceDTO))).thenReturn(expectedResult);
    Resource result = debtPositionRetrieverService.getDebtPositionNoticesZip(brokerId, fiscalCode, debtPositionId, accessToken);

    assertNotNull(result);
    assertEquals(expectedResult, result);
    Mockito.verify(printPaymentNoticeServiceMock, Mockito.times(2)).generateNotice(iuv, debtPositionDTO, accessToken);
  }

  @Test
  void givenDebtPositionWithNoUnpayedOrUnpayableInstallmentsWhenGetDebtPositionNoticesZipThenReturnNull() {
    Long brokerId = 1L;
    Long debtPositionId = 2L;
    Long organizationId = 3L;
    String fiscalCode = "fiscalCode";

    DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
    debtPositionDTO.setOrganizationId(organizationId);
    PaymentOptionDTO paymentOptionDTO = new PaymentOptionDTO();
    InstallmentDTO installment = podamFactory.manufacturePojo(InstallmentDTO.class);
    installment.setStatus(InstallmentStatus.PAID);
    installment.getDebtor().setFiscalCode(fiscalCode);
    paymentOptionDTO.setInstallments(List.of(installment));
    debtPositionDTO.setPaymentOptions(List.of(paymentOptionDTO));

    Mockito.doNothing().when(organizationRetrieverServiceMock).validateOrganization(organizationId,brokerId,accessToken);
    Mockito.when(debtPositionServiceMock.getDebtPosition(debtPositionId, accessToken)).thenReturn(debtPositionDTO);

    Resource result = debtPositionRetrieverService.getDebtPositionNoticesZip(brokerId, fiscalCode, debtPositionId, accessToken);

    assertNull(result);

    Mockito.verifyNoInteractions(printPaymentNoticeServiceMock, zipFileServiceMock);
  }

  @Test
  void givenNoInstallmentWithMatchingFiscalCodeWhenGetDebtPositionNoticesZipThenAuthorizationDeniedException() {
    Long brokerId = 1L;
    Long debtPositionId = 2L;
    Long organizationId = 3L;
    String fiscalCode = "fiscalCode";

    DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
    debtPositionDTO.setOrganizationId(organizationId);

    Mockito.doNothing().when(organizationRetrieverServiceMock).validateOrganization(organizationId,brokerId,accessToken);
    Mockito.when(debtPositionServiceMock.getDebtPosition(debtPositionId, accessToken)).thenReturn(debtPositionDTO);

    assertThrows(AuthorizationDeniedException.class,()->debtPositionRetrieverService.getDebtPositionNoticesZip(brokerId, fiscalCode, debtPositionId, accessToken));

    Mockito.verifyNoInteractions(printPaymentNoticeServiceMock, zipFileServiceMock);
  }

  @Test
  void givenNullDebtPositionWhenGetDebtPositionNoticesZipThenReturnNull() {
    Long brokerId = 1L;
    Long debtPositionId = 2L;
    String fiscalCode = "fiscalCode";

    Mockito.when(debtPositionServiceMock.getDebtPosition(debtPositionId, accessToken)).thenReturn(null);

    Resource result = debtPositionRetrieverService.getDebtPositionNoticesZip(brokerId, fiscalCode, debtPositionId, accessToken);

    assertNull(result);
    Mockito.verifyNoInteractions(printPaymentNoticeServiceMock, zipFileServiceMock, organizationRetrieverServiceMock);
  }
}
