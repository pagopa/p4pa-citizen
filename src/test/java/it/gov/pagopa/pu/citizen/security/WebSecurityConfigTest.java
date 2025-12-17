package it.gov.pagopa.pu.citizen.security;

import it.gov.pagopa.pu.citizen.controller.OrganizationController;
import it.gov.pagopa.pu.citizen.service.AuthorizationService;
import it.gov.pagopa.pu.citizen.service.organization.OrganizationRetrieverService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {OrganizationController.class}, includeFilters = @ComponentScan.Filter(
  type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class))
@Import(WebSecurityConfig.class)
class WebSecurityConfigTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private OrganizationRetrieverService organizationRetrieverService;

  @MockitoBean
  private AuthorizationService authorizationServiceMock;

  @Test
  void givenURLWhenWithoutAccessTokenThenReturn403() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/notFound"))
      .andExpect(status().is4xxClientError());
  }
}

