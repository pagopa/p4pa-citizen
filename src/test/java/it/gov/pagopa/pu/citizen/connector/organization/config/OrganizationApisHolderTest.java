package it.gov.pagopa.pu.citizen.connector.organization.config;

import it.gov.pagopa.pu.citizen.config.json.JsonConfig;
import it.gov.pagopa.pu.citizen.connector.BaseApiHolderTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.ArrayList;
import java.util.Set;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganizationApisHolderTest extends BaseApiHolderTest {

  @Mock
  private RestTemplateBuilder restTemplateBuilderMock;

  private OrganizationApisHolder apisHolder;
  private OrganizationApiClientConfig apiClientConfig;

  @BeforeEach
  void setUp() {
    when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
    when(restTemplateMock.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());

    apiClientConfig = OrganizationApiClientConfig.builder()
      .baseUrl("http://example.com")
      .maxAttempts(3)
      .build();
    apisHolder = new OrganizationApisHolder(apiClientConfig, restTemplateBuilderMock, new JsonConfig().objectMapperJackson3());

    verifyHttpClientErrorJsonBodyHandlerConfiguration(apisHolder.getOrganizationSearchControllerApi(null));
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      restTemplateBuilderMock,
      restTemplateMock
    );
  }

  @Test
  void testRetryConfiguration() {
    assertRetry(apiClientConfig,
      accessToken -> apisHolder.getOrganizationSearchControllerApi(accessToken)
        .crudOrganizationsFindByBrokerIdAndFilters(1L, "orgName", "ipaCode", "orgFiscalCode", Set.of(),1, 1,  new ArrayList<>()),
      new ParameterizedTypeReference<>() {}
    );
  }

  @Test
  void whenGetOrganizationSearchControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
    assertAuthenticationShouldBeSetInThreadSafeMode(
      accessToken -> apisHolder.getOrganizationSearchControllerApi(accessToken)
        .crudOrganizationsFindByBrokerIdAndFilters(1L, "orgName", "ipaCode", "orgFiscalCode", Set.of(),1, 1,  new ArrayList<>()),
      new ParameterizedTypeReference<>() {},
      apisHolder::unload);
  }

  @Test
  void whenGetOrganizationEntityControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
    assertAuthenticationShouldBeSetInThreadSafeMode(
      accessToken -> apisHolder.getOrganizationEntityControllerApi(accessToken)
        .crudGetOrganization("1"),
      new ParameterizedTypeReference<>() {},
      apisHolder::unload);
  }

  @Test
  void whenGetBrokerEntityControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
    assertAuthenticationShouldBeSetInThreadSafeMode(
      accessToken -> apisHolder.getBrokerEntityControllerApi(accessToken)
        .crudGetBroker("1"),
      new ParameterizedTypeReference<>() {},
      apisHolder::unload);
  }

  @Test
  void whenGetBrokerSearchControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
    assertAuthenticationShouldBeSetInThreadSafeMode(
      accessToken -> apisHolder.getBrokerSearchControllerApi(accessToken)
        .crudBrokersFindBrokerByExternalId("externalId"),
      new ParameterizedTypeReference<>() {},
      apisHolder::unload);
  }

  @Test
  void whenGetBrokerConfigurationEntityControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
    assertAuthenticationShouldBeSetInThreadSafeMode(
      accessToken -> apisHolder.getBrokerConfigurationEntityControllerApi(accessToken)
        .crudGetBrokerconfiguration("1"),
      new ParameterizedTypeReference<>() {},
      apisHolder::unload);
  }
}
