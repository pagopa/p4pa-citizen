package it.gov.pagopa.pu.citizen.service.receipt;

import it.gov.pagopa.pu.auth.dto.generated.UserInfo;
import it.gov.pagopa.pu.citizen.connector.debtpositions.ReceiptService;
import it.gov.pagopa.pu.citizen.dto.DebtorReceiptsFiltersDTO;
import it.gov.pagopa.pu.citizen.dto.FileResourceDTO;
import it.gov.pagopa.pu.citizen.dto.ReceiptDetailExtendedDTO;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorReceiptsDTO;
import it.gov.pagopa.pu.citizen.exception.ResourceNotFoundException;
import it.gov.pagopa.pu.citizen.mapper.PagedDebtorReceiptsDTOMapper;
import it.gov.pagopa.pu.citizen.mapper.ReceiptDetailExtendedMapper;
import it.gov.pagopa.pu.citizen.service.organization.BrokerOrganizationsRetrieverService;
import it.gov.pagopa.pu.citizen.service.organization.OrganizationRetrieverService;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.PagedModelReceiptNoPIIView;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptDetailDTO;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptOriginType;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.authorization.AuthorizationDeniedException;
import uk.co.jemos.podam.api.PodamFactory;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptOriginType.RECEIPT_PAGOPA;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReceiptFacadeServiceImplTest {

  @Mock
  private ReceiptService receiptServiceMock;
  @Mock
  private BrokerOrganizationsRetrieverService brokerOrganizationsRetrieverServiceMock;
  @Mock
  private PagedDebtorReceiptsDTOMapper pagedDebtorReceiptsDTOMapperMock;
  @Mock
  private OrganizationRetrieverService organizationRetrieverServiceMock;
  @Mock
  private ReceiptDetailExtendedMapper receiptDetailExtendedMapperMock;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  ReceiptFacadeService receiptFacadeService;

  @BeforeEach
  void setUp() {
    receiptFacadeService = new ReceiptFacadeServiceImpl(receiptServiceMock, brokerOrganizationsRetrieverServiceMock, pagedDebtorReceiptsDTOMapperMock, organizationRetrieverServiceMock, receiptDetailExtendedMapperMock);
  }

  @AfterEach
  void mockitoVerify() {
    Mockito.verifyNoMoreInteractions(receiptServiceMock, brokerOrganizationsRetrieverServiceMock, pagedDebtorReceiptsDTOMapperMock);
  }

  @Test
  void givenFiltersWhenGetPagedDebtorReceiptsThenReturnPagedDebtorReceiptsDTO() {
    //given
    Long brokerId = 1L;
    String orgName = "orgName";
    String accessToken = "accessToken";
    String fiscalCode = "fiscalCode";
    List<ReceiptOriginType> receipts = List.of(RECEIPT_PAGOPA);
    String noticeNumberOrIuv = "noticeNumberOrIuv";
    OffsetDateTime paymentDateTimeFrom = OffsetDateTime.now().minusDays(1);
    OffsetDateTime paymentDateTimeTo = OffsetDateTime.now();
    DebtorReceiptsFiltersDTO debtorReceiptsFiltersDTO = DebtorReceiptsFiltersDTO.builder()
      .debtorFiscalCode(fiscalCode)
      .noticeNumberOrIuv(noticeNumberOrIuv)
      .paymentDateTimeTo(paymentDateTimeTo)
      .paymentDateTimeFrom(paymentDateTimeFrom)
      .build();

    List<Organization> organizations = podamFactory.manufacturePojo(List.class, Organization.class);
    Map<String, Organization> organizationMap = organizations.stream().collect(Collectors.toMap(Organization::getOrgFiscalCode, org -> org));
    List<String> organizationsFiscalCode = new ArrayList<>(organizationMap.keySet());
    PagedModelReceiptNoPIIView pagedModelReceiptNoPIIView = podamFactory.manufacturePojo(PagedModelReceiptNoPIIView.class);
    PagedDebtorReceiptsDTO expectedResult = podamFactory.manufacturePojo(PagedDebtorReceiptsDTO.class);

    Mockito.when(brokerOrganizationsRetrieverServiceMock.getAllOrganizationsByBrokerIdAndOrgName(brokerId, orgName, accessToken)).thenReturn(organizations);
    Mockito.when(receiptServiceMock.getPagedModelReceiptNoPIIView(debtorReceiptsFiltersDTO, null, accessToken)).thenReturn(pagedModelReceiptNoPIIView);
    Mockito.when(pagedDebtorReceiptsDTOMapperMock.map(organizationMap, pagedModelReceiptNoPIIView)).thenReturn(expectedResult);
    //when

    PagedDebtorReceiptsDTO result = receiptFacadeService.getPagedDebtorReceipts(brokerId, orgName, debtorReceiptsFiltersDTO, accessToken, null);
    //then
    assertNotNull(result);
    assertEquals(expectedResult, result);
    assertEquals(organizationsFiscalCode, debtorReceiptsFiltersDTO.getOrganizationsFiscalCode());
    assertEquals(receipts, debtorReceiptsFiltersDTO.getReceiptOrigins());
  }

  @Test
  void givenEmptyListWhenGetPagedDebtorReceiptsThenThrowResourceNotFoundException() {
    //given
    Long brokerId = 1L;
    String orgName = "orgName";
    String accessToken = "accessToken";
    String fiscalCode = "fiscalCode";
    String noticeNumberOrIuv = "noticeNumberOrIuv";
    OffsetDateTime paymentDateTimeFrom = OffsetDateTime.now().minusDays(1);
    OffsetDateTime paymentDateTimeTo = OffsetDateTime.now();
    DebtorReceiptsFiltersDTO debtorReceiptsFiltersDTO = DebtorReceiptsFiltersDTO.builder()
      .debtorFiscalCode(fiscalCode)
      .noticeNumberOrIuv(noticeNumberOrIuv)
      .paymentDateTimeTo(paymentDateTimeTo)
      .paymentDateTimeFrom(paymentDateTimeFrom)
      .build();

    Mockito.when(brokerOrganizationsRetrieverServiceMock.getAllOrganizationsByBrokerIdAndOrgName(brokerId, orgName, accessToken)).thenReturn(Collections.emptyList());
    //when

    assertThrows(ResourceNotFoundException.class, () -> receiptFacadeService.getPagedDebtorReceipts(brokerId, orgName, debtorReceiptsFiltersDTO, accessToken, null));
    Mockito.verifyNoInteractions(receiptServiceMock, pagedDebtorReceiptsDTOMapperMock);
  }

  @Test
  void whenGetReceiptDetailThenOk() {
    UserInfo loggedUser = new UserInfo();
    loggedUser.setMappedExternalUserId("mappedExternalUserId");
    String accessToken = "accessToken";
    Long brokerId = 1L;
    Long organizationId = 2L;
    Long receiptId = 3L;
    String fiscalCode = "fiscalCode";

    Organization organization = podamFactory.manufacturePojo(Organization.class);
    ReceiptDetailDTO receiptDetailDTO = podamFactory.manufacturePojo(ReceiptDetailDTO.class);
    receiptDetailDTO.getDebtor().setFiscalCode(fiscalCode);
    ReceiptDetailExtendedDTO expectedResult = podamFactory.manufacturePojo(ReceiptDetailExtendedDTO.class);
    expectedResult.getDebtor().setFiscalCode(fiscalCode);

    Mockito.when(organizationRetrieverServiceMock.getValidOrganization(organizationId, brokerId, accessToken)).thenReturn(organization);
    Mockito.when(receiptServiceMock.getReceiptDetail(receiptId,organizationId,accessToken)).thenReturn(receiptDetailDTO);
    Mockito.when(receiptDetailExtendedMapperMock.map(organization, receiptDetailDTO)).thenReturn(expectedResult);

    ReceiptDetailExtendedDTO result = receiptFacadeService.getReceiptDetail(fiscalCode,brokerId,organizationId,receiptId, accessToken);

    assertNotNull(result);
    assertEquals(expectedResult, result);
  }

  @Test
  void givenNoReceiptWhenGetReceiptDetailThenNull() {
    UserInfo loggedUser = new UserInfo();
    loggedUser.setMappedExternalUserId("mappedExternalUserId");
    String accessToken = "accessToken";
    Long brokerId = 1L;
    Long organizationId = 2L;
    Long receiptId = 3L;
    String fiscalCode = "fiscalCode";

    Mockito.when(receiptServiceMock.getReceiptDetail(receiptId,organizationId,accessToken)).thenReturn(null);

    ReceiptDetailExtendedDTO result = receiptFacadeService.getReceiptDetail(fiscalCode,brokerId,organizationId,receiptId, accessToken);

    assertNull(result);
    Mockito.verifyNoInteractions(receiptDetailExtendedMapperMock);
  }

  @Test
  void givenNoMatchingFiscalCodeWhenGetReceiptDetailThenAuthorizationDeniedException() {
    UserInfo loggedUser = new UserInfo();
    loggedUser.setMappedExternalUserId("mappedExternalUserId");
    String accessToken = "accessToken";
    Long brokerId = 1L;
    Long organizationId = 2L;
    Long receiptId = 3L;
    String fiscalCode = "fiscalCode";

    ReceiptDetailDTO expectedResult = podamFactory.manufacturePojo(ReceiptDetailDTO.class);
    expectedResult.getDebtor().setFiscalCode(fiscalCode+"1");

    Mockito.when(receiptServiceMock.getReceiptDetail(receiptId,organizationId,accessToken)).thenReturn(expectedResult);

    assertThrows(AuthorizationDeniedException.class,() -> receiptFacadeService.getReceiptDetail(fiscalCode,brokerId,organizationId,receiptId, accessToken));
  }

  @Test
  void whenGetReceiptPdfThenOk() {
    UserInfo loggedUser = new UserInfo();
    loggedUser.setMappedExternalUserId("mappedExternalUserId");
    String accessToken = "accessToken";
    Long brokerId = 1L;
    Long organizationId = 2L;
    Long receiptId = 3L;
    String debtorFiscalCode = "debtorFiscalCode";
    ByteArrayResource resource = new ByteArrayResource("PDF-DATA".getBytes());
    FileResourceDTO expectedResult = new FileResourceDTO(resource, "filename");

    Mockito.doNothing().when(organizationRetrieverServiceMock).validateOrganization(organizationId, brokerId, accessToken);
    Mockito.when(receiptServiceMock.isReceiptDebtorValid(receiptId,organizationId,debtorFiscalCode,accessToken))
      .thenReturn(true);
    Mockito.when(receiptServiceMock.getReceiptPdf(receiptId,organizationId,accessToken)).thenReturn(expectedResult);

    FileResourceDTO result = receiptFacadeService.getReceiptPdf(debtorFiscalCode,brokerId,organizationId,receiptId,accessToken);

    assertNotNull(result);
    assertEquals(expectedResult, result);
  }

  @Test
  void givenInvalidReceiptDebtorWhenGetReceiptPdfThenNull() {
    UserInfo loggedUser = new UserInfo();
    loggedUser.setMappedExternalUserId("mappedExternalUserId");
    String accessToken = "accessToken";
    Long brokerId = 1L;
    Long organizationId = 2L;
    Long receiptId = 3L;
    String debtorFiscalCode = "debtorFiscalCode";

    Mockito.doNothing().when(organizationRetrieverServiceMock).validateOrganization(organizationId, brokerId, accessToken);
    Mockito.when(receiptServiceMock.isReceiptDebtorValid(receiptId,organizationId,debtorFiscalCode,accessToken))
      .thenReturn(false);

    FileResourceDTO result = receiptFacadeService.getReceiptPdf(debtorFiscalCode,brokerId,organizationId,receiptId,accessToken);

    assertNull(result);
  }
}
