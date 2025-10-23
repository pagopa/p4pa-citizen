package it.gov.pagopa.pu.citizen.connector.pagopapayments.config;

import it.gov.pagopa.pu.citizen.config.rest.ApiClientConfig;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rest.pagopa-payments")
@SuperBuilder
@NoArgsConstructor
public class PagoPAPaymentsApiClientConfig extends ApiClientConfig {
}
