package it.gov.pagopa.pu.citizen.connector.debtpositions.config;

import it.gov.pagopa.pu.citizen.config.rest.RestTemplateConfig;
import it.gov.pagopa.pu.debtpositions.controller.ApiClient;
import it.gov.pagopa.pu.debtpositions.controller.BaseApi;
import it.gov.pagopa.pu.debtpositions.controller.generated.*;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DebtPositionsApisHolder {

  private final DebtPositionTypeOrgSearchControllerApi debtPositionTypeOrgSearchControllerApi;
  private final DebtPositionTypeOrgWithActiveSpontaneousCountSearchControllerApi debtPositionTypeOrgWithActiveSpontaneousCountSearchControllerApi;
  private final DebtPositionTypeOrgEntityControllerApi debtPositionTypeOrgEntityControllerApi;
  private final SpontaneousFormEntityControllerApi spontaneousFormEntityControllerApi;
  private final DebtPositionApi debtPositionApi;
  private final ReceiptNoPiiViewSearchControllerApi receiptNoPiiViewSearchControllerApi;
  private final ReceiptApi receiptApi;
  private final ReceiptNoPiiSearchControllerApi receiptNoPiiSearchControllerApi;
  private final InstallmentApi installmentApi;
  private final InstallmentNoPiiSearchControllerApi installmentNoPiiSearchControllerApi;
  private final ReceiptNoPiiEntityControllerApi receiptNoPiiEntityControllerApi;

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
    this.receiptNoPiiViewSearchControllerApi = new ReceiptNoPiiViewSearchControllerApi(apiClient);
    this.receiptApi = new ReceiptApi(apiClient);
    this.receiptNoPiiSearchControllerApi = new ReceiptNoPiiSearchControllerApi(apiClient);
    this.installmentApi = new InstallmentApi(apiClient);
    this.installmentNoPiiSearchControllerApi = new InstallmentNoPiiSearchControllerApi(apiClient);
    this.receiptNoPiiEntityControllerApi = new ReceiptNoPiiEntityControllerApi(apiClient);
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

  /**
   * It will return a {@link ReceiptNoPiiViewSearchControllerApi} instrumented with the provided accessToken. Use null if auth is not required
   */
  public ReceiptNoPiiViewSearchControllerApi getReceiptNoPiiViewSearchControllerApi(String accessToken){
    return getApi(accessToken, receiptNoPiiViewSearchControllerApi);
  }

  /**
   * It will return a {@link ReceiptApi} instrumented with the provided accessToken. Use null if auth is not required
   */
  public ReceiptApi getReceiptApi(String accessToken) {
    return getApi(accessToken, receiptApi);
  }

  /**
   * It will return a {@link ReceiptNoPiiSearchControllerApi} instrumented with the provided accessToken. Use null if auth is not required
   */
  public ReceiptNoPiiSearchControllerApi getReceiptNoPiiSearchControllerApi(String accessToken) {
    return getApi(accessToken, receiptNoPiiSearchControllerApi);
  }

  /**
   * It will return a {@link InstallmentApi} instrumented with the provided accessToken. Use null if auth is not required
   */
  public InstallmentApi getInstallmentApi(String accessToken) {
    return getApi(accessToken, installmentApi);
  }

  /**
   * It will return a {@link InstallmentNoPiiSearchControllerApi} instrumented with the provided accessToken. Use null if auth is not required
   */
  public InstallmentNoPiiSearchControllerApi getInstallmentNoPiiSearchControllerApi(String accessToken) {
    return getApi(accessToken, installmentNoPiiSearchControllerApi);
  }

  /**
   * It will return a {@link ReceiptNoPiiEntityControllerApi} instrumented with the provided accessToken. Use null if auth is not required
   */
  public ReceiptNoPiiEntityControllerApi getReceiptNoPiiEntityControllerApi(String accessToken) {
    return getApi(accessToken, receiptNoPiiEntityControllerApi);
  }

  private <T extends BaseApi> T getApi(String accessToken, T api) {
    bearerTokenHolder.set(accessToken);
    return api;
  }
}
