package it.gov.pagopa.pu.citizen.service.receipt;

import it.gov.pagopa.pu.citizen.connector.debtpositions.ReceiptService;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorReceiptsDTO;
import it.gov.pagopa.pu.citizen.exception.ResourceNotFoundException;
import it.gov.pagopa.pu.citizen.mapper.PagedDebtorReceiptsDTOMapper;
import it.gov.pagopa.pu.citizen.service.organization.BrokerOrganizationsRetrieverService;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.debtpositions.dto.generated.PagedModelReceiptNoPIIView;
import it.gov.pagopa.pu.debtpositions.dto.generated.ReceiptOriginType;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

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

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  ReceiptFacadeService receiptFacadeService;

  @BeforeEach
  void setUp() {
    receiptFacadeService = new ReceiptFacadeServiceImpl(receiptServiceMock, brokerOrganizationsRetrieverServiceMock, pagedDebtorReceiptsDTOMapperMock);
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

    List<Organization> organizations = podamFactory.manufacturePojo(List.class, Organization.class);
    Map<String, Organization> organizationMap = organizations.stream().collect(Collectors.toMap(Organization::getOrgFiscalCode, org -> org));
    List<String> organizationsFiscalCode = new ArrayList<>(organizationMap.keySet());
    PagedModelReceiptNoPIIView pagedModelReceiptNoPIIView = podamFactory.manufacturePojo(PagedModelReceiptNoPIIView.class);
    PagedDebtorReceiptsDTO expectedResult = podamFactory.manufacturePojo(PagedDebtorReceiptsDTO.class);

    Mockito.when(brokerOrganizationsRetrieverServiceMock.getAllOrganizationsByBrokerIdAndOrgName(brokerId, orgName, accessToken)).thenReturn(organizations);
    Mockito.when(receiptServiceMock.getPagedModelReceiptNoPIIView(fiscalCode, organizationsFiscalCode, receipts, null, accessToken)).thenReturn(pagedModelReceiptNoPIIView);
    Mockito.when(pagedDebtorReceiptsDTOMapperMock.map(organizationMap, pagedModelReceiptNoPIIView)).thenReturn(expectedResult);
    //when

    PagedDebtorReceiptsDTO result = receiptFacadeService.getPagedDebtorReceipts(brokerId, orgName, fiscalCode, accessToken, null);
    //then
    assertNotNull(result);
    assertEquals(expectedResult, result);
  }

  @Test
  void givenEmptyListWhenGetPagedDebtorReceiptsThenThrowResourceNotFoundException() {
    //given
    Long brokerId = 1L;
    String orgName = "orgName";
    String accessToken = "accessToken";
    String fiscalCode = "fiscalCode";

    Mockito.when(brokerOrganizationsRetrieverServiceMock.getAllOrganizationsByBrokerIdAndOrgName(brokerId, orgName, accessToken)).thenReturn(Collections.emptyList());
    //when

    assertThrows(ResourceNotFoundException.class, () -> receiptFacadeService.getPagedDebtorReceipts(brokerId, orgName, fiscalCode, accessToken, null));
    Mockito.verifyNoInteractions(receiptServiceMock, pagedDebtorReceiptsDTOMapperMock);
  }
}
