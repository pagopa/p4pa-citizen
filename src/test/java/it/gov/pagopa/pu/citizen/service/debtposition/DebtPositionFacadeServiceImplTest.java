package it.gov.pagopa.pu.citizen.service.debtposition;

import it.gov.pagopa.pu.auth.dto.generated.UserInfo;
import it.gov.pagopa.pu.citizen.connector.debtpositions.DebtPositionService;
import it.gov.pagopa.pu.citizen.connector.debtpositions.ReceiptService;
import it.gov.pagopa.pu.citizen.connector.pagopapayments.PrintPaymentNoticeService;
import it.gov.pagopa.pu.citizen.dto.FileResourceDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionResponseDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtorUnpaidDebtPositionOverviewDTO;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorDebtPositionDTO;
import it.gov.pagopa.pu.citizen.exception.ConflictException;
import it.gov.pagopa.pu.citizen.exception.InvalidParamException;
import it.gov.pagopa.pu.citizen.exception.ResourceNotFoundException;
import it.gov.pagopa.pu.citizen.mapper.DebtPositionDTOMapper;
import it.gov.pagopa.pu.citizen.mapper.DebtPositionResponseDTOMapper;
import it.gov.pagopa.pu.citizen.mapper.DebtorUnpaidDebtPositionOverviewMapper;
import it.gov.pagopa.pu.citizen.mapper.PagedDebtorDebtPositionMapper;
import it.gov.pagopa.pu.citizen.service.ZipFileService;
import it.gov.pagopa.pu.citizen.service.organization.BrokerOrganizationsRetrieverService;
import it.gov.pagopa.pu.citizen.service.organization.OrganizationRetrieverService;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.*;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authorization.AuthorizationDeniedException;
import uk.co.jemos.podam.api.PodamFactory;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class DebtPositionFacadeServiceImplTest {

  @Mock
  private DebtPositionService debtPositionServiceMock;
  @Mock
  private DebtPositionDTOMapper debtPositionDTOMapperMock;
  @Mock
  private DebtPositionResponseDTOMapper debtPositionResponseDTOMapperMock;
  @Mock
  private PrintPaymentNoticeService printPaymentNoticeServiceMock;
  @Mock
  private ZipFileService zipFileServiceMock;
  @Mock
  private OrganizationRetrieverService organizationRetrieverServiceMock;
  @Mock
  private BrokerOrganizationsRetrieverService brokerOrganizationsRetrieverServiceMock;
  @Mock
  private PagedDebtorDebtPositionMapper pagedDebtorDebtPositionMapperMock;
  @Mock
  private DebtorUnpaidDebtPositionOverviewMapper debtorUnpaidDebtPositionOverviewMapperMock;
  @Mock
  private ReceiptService receiptServiceMock;

  private DebtPositionFacadeService debtPositionFacadeService;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  private final String accessToken = "TOKEN";

  @BeforeEach
  void setUp() {
    debtPositionFacadeService = new DebtPositionFacadeServiceImpl(debtPositionServiceMock, debtPositionDTOMapperMock, 1,
        debtPositionResponseDTOMapperMock, printPaymentNoticeServiceMock, zipFileServiceMock, organizationRetrieverServiceMock, brokerOrganizationsRetrieverServiceMock, pagedDebtorDebtPositionMapperMock, debtorUnpaidDebtPositionOverviewMapperMock, receiptServiceMock);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      debtPositionServiceMock,
      debtPositionDTOMapperMock,
      debtPositionResponseDTOMapperMock,
      printPaymentNoticeServiceMock,
      zipFileServiceMock,
      organizationRetrieverServiceMock,
      debtorUnpaidDebtPositionOverviewMapperMock
    );
  }

  @Test
  void givenValidDebtPositionRequestDTOWhenCreateDebtPositionThenOk() {
    Long brokerId =1L;
    DebtPositionRequestDTO requestDTO = podamFactory.manufacturePojo(DebtPositionRequestDTO.class);
    UserInfo loggedUser = new UserInfo();
    loggedUser.setMappedExternalUserId("mappedExternalUserId");
    DebtPositionDTO debtPosition = podamFactory.manufacturePojo(DebtPositionDTO.class);
    Organization organization = podamFactory.manufacturePojo(Organization.class);
    DebtPositionResponseDTO expectedResult = podamFactory.manufacturePojo(DebtPositionResponseDTO.class);

    Mockito.when(organizationRetrieverServiceMock.getValidOrganization(requestDTO.getOrganizationId(), brokerId, accessToken)).thenReturn(organization);
    Mockito.when(debtPositionDTOMapperMock.mapSpontaneousDebtPositionDTO(requestDTO, 1)).thenReturn(debtPosition);
    Mockito.when(debtPositionServiceMock.createDebtPosition(debtPosition, false, accessToken))
        .thenReturn(debtPosition);
    Mockito.when(debtPositionResponseDTOMapperMock.map(debtPosition, organization)).thenReturn(expectedResult);

    DebtPositionResponseDTO result = debtPositionFacadeService.createSpontaneousDebtPosition(brokerId, requestDTO, accessToken);

    assertNotNull(result);
    assertSame(expectedResult, result);
  }

  @Test
  void givenInvalidOrganizationIdWhenCreateDebtPositionThenThrowException() {
    Long brokerId =1L;
    DebtPositionRequestDTO requestDTO = podamFactory.manufacturePojo(DebtPositionRequestDTO.class);
    UserInfo loggedUser = new UserInfo();
    loggedUser.setMappedExternalUserId("mappedExternalUserId");
    DebtPositionDTO debtPosition = podamFactory.manufacturePojo(DebtPositionDTO.class);

    Mockito.when(organizationRetrieverServiceMock.getValidOrganization(requestDTO.getOrganizationId(), brokerId, accessToken)).thenThrow(ResourceNotFoundException.class);
    Mockito.when(debtPositionDTOMapperMock.mapSpontaneousDebtPositionDTO(requestDTO, 1)).thenReturn(debtPosition);
    Mockito.when(debtPositionServiceMock.createDebtPosition(debtPosition, false, accessToken))
      .thenReturn(debtPosition);

    assertThrows(ResourceNotFoundException.class, () -> debtPositionFacadeService.createSpontaneousDebtPosition(brokerId, requestDTO, accessToken));
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
    String iuv = "iuv";
    String excludedIuv = "excludedIuv";

    DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
    debtPositionDTO.setOrganizationId(organizationId);
    PaymentOptionDTO paymentOptionDTO = new PaymentOptionDTO();
    PaymentOptionDTO paymentOptionDTO1 = new PaymentOptionDTO();
    InstallmentDTO installmentDTOUNPAID = podamFactory.manufacturePojo(InstallmentDTO.class);
    installmentDTOUNPAID.setIuv(iuv);
    installmentDTOUNPAID.setStatus(InstallmentStatus.UNPAID);
    installmentDTOUNPAID.getDebtor().setFiscalCode(fiscalCode);
    InstallmentDTO installmentDTOEXPIRED = podamFactory.manufacturePojo(InstallmentDTO.class);
    installmentDTOEXPIRED.setIuv(iuv);
    installmentDTOEXPIRED.setStatus(InstallmentStatus.EXPIRED);
    installmentDTOEXPIRED.getDebtor().setFiscalCode(fiscalCode);
    InstallmentDTO installmentDTOEXPIREDWithWrongFiscalCode = podamFactory.manufacturePojo(InstallmentDTO.class);
    installmentDTOEXPIREDWithWrongFiscalCode.setIuv(excludedIuv);
    installmentDTOEXPIREDWithWrongFiscalCode.setStatus(InstallmentStatus.EXPIRED);
    installmentDTOEXPIREDWithWrongFiscalCode.getDebtor().setFiscalCode("wrongFiscalCode");
    InstallmentDTO installmentDTOPAID = podamFactory.manufacturePojo(InstallmentDTO.class);
    installmentDTOPAID.setIuv(excludedIuv);
    installmentDTOPAID.setStatus(InstallmentStatus.PAID);
    InstallmentDTO installmentDTOWithNullStatus = podamFactory.manufacturePojo(InstallmentDTO.class);
    installmentDTOWithNullStatus.setIuv(excludedIuv);
    installmentDTOWithNullStatus.setStatus(null);
    paymentOptionDTO.setInstallments(List.of(installmentDTOUNPAID, installmentDTOPAID, installmentDTOEXPIREDWithWrongFiscalCode));
    paymentOptionDTO1.setInstallments(List.of(installmentDTOEXPIRED, installmentDTOWithNullStatus));
    debtPositionDTO.setPaymentOptions(List.of(paymentOptionDTO, paymentOptionDTO1));

    ByteArrayResource expectedResult = new ByteArrayResource("PDF-DATA".getBytes());

    FileResourceDTO fileResourceDTO = new FileResourceDTO(expectedResult, "filename");

    Mockito.doNothing().when(organizationRetrieverServiceMock).validateOrganization(organizationId, brokerId, accessToken);
    Mockito.when(debtPositionServiceMock.getDebtPosition(debtPositionId, accessToken)).thenReturn(debtPositionDTO);
    Mockito.when(printPaymentNoticeServiceMock.generateNotice(Mockito.anyString(), eq(debtPositionDTO), eq(accessToken))).thenReturn(fileResourceDTO);

    Mockito.when(zipFileServiceMock.zipper(List.of(fileResourceDTO, fileResourceDTO))).thenReturn(expectedResult);
    Resource result = debtPositionFacadeService.getDebtPositionNoticesZip(brokerId, fiscalCode, debtPositionId, accessToken);

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

    Mockito.doNothing().when(organizationRetrieverServiceMock).validateOrganization(organizationId, brokerId, accessToken);
    Mockito.when(debtPositionServiceMock.getDebtPosition(debtPositionId, accessToken)).thenReturn(debtPositionDTO);

    Resource result = debtPositionFacadeService.getDebtPositionNoticesZip(brokerId, fiscalCode, debtPositionId, accessToken);

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

    Mockito.doNothing().when(organizationRetrieverServiceMock).validateOrganization(organizationId, brokerId, accessToken);
    Mockito.when(debtPositionServiceMock.getDebtPosition(debtPositionId, accessToken)).thenReturn(debtPositionDTO);

    assertThrows(AuthorizationDeniedException.class,()-> debtPositionFacadeService.getDebtPositionNoticesZip(brokerId, fiscalCode, debtPositionId, accessToken));

    Mockito.verifyNoInteractions(printPaymentNoticeServiceMock, zipFileServiceMock);
  }

  @Test
  void givenNullDebtPositionWhenGetDebtPositionNoticesZipThenReturnNull() {
    Long brokerId = 1L;
    Long debtPositionId = 2L;
    String fiscalCode = "fiscalCode";

    Mockito.when(debtPositionServiceMock.getDebtPosition(debtPositionId, accessToken)).thenReturn(null);

    Resource result = debtPositionFacadeService.getDebtPositionNoticesZip(brokerId, fiscalCode, debtPositionId, accessToken);

    assertNull(result);
    Mockito.verifyNoInteractions(printPaymentNoticeServiceMock, zipFileServiceMock, organizationRetrieverServiceMock);
  }

  @Test
  void givenValidDebtPositionWhenGetDebtPositionDetailThenReturnDebtPositionDetail() {
    //given
    Long brokerId = 1L;
    Long debtPositionId = 2L;
    String fiscalCode = "fiscalCode";

    DebtPositionDTO debtPositionDTO = podamFactory.manufacturePojo(DebtPositionDTO.class);
    InstallmentDTO installment = debtPositionDTO.getPaymentOptions().getFirst().getInstallments().getFirst();
    installment.getDebtor().setFiscalCode(fiscalCode);
    installment.setStatus(InstallmentStatus.REPORTED);

    Mockito.doNothing().when(organizationRetrieverServiceMock).validateOrganization(debtPositionDTO.getOrganizationId(), brokerId, accessToken);
    Mockito.when(debtPositionServiceMock.getDebtPosition(debtPositionId, accessToken)).thenReturn(debtPositionDTO);
    //when
    DebtPositionDTO result = debtPositionFacadeService.getDebtPositionDetail(brokerId, fiscalCode, debtPositionId, accessToken);
    //then
    assertNotNull(result);
    assertEquals(debtPositionDTO, result);
    assertEquals(InstallmentStatus.PAID, result.getPaymentOptions().getFirst().getInstallments().getFirst().getStatus());
  }

  @Test
  void givenUnmatchedDebtorWhenGetDebtPositionDetailThenThrowAuthorizationDeniedException() {
    //given
    Long brokerId = 1L;
    Long debtPositionId = 2L;
    String fiscalCode = "fiscalCode";

    DebtPositionDTO debtPositionDTO = podamFactory.manufacturePojo(DebtPositionDTO.class);
    debtPositionDTO.setDebtPositionId(debtPositionId);

    Mockito.doNothing().when(organizationRetrieverServiceMock).validateOrganization(debtPositionDTO.getOrganizationId(), brokerId, accessToken);
    Mockito.when(debtPositionServiceMock.getDebtPosition(debtPositionId, accessToken)).thenReturn(debtPositionDTO);
    //when
    AuthorizationDeniedException ex = assertThrows(AuthorizationDeniedException.class, () -> debtPositionFacadeService.getDebtPositionDetail(brokerId, fiscalCode, debtPositionId, accessToken));

    //then
    assertEquals("[USER_UNAUTHORIZED] User cannot access DebtPosition having id 2", ex.getMessage());
  }

  @Test
  void givenNullDebtPositionWhenGetDebtPositionDetailThenReturnNull() {
    //given
    Long brokerId = 1L;
    Long debtPositionId = 2L;
    String fiscalCode = "fiscalCode";

    Mockito.when(debtPositionServiceMock.getDebtPosition(debtPositionId, accessToken)).thenReturn(null);
    //when
    DebtPositionDTO result = debtPositionFacadeService.getDebtPositionDetail(brokerId, fiscalCode, debtPositionId, accessToken);

    //then
    assertNull(result);
    Mockito.verifyNoInteractions(organizationRetrieverServiceMock);
  }

  @Test
  void givenInstallmentIdWhenGetPaymentNoticeThenOk() {
    UserInfo loggedUser = new UserInfo();
    loggedUser.setMappedExternalUserId("mappedExternalUserId");
    String fiscalCode = "fiscalCode";
    Long brokerId = 1L;
    Long organizationId = 2L;
    Long installmentId = 3L;

    DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
    debtPositionDTO.setOrganizationId(organizationId);
    debtPositionDTO.setDebtPositionOrigin(DebtPositionOrigin.ORDINARY);
    PaymentOptionDTO paymentOptionDTO = new PaymentOptionDTO();
    PaymentOptionDTO paymentOptionDTO1 = new PaymentOptionDTO();
    InstallmentDTO installmentDTOUNPAID = podamFactory.manufacturePojo(InstallmentDTO.class);
    installmentDTOUNPAID.setInstallmentId(installmentId);
    installmentDTOUNPAID.setStatus(InstallmentStatus.UNPAID);
    installmentDTOUNPAID.getDebtor().setFiscalCode(fiscalCode);
    InstallmentDTO installmentDTOUNPAYABLE = podamFactory.manufacturePojo(InstallmentDTO.class);
    installmentDTOUNPAYABLE.setStatus(InstallmentStatus.UNPAYABLE);
    InstallmentDTO installmentDTOPAID = podamFactory.manufacturePojo(InstallmentDTO.class);
    installmentDTOPAID.setStatus(InstallmentStatus.PAID);
    InstallmentDTO installmentDTOWithNullStatus = podamFactory.manufacturePojo(InstallmentDTO.class);
    installmentDTOWithNullStatus.setStatus(null);
    paymentOptionDTO.setInstallments(List.of(installmentDTOUNPAID, installmentDTOUNPAYABLE));
    paymentOptionDTO1.setInstallments(List.of(installmentDTOPAID, installmentDTOWithNullStatus));
    debtPositionDTO.setPaymentOptions(List.of(paymentOptionDTO, paymentOptionDTO1));

    ByteArrayResource resource = new ByteArrayResource("PDF-DATA".getBytes());

    FileResourceDTO expectedResult = new FileResourceDTO(resource, "filename");

    Mockito.doNothing().when(organizationRetrieverServiceMock).validateOrganization(organizationId, brokerId, accessToken);
    Mockito.when(debtPositionServiceMock.getDebtPositionByInstallmentId(installmentId, accessToken)).thenReturn(debtPositionDTO);
    Mockito.when(printPaymentNoticeServiceMock.generateNotice(installmentDTOUNPAID.getIuv(), debtPositionDTO, accessToken))
      .thenReturn(expectedResult);

    FileResourceDTO result = debtPositionFacadeService.getPaymentNotice(fiscalCode, brokerId, organizationId, installmentId, null, null, accessToken);

    assertNotNull(result);
    assertEquals(expectedResult, result);
  }

  @Test
  void givenIuvWhenGetPaymentNoticeThenOk() {
    UserInfo loggedUser = new UserInfo();
    loggedUser.setMappedExternalUserId("mappedExternalUserId");
    String fiscalCode = "fiscalCode";
    Long brokerId = 1L;
    Long organizationId = 2L;
    String iuv = "iuv";
    List<DebtPositionOrigin> debtPositionOrigins = List.of(    DebtPositionOrigin.ORDINARY,
      DebtPositionOrigin.ORDINARY_SIL,
      DebtPositionOrigin.SPONTANEOUS,
      DebtPositionOrigin.SPONTANEOUS_SIL);

    DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
    debtPositionDTO.setOrganizationId(organizationId);
    debtPositionDTO.setDebtPositionOrigin(DebtPositionOrigin.ORDINARY);
    PaymentOptionDTO paymentOptionDTO = new PaymentOptionDTO();
    PaymentOptionDTO paymentOptionDTO1 = new PaymentOptionDTO();
    InstallmentDTO installmentDTOUNPAID = podamFactory.manufacturePojo(InstallmentDTO.class);
    installmentDTOUNPAID.setStatus(InstallmentStatus.UNPAID);
    installmentDTOUNPAID.setIuv(iuv);
    installmentDTOUNPAID.getDebtor().setFiscalCode(fiscalCode);
    InstallmentDTO installmentDTOUNPAYABLE = podamFactory.manufacturePojo(InstallmentDTO.class);
    installmentDTOUNPAYABLE.setStatus(InstallmentStatus.UNPAYABLE);
    InstallmentDTO installmentDTOPAID = podamFactory.manufacturePojo(InstallmentDTO.class);
    installmentDTOPAID.setStatus(InstallmentStatus.PAID);
    InstallmentDTO installmentDTOWithNullStatus = podamFactory.manufacturePojo(InstallmentDTO.class);
    installmentDTOWithNullStatus.setStatus(null);
    paymentOptionDTO.setInstallments(List.of(installmentDTOUNPAID, installmentDTOUNPAYABLE));
    paymentOptionDTO1.setInstallments(List.of(installmentDTOPAID, installmentDTOWithNullStatus));
    debtPositionDTO.setPaymentOptions(List.of(paymentOptionDTO, paymentOptionDTO1));

    ByteArrayResource resource = new ByteArrayResource("PDF-DATA".getBytes());

    FileResourceDTO expectedResult = new FileResourceDTO(resource, "filename");

    Mockito.doNothing().when(organizationRetrieverServiceMock).validateOrganization(organizationId, brokerId, accessToken);
    Mockito.when(debtPositionServiceMock.getDebtPositionsByOrganizationIdAndIuv(organizationId, iuv, debtPositionOrigins, accessToken))
      .thenReturn(List.of(debtPositionDTO));
    Mockito.when(printPaymentNoticeServiceMock.generateNotice(installmentDTOUNPAID.getIuv(), debtPositionDTO, accessToken))
      .thenReturn(expectedResult);

    FileResourceDTO result = debtPositionFacadeService.getPaymentNotice(fiscalCode, brokerId, organizationId, null, iuv, null, accessToken);

    assertNotNull(result);
    assertEquals(expectedResult, result);
  }

  @Test
  void givenIudWhenGetPaymentNoticeThenOk() {
    UserInfo loggedUser = new UserInfo();
    loggedUser.setMappedExternalUserId("mappedExternalUserId");
    String fiscalCode = "fiscalCode";
    Long brokerId = 1L;
    Long organizationId = 2L;
    String iud = "iud";
    List<DebtPositionOrigin> debtPositionOrigins = List.of(    DebtPositionOrigin.ORDINARY,
      DebtPositionOrigin.ORDINARY_SIL,
      DebtPositionOrigin.SPONTANEOUS,
      DebtPositionOrigin.SPONTANEOUS_SIL);

    DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
    debtPositionDTO.setOrganizationId(organizationId);
    debtPositionDTO.setDebtPositionOrigin(DebtPositionOrigin.ORDINARY);
    PaymentOptionDTO paymentOptionDTO = new PaymentOptionDTO();
    PaymentOptionDTO paymentOptionDTO1 = new PaymentOptionDTO();
    InstallmentDTO installmentDTOUNPAID = podamFactory.manufacturePojo(InstallmentDTO.class);
    installmentDTOUNPAID.setStatus(InstallmentStatus.UNPAID);
    installmentDTOUNPAID.setIud(iud);
    installmentDTOUNPAID.getDebtor().setFiscalCode(fiscalCode);
    InstallmentDTO installmentDTOUNPAYABLE = podamFactory.manufacturePojo(InstallmentDTO.class);
    installmentDTOUNPAYABLE.setStatus(InstallmentStatus.UNPAYABLE);
    InstallmentDTO installmentDTOPAID = podamFactory.manufacturePojo(InstallmentDTO.class);
    installmentDTOPAID.setStatus(InstallmentStatus.PAID);
    InstallmentDTO installmentDTOWithNullStatus = podamFactory.manufacturePojo(InstallmentDTO.class);
    installmentDTOWithNullStatus.setStatus(null);
    paymentOptionDTO.setInstallments(List.of(installmentDTOUNPAID, installmentDTOUNPAYABLE));
    paymentOptionDTO1.setInstallments(List.of(installmentDTOPAID, installmentDTOWithNullStatus));
    debtPositionDTO.setPaymentOptions(List.of(paymentOptionDTO, paymentOptionDTO1));

    ByteArrayResource resource = new ByteArrayResource("PDF-DATA".getBytes());

    FileResourceDTO expectedResult = new FileResourceDTO(resource, "filename");

    Mockito.doNothing().when(organizationRetrieverServiceMock).validateOrganization(organizationId, brokerId, accessToken);
    Mockito.when(debtPositionServiceMock.getDebtPositionsByOrganizationIdAndIud(organizationId, iud, debtPositionOrigins, accessToken))
      .thenReturn(List.of(debtPositionDTO));
    Mockito.when(printPaymentNoticeServiceMock.generateNotice(installmentDTOUNPAID.getIuv(), debtPositionDTO, accessToken))
      .thenReturn(expectedResult);

    FileResourceDTO result = debtPositionFacadeService.getPaymentNotice(fiscalCode, brokerId, organizationId, null, null, iud, accessToken);

    assertNotNull(result);
    assertEquals(expectedResult, result);
  }

  @Test
  void givenNoInstallmentWithMatchingFiscalCodeWhenGetPaymentNoticeThenAuthorizationDeniedException() {
    UserInfo loggedUser = new UserInfo();
    loggedUser.setMappedExternalUserId("mappedExternalUserId");
    String fiscalCode = "fiscalCode";
    Long brokerId = 1L;
    Long organizationId = 2L;
    Long installmentId = 3L;

    DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
    debtPositionDTO.setOrganizationId(organizationId);
    debtPositionDTO.setDebtPositionOrigin(DebtPositionOrigin.ORDINARY);

    Mockito.doNothing().when(organizationRetrieverServiceMock).validateOrganization(organizationId, brokerId, accessToken);
    Mockito.when(debtPositionServiceMock.getDebtPositionByInstallmentId(installmentId, accessToken)).thenReturn(debtPositionDTO);

    assertThrows(AuthorizationDeniedException.class,()-> debtPositionFacadeService.getPaymentNotice(fiscalCode, brokerId, organizationId, installmentId, null, null, accessToken));

    Mockito.verifyNoInteractions(printPaymentNoticeServiceMock);
  }

  @Test
  void givenInvalidDebtPositionOriginWhenGetPaymentNoticeThenValidationException() {
    UserInfo loggedUser = new UserInfo();
    loggedUser.setMappedExternalUserId("mappedExternalUserId");
    String fiscalCode = "fiscalCode";
    Long brokerId = 1L;
    Long organizationId = 2L;
    Long installmentId = 3L;

    DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
    debtPositionDTO.setOrganizationId(organizationId);
    debtPositionDTO.setDebtPositionOrigin(DebtPositionOrigin.RECEIPT_FILE);

    Mockito.doNothing().when(organizationRetrieverServiceMock).validateOrganization(organizationId, brokerId, accessToken);
    Mockito.when(debtPositionServiceMock.getDebtPositionByInstallmentId(installmentId, accessToken)).thenReturn(debtPositionDTO);

    assertThrows(ValidationException.class,()-> debtPositionFacadeService.getPaymentNotice(fiscalCode, brokerId, organizationId, installmentId, null, null, accessToken));

    Mockito.verifyNoInteractions(printPaymentNoticeServiceMock);
  }

  @Test
  void givenNoMatchingOrganizationIdWhenGetPaymentNoticeThenConflictException() {
    UserInfo loggedUser = new UserInfo();
    loggedUser.setMappedExternalUserId("mappedExternalUserId");
    String fiscalCode = "fiscalCode";
    Long brokerId = 1L;
    Long organizationId = 2L;
    Long installmentId = 3L;

    DebtPositionDTO debtPositionDTO = new DebtPositionDTO();
    debtPositionDTO.setOrganizationId(organizationId+1);

    Mockito.doNothing().when(organizationRetrieverServiceMock).validateOrganization(organizationId, brokerId, accessToken);
    Mockito.when(debtPositionServiceMock.getDebtPositionByInstallmentId(installmentId, accessToken)).thenReturn(debtPositionDTO);

    assertThrows(ConflictException.class,()-> debtPositionFacadeService.getPaymentNotice(fiscalCode, brokerId, organizationId, installmentId, null, null, accessToken));

    Mockito.verifyNoInteractions(printPaymentNoticeServiceMock);
  }

  @Test
  void givenNoDebtPositionWhenGetPaymentNoticeThenReturnNull() {
    UserInfo loggedUser = new UserInfo();
    loggedUser.setMappedExternalUserId("mappedExternalUserId");
    String fiscalCode = "fiscalCode";
    Long brokerId = 1L;
    Long organizationId = 2L;
    Long installmentId = 3L;

    Mockito.doNothing().when(organizationRetrieverServiceMock).validateOrganization(organizationId, brokerId, accessToken);
    Mockito.when(debtPositionServiceMock.getDebtPositionByInstallmentId(installmentId, accessToken)).thenReturn(null);

    FileResourceDTO result = debtPositionFacadeService.getPaymentNotice(fiscalCode, brokerId, organizationId, installmentId, null, null, accessToken);

    Assertions.assertNull(result);
    Mockito.verifyNoInteractions(printPaymentNoticeServiceMock);
  }

  @Test
  void givenNoFiltersWhenGetPaymentNoticeThenInvalidParamException() {
    UserInfo loggedUser = new UserInfo();
    loggedUser.setMappedExternalUserId("mappedExternalUserId");
    String fiscalCode = "fiscalCode";
    Long brokerId = 1L;
    Long organizationId = 2L;

    Mockito.doNothing().when(organizationRetrieverServiceMock).validateOrganization(organizationId, brokerId, accessToken);

    assertThrows(InvalidParamException.class,() -> debtPositionFacadeService.getPaymentNotice(fiscalCode, brokerId, organizationId, null, null, null, accessToken));

    Mockito.verifyNoInteractions(debtPositionServiceMock, printPaymentNoticeServiceMock);
  }

  @Test
  void givenMoreThanOneFilterWhenGetPaymentNoticeThenInvalidParamException() {
    UserInfo loggedUser = new UserInfo();
    loggedUser.setMappedExternalUserId("mappedExternalUserId");
    String fiscalCode = "fiscalCode";
    Long brokerId = 1L;
    Long organizationId = 2L;
    Long installmentId = 3L;
    String iuv = "iuv";

    Mockito.doNothing().when(organizationRetrieverServiceMock).validateOrganization(organizationId, brokerId, accessToken);

    assertThrows(InvalidParamException.class,() -> debtPositionFacadeService.getPaymentNotice(fiscalCode, brokerId, organizationId, installmentId, iuv, null, accessToken));

    Mockito.verifyNoInteractions(debtPositionServiceMock, printPaymentNoticeServiceMock);
  }

  @Test
  void givenValidParamsWhenGetPagedUnpaidDebtPositionsThenReturnDTO() {
    // given
    Long brokerId = 1L;
    String xFiscalCode = "debtorFiscalCode";
    String orgName = "TestOrg";
    String orgFiscalCode = "12345678901";

    Pageable pageable = Mockito.mock(Pageable.class);

    List<Organization> organizations = podamFactory.manufacturePojo(List.class, Organization.class);
    Map<Long, Organization> orgMap = organizations.stream()
      .collect(Collectors.toMap(Organization::getOrganizationId, o -> o));

    PagedDebtorUnpaidDebtPositionDTO pagedDebtorUnpaidDebtPositionDTO = podamFactory.manufacturePojo(PagedDebtorUnpaidDebtPositionDTO.class);

    PagedDebtorDebtPositionDTO expectedResult =
      podamFactory.manufacturePojo(PagedDebtorDebtPositionDTO.class);

    Mockito.when(brokerOrganizationsRetrieverServiceMock
        .getAllOrganizationsByBrokerIdAndOrgNameAndOrgFiscalCode(brokerId, orgName, orgFiscalCode, accessToken))
      .thenReturn(organizations);

    Mockito.when(debtPositionServiceMock.getPagedDebtorUnpaidDebtPosition(xFiscalCode,
        new ArrayList<>(orgMap.keySet()), pageable, accessToken))
      .thenReturn(pagedDebtorUnpaidDebtPositionDTO);


    Mockito.when(pagedDebtorDebtPositionMapperMock.map(
       orgMap,
      pagedDebtorUnpaidDebtPositionDTO))
      .thenReturn(expectedResult);

    // when
    PagedDebtorDebtPositionDTO result = debtPositionFacadeService.getPagedUnpaidDebtPositions(
      xFiscalCode, brokerId, orgName, orgFiscalCode, pageable, accessToken
    );

    // then
    assertNotNull(result);
    assertEquals(expectedResult, result);
  }

  @Test
  void givenNullOrganizationsWhenGetPagedUnpaidDebtPositionsThenThrowException() {
    // given
    Long brokerId = 1L;
    String xFiscalCode = "debtorFiscalCode";

    Pageable pageable = Mockito.mock(Pageable.class);

    List<Organization> organizations = new ArrayList<>();

    Mockito.when(brokerOrganizationsRetrieverServiceMock
        .getAllOrganizationsByBrokerIdAndOrgNameAndOrgFiscalCode(brokerId, null, null, accessToken))
      .thenReturn(organizations);

    assertThrows(ResourceNotFoundException.class, () -> debtPositionFacadeService.getPagedUnpaidDebtPositions(xFiscalCode,brokerId, null, null, pageable, accessToken));
  }

  @Test
  void givenValidDebtPositionOverviewWhenGetThenReturnMappedDTO() {
    // given
    Long brokerId = 1L;
    Long debtPositionId = 2L;
    Long organizationId = 3L;
    String debtorFiscalCode = "debtorFiscalCode";

    Organization org = podamFactory.manufacturePojo(Organization.class);
    DebtorDebtPositionDTO debtPositionDTO = podamFactory.manufacturePojo(DebtorDebtPositionDTO.class);
    BasePaymentOption po = podamFactory.manufacturePojo(BasePaymentOption.class);
    po.setTotalAmountCents(20L);
    po.setInstallments(new java.util.ArrayList<>());

    BaseInstallment inst = podamFactory.manufacturePojo(BaseInstallment.class);
    inst.setAmountCents(20L);
    inst.setDueDate(LocalDate.of(2024,1,10));
    inst.setReceiptId(1L);
    po.getInstallments().add(inst);

    debtPositionDTO.setPaymentOptions(List.of(po));

    DebtorUnpaidDebtPositionOverviewDTO expectedDTO = podamFactory.manufacturePojo(DebtorUnpaidDebtPositionOverviewDTO.class);

    ReceiptNoPII receipt = podamFactory.manufacturePojo(ReceiptNoPII.class);
    receipt.setReceiptId(inst.getReceiptId());

    List<ReceiptNoPII> receiptNoPIIList = List.of(receipt);

    Mockito.when(debtPositionServiceMock.getDebtorDebtPositionOverview(debtPositionId, debtorFiscalCode, organizationId, accessToken))
      .thenReturn(debtPositionDTO);
    Mockito.when(organizationRetrieverServiceMock.getValidOrganization(organizationId, brokerId, accessToken))
      .thenReturn(org);
    Mockito.when(
      receiptServiceMock.getReceiptNoPiiList(Set.of(inst.getReceiptId()), accessToken)
    ).thenReturn(receiptNoPIIList);

    Map<Long, OffsetDateTime> expectedMap =
      Map.of(inst.getInstallmentId(), receipt.getPaymentDateTime());

    Mockito.when(
      debtorUnpaidDebtPositionOverviewMapperMock.map(
        org,
        debtPositionDTO,
        expectedMap
      )
    ).thenReturn(expectedDTO);

    // when
    DebtorUnpaidDebtPositionOverviewDTO result = debtPositionFacadeService.getDebtorUnpaidDebtPositionOverview(
      brokerId, debtPositionId, debtorFiscalCode, organizationId, accessToken
    );

    // then
    assertNotNull(result);
    assertSame(expectedDTO, result);
  }

  @Test
  void givenNullDebtPositionOverviewWhenGetThenReturnNull() {
    // given
    Long brokerId = 1L;
    Long debtPositionId = 2L;
    Long organizationId = 3L;
    String debtorFiscalCode = "debtorFiscalCode";

    Mockito.when(debtPositionServiceMock.getDebtorDebtPositionOverview(debtPositionId, debtorFiscalCode, organizationId, accessToken))
      .thenReturn(null);

    // when
    DebtorUnpaidDebtPositionOverviewDTO result = debtPositionFacadeService.getDebtorUnpaidDebtPositionOverview(
      brokerId, debtPositionId, debtorFiscalCode, organizationId, accessToken
    );

    // then
    assertNull(result);
    Mockito.verifyNoInteractions(organizationRetrieverServiceMock, debtorUnpaidDebtPositionOverviewMapperMock);
  }

}
