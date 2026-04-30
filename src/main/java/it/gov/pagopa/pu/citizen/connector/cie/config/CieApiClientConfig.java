package it.gov.pagopa.pu.citizen.connector.cie.config;

import it.gov.pagopa.pu.citizen.config.rest.ApiClientConfig;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rest.cie")
@SuperBuilder
@NoArgsConstructor
public class CieApiClientConfig extends ApiClientConfig {
}
