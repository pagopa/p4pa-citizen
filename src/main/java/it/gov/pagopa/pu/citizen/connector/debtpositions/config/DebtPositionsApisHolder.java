package it.gov.pagopa.pu.citizen.connector.debtpositions.config;

import it.gov.pagopa.pu.citizen.config.rest.RestTemplateConfig;
import it.gov.pagopa.pu.debtpositions.controller.ApiClient;
import it.gov.pagopa.pu.debtpositions.controller.BaseApi;
import it.gov.pagopa.pu.debtpositions.controller.generated.*;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DebtPositionsApisHolder {

  private final DebtPositionTypeOrgSearchControllerApi debtPositionTypeOrgSearchControllerApi;
  private final DebtPositionTypeOrgWithActiveSpontaneousCountSearchControllerApi debtPositionTypeOrgWithActiveSpontaneousCountSearchControllerApi;
  private final DebtPositionTypeOrgEntityControllerApi debtPositionTypeOrgEntityControllerApi;
  private final SpontaneousFormEntityControllerApi spontaneousFormEntityControllerApi;
  private final DebtPositionApi debtPositionApi;

  private final ThreadLocal<String> bearerTokenHolder = new ThreadLocal<>();

  public DebtPositionsApisHolder(
    DebtPositionsApiClientConfig clientConfig,
    RestTemplateBuilder restTemplateBuilder
  ) {
    RestTemplate restTemplate = restTemplateBuilder.build();
    ApiClient apiClient = new ApiClient(restTemplate);
    apiClient.setBasePath(clientConfig.getBaseUrl());
    apiClient.setBearerToken(bearerTokenHolder::get);
    apiClient.setMaxAttemptsForRetry(Math.max(1, clientConfig.getMaxAttempts()));
    apiClient.setWaitTimeMillis(clientConfig.getWaitTimeMillis());
    if (clientConfig.isPrintBodyWhenError()) {
      restTemplate.setErrorHandler(RestTemplateConfig.bodyPrinterWhenError("DEBT-POSITIONS"));
    }

    this.debtPositionTypeOrgSearchControllerApi = new DebtPositionTypeOrgSearchControllerApi(apiClient);
    this.debtPositionTypeOrgWithActiveSpontaneousCountSearchControllerApi = new DebtPositionTypeOrgWithActiveSpontaneousCountSearchControllerApi(apiClient);
    this.debtPositionTypeOrgEntityControllerApi = new DebtPositionTypeOrgEntityControllerApi(apiClient);
    this.spontaneousFormEntityControllerApi = new SpontaneousFormEntityControllerApi(apiClient);
    this.debtPositionApi = new DebtPositionApi(apiClient);
  }

  @PreDestroy
  public void unload() {
    bearerTokenHolder.remove();
  }

  /** It will return a {@link DebtPositionTypeOrgSearchControllerApi} instrumented with the provided accessToken. Use null if auth is not required */
  public DebtPositionTypeOrgSearchControllerApi getDebtPositionTypeOrgSearchControllerApi(String accessToken) {
    return getApi(accessToken, debtPositionTypeOrgSearchControllerApi);
  }

  /** It will return a {@link DebtPositionTypeOrgWithActiveSpontaneousCountSearchControllerApi} instrumented with the provided accessToken. Use null if auth is not required */
  public DebtPositionTypeOrgWithActiveSpontaneousCountSearchControllerApi getDebtPositionTypeOrgWithActiveSpontaneousCountSearchControllerApi(String accessToken) {
    return getApi(accessToken, debtPositionTypeOrgWithActiveSpontaneousCountSearchControllerApi);
  }

  /**
   * It will return a {@link DebtPositionTypeOrgEntityControllerApi} instrumented with the provided accessToken. Use null if auth is not required
   */
  public DebtPositionTypeOrgEntityControllerApi getDebtPositionTypeOrgEntityControllerApi(String accessToken) {
    return getApi(accessToken, debtPositionTypeOrgEntityControllerApi);
  }

  /**
   * It will return a {@link SpontaneousFormEntityControllerApi} instrumented with the provided accessToken. Use null if auth is not required
   */
  public SpontaneousFormEntityControllerApi getSpontaneousFormEntityControllerApi(String accessToken) {
    return getApi(accessToken, spontaneousFormEntityControllerApi);
  }

  /**
   * It will return a {@link DebtPositionApi} instrumented with the provided accessToken. Use null if auth is not required
   */
  public DebtPositionApi getDebtPositionApi(String accessToken) {
    return getApi(accessToken, debtPositionApi);
  }

  private <T extends BaseApi> T getApi(String accessToken, T api) {
    bearerTokenHolder.set(accessToken);
    return api;
  }
}
