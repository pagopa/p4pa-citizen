package it.gov.pagopa.pu.citizen.connector.organization.client;

import it.gov.pagopa.pu.citizen.connector.organization.config.OrganizationApisHolder;
import it.gov.pagopa.pu.citizen.exception.common.RestInvokeNotFoundException;
import it.gov.pagopa.pu.citizen.utils.PageUtils;
import it.gov.pagopa.pu.citizen.utils.TestUtils;
import it.gov.pagopa.pu.organization.client.generated.OrganizationSearchControllerApi;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationStatus;
import it.gov.pagopa.pu.organization.dto.generated.PagedModelOrganization;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.ArrayList;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganizationSearchClientTest {

  @Mock
  private OrganizationApisHolder organizationApisHolderMock;
  @Mock
  private OrganizationSearchControllerApi organizationSearchControllerApiMock;

  private final PodamFactory podamFactory = TestUtils.getPodamFactory();

  OrganizationSearchClient organizationSearchClient;

  @BeforeEach
  void setUp() {
    organizationSearchClient = new OrganizationSearchClient(organizationApisHolderMock);
  }

  @AfterEach
  void mockitoVerify(){
    Mockito.verifyNoMoreInteractions(organizationApisHolderMock, organizationSearchControllerApiMock);
  }

  @Test
  void givenBrokerIdWhenGetPagedOrganizationsByBrokerIdAndStatusThenReturnOrganizationsList() {
    //given
    String accessToken = "ACCESS_TOKEN";
    Long brokerId = 1L;
    PagedModelOrganization expectedResult = podamFactory.manufacturePojo(PagedModelOrganization.class);

    PageRequest pageable = PageRequest.of(0, 10);

    when(organizationApisHolderMock.getOrganizationSearchControllerApi(accessToken)).thenReturn(organizationSearchControllerApiMock);

    when(organizationSearchControllerApiMock.crudOrganizationsFindPagedOrganizationsByBrokerIdAndStatus(brokerId, OrganizationStatus.ACTIVE, pageable.getPageNumber(), pageable.getPageSize(), new ArrayList<>())).thenReturn(expectedResult);
    //when
    PagedModelOrganization result = organizationSearchClient.getPagedOrganizationsByBrokerIdAndStatus(brokerId, OrganizationStatus.ACTIVE, pageable, accessToken);
    //then
    Assertions.assertNotNull(result);
    Assertions.assertEquals(expectedResult, result);
  }

  @Test
  void givenBrokerIdAndOrgNameWhenGetOrganizationsListByBrokerIdAndOrgNameThenReturnPagedModelOrganization() {
    //given
    String accessToken = "ACCESS_TOKEN";
    Long brokerId = 1L;
    String orgName = "orgName";
    PagedModelOrganization expectedResult = podamFactory.manufacturePojo(PagedModelOrganization.class);

    PageRequest pageable = PageRequest.of(0, 10);

    when(organizationApisHolderMock.getOrganizationSearchControllerApi(accessToken)).thenReturn(organizationSearchControllerApiMock);

    when(organizationSearchControllerApiMock.crudOrganizationsFindByBrokerIdAndOrgName(brokerId, orgName, pageable.getPageNumber(), pageable.getPageSize(), new ArrayList<>())).thenReturn(expectedResult);
    //when
    PagedModelOrganization result = organizationSearchClient.getOrganizationsListByBrokerIdAndOrgName(brokerId, orgName, pageable, accessToken);
    //then
    Assertions.assertNotNull(result);
    Assertions.assertEquals(expectedResult, result);
  }

  @Test
  void whenGetOrganizationsByBrokerIdAndOrgNameAndOrgFiscalCodeThenInvokeClient() {
    // given
    String accessToken = "ACCESS_TOKEN";
    Long brokerId = 10L;
    String orgName = "Test Organization";
    String orgFiscalCode = "12345678901";
    Pageable pageable = PageRequest.of(0, 20);

    PagedModelOrganization expectedResult = podamFactory.manufacturePojo(PagedModelOrganization.class);

    when(organizationApisHolderMock.getOrganizationSearchControllerApi(accessToken))
      .thenReturn(organizationSearchControllerApiMock);

    when(organizationSearchControllerApiMock
        .crudOrganizationsFindByBrokerIdAndOrgNameAndOrgFiscalCode(
          brokerId,
          orgName,
          orgFiscalCode,
          PageUtils.getPageNumber(pageable),
          PageUtils.getPageSize(pageable),
          PageUtils.getSortList(pageable)
        ))
      .thenReturn(expectedResult);

    // when
    PagedModelOrganization result =
      organizationSearchClient.getOrganizationsByBrokerIdAndOrgNameAndOrgFiscalCode(
        brokerId, orgName, orgFiscalCode, pageable, accessToken
      );

    // then
    Assertions.assertNotNull(result);
    Assertions.assertSame(expectedResult, result);
  }

  @Test
  void whenGetBrokerOrganizationThenInvokeWithAccessToken() {
    Long brokerId = 1L;
    String accessToken = "ACCESSTOKEN";
    Organization expectedResult = new Organization();

    when(organizationApisHolderMock.getOrganizationSearchControllerApi(accessToken))
      .thenReturn(organizationSearchControllerApiMock);
    when(organizationSearchControllerApiMock.crudOrganizationsGetBrokerOrganization(brokerId))
      .thenReturn(expectedResult);

    Organization result = organizationSearchClient.getBrokerOrganization(brokerId, accessToken);

    Assertions.assertSame(expectedResult, result);
  }

  @Test
  void givenNotFoundWhenGetBrokerOrganizationThenNull() {
    Long brokerId = 1L;
    String accessToken = "ACCESSTOKEN";

    when(organizationApisHolderMock.getOrganizationSearchControllerApi(accessToken))
      .thenReturn(organizationSearchControllerApiMock);
    when(organizationSearchControllerApiMock.crudOrganizationsGetBrokerOrganization(brokerId))
      .thenThrow(new RestInvokeNotFoundException("APPNAME", HttpStatus.NOT_FOUND, "ERROR", "ERRORCODE", "ERRORMESSAGE"));

    Organization result = organizationSearchClient.getBrokerOrganization(brokerId, accessToken);

    Assertions.assertNull(result);
  }

  @Test
  void whenFindByOrgFiscalCodeThenOk() {
    String orgFiscalCode = "orgFiscalCode";
    String accessToken = "ACCESSTOKEN";
    Organization expectedResult = new Organization();

    when(organizationApisHolderMock.getOrganizationSearchControllerApi(accessToken))
      .thenReturn(organizationSearchControllerApiMock);
    when(organizationSearchControllerApiMock.crudOrganizationsFindByOrgFiscalCode(orgFiscalCode))
      .thenReturn(expectedResult);

    Organization result = organizationSearchClient.findByOrgFiscalCode(orgFiscalCode, accessToken);

    Assertions.assertSame(expectedResult, result);
  }

  @Test
  void givenNotFoundWhenFindByOrgFiscalCodeThenNull() {
    String orgFiscalCode = "orgFiscalCode";
    String accessToken = "ACCESSTOKEN";

    when(organizationApisHolderMock.getOrganizationSearchControllerApi(accessToken))
      .thenReturn(organizationSearchControllerApiMock);
    when(organizationSearchControllerApiMock.crudOrganizationsFindByOrgFiscalCode(orgFiscalCode))
      .thenThrow(new RestInvokeNotFoundException("APPNAME", HttpStatus.NOT_FOUND, "ERROR", "ERRORCODE", "ERRORMESSAGE"));

    Organization result = organizationSearchClient.findByOrgFiscalCode(orgFiscalCode, accessToken);

    Assertions.assertNull(result);
  }
}
