package it.gov.pagopa.pu.citizen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.webmvc.autoconfigure.error.ErrorMvcAutoConfiguration;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class, ErrorMvcAutoConfiguration.class})
public class PuCitizenApplication {

	public static void main(String[] args) {
		SpringApplication.run(PuCitizenApplication.class, args);
	}

}
