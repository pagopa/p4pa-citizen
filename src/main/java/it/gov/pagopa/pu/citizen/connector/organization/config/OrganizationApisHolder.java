package it.gov.pagopa.pu.citizen.connector.organization.config;


import it.gov.pagopa.pu.citizen.config.rest.RestTemplateConfig;
import it.gov.pagopa.pu.organization.controller.ApiClient;
import it.gov.pagopa.pu.organization.controller.BaseApi;
import it.gov.pagopa.pu.organization.controller.generated.BrokerEntityControllerApi;
import it.gov.pagopa.pu.organization.controller.generated.OrganizationEntityControllerApi;
import it.gov.pagopa.pu.organization.controller.generated.OrganizationSearchControllerApi;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrganizationApisHolder {
  private final OrganizationSearchControllerApi organizationSearchControllerApi;
  private final OrganizationEntityControllerApi organizationEntityControllerApi;
  private final BrokerEntityControllerApi brokerEntityControllerApi;
  private final ThreadLocal<String> bearerTokenHolder = new ThreadLocal<>();

  public OrganizationApisHolder(OrganizationApiClientConfig clientConfig, RestTemplateBuilder restTemplateBuilder){

    RestTemplate restTemplate = restTemplateBuilder.build();
    ApiClient apiClient = new ApiClient(restTemplate);
    apiClient.setBasePath(clientConfig.getBaseUrl());
    apiClient.setBearerToken(bearerTokenHolder::get);
    apiClient.setMaxAttemptsForRetry(Math.max(1, clientConfig.getMaxAttempts()));
    apiClient.setWaitTimeMillis(clientConfig.getWaitTimeMillis());
    if (clientConfig.isPrintBodyWhenError()) {
      restTemplate.setErrorHandler(RestTemplateConfig.bodyPrinterWhenError("ORGANIZATION"));
    }

    this.organizationSearchControllerApi = new OrganizationSearchControllerApi(apiClient);
    this.organizationEntityControllerApi = new OrganizationEntityControllerApi(apiClient);
    this.brokerEntityControllerApi = new BrokerEntityControllerApi(apiClient);
  }

  @PreDestroy
  public void unload(){
    bearerTokenHolder.remove();
  }

  /** It will return a {@link OrganizationSearchControllerApi} instrumented with the provided accessToken. Use null if auth is not required */
  public OrganizationSearchControllerApi getOrganizationSearchControllerApi(String accessToken){
    return getApi(accessToken, organizationSearchControllerApi);
  }

  /** It will return a {@link OrganizationEntityControllerApi} instrumented with the provided accessToken. Use null if auth is not required */
  public OrganizationEntityControllerApi getOrganizationEntityControllerApi(String accessToken){
    return getApi(accessToken,organizationEntityControllerApi);
  }

  /** It will return a {@link BrokerEntityControllerApi} instrumented with the provided accessToken. Use null if auth is not required */
  public BrokerEntityControllerApi getBrokerEntityControllerApi(String accessToken){
    return getApi(accessToken,brokerEntityControllerApi);
  }


  private <T extends BaseApi> T getApi(String accessToken, T api) {
    bearerTokenHolder.set(accessToken);
    return api;
  }

}
