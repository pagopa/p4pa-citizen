package it.gov.pagopa.pu.citizen.security;

import it.gov.pagopa.pu.auth.dto.generated.UserInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URI;
import java.util.Collection;

public class SecurityUtilsTest {

  @AfterEach
  void clear(){
    clearSecurityContext();
    RequestContextHolder.resetRequestAttributes();
  }

  public static void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  public static void configureSecurityContext(UserInfo userInfo) {
    configureSecurityContext("TOKENHEADER.TOKENPAYLOAD.TOKENDIGEST", userInfo);
  }

  public static void configureSecurityContext(String token, UserInfo userInfo) {
    Collection<? extends GrantedAuthority> authorities = null;
    if (userInfo.getOrganizationAccess() != null) {
      authorities = userInfo.getOrganizations().stream()
        .filter(o -> userInfo.getOrganizationAccess().equals(o.getOrganizationIpaCode()))
        .flatMap(r -> r.getRoles().stream())
        .map(SimpleGrantedAuthority::new)
        .toList();
    }

    SecurityContextHolder.setContext(new SecurityContextImpl(new UsernamePasswordAuthenticationToken(userInfo, token, authorities)));
  }

//region test getLoggedUser
  @Test
  void whenGetLoggedUserThenReturnIt() {
    // Given
    String expectedMappedExternalUserId = "USERID";
    UserInfo expectedUserInfo = new UserInfo();
    expectedUserInfo.setMappedExternalUserId(expectedMappedExternalUserId);
    configureSecurityContext(expectedUserInfo);

    // When
    UserInfo result = SecurityUtils.getLoggedUser();

    // Then
    Assertions.assertSame(expectedUserInfo, result);
    Assertions.assertEquals(expectedMappedExternalUserId, expectedUserInfo.getMappedExternalUserId());
  }

  @Test
  void givenPuSystemUserAndUserIdWhenGetLoggedUserThenReturnIt() {
    // Given
    String expectedMappedExternalUserId = "ANOTHERUSER";
    UserInfo expectedUserInfo = new UserInfo();
    expectedUserInfo.setMappedExternalUserId(SecurityUtils.SYSTEM_USERID_PREFIX + "ORGIPACODE");
    configureSecurityContext(expectedUserInfo);
    configureXUserIdHeader(expectedMappedExternalUserId);

    // When
    UserInfo result = SecurityUtils.getLoggedUser();

    // Then
    Assertions.assertSame(expectedUserInfo, result);
    Assertions.assertEquals(expectedMappedExternalUserId, expectedUserInfo.getMappedExternalUserId());
  }

  public static void configureXUserIdHeader(String expectedMappedExternalUserId) {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader(SecurityUtils.HEADER_USER_ID, expectedMappedExternalUserId);
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
  }

  @Test
  void givenPuSystemUserAndNotUserIdWhenGetLoggedUserThenReturnIt() {
    // Given
    String expectedMappedExternalUserId = SecurityUtils.SYSTEM_USERID_PREFIX + "ORGIPACODE";
    UserInfo expectedUserInfo = new UserInfo();
    expectedUserInfo.setMappedExternalUserId(expectedMappedExternalUserId);
    configureSecurityContext(expectedUserInfo);
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));

    // When
    UserInfo result = SecurityUtils.getLoggedUser();

    // Then
    Assertions.assertSame(expectedUserInfo, result);
    Assertions.assertEquals(expectedMappedExternalUserId, expectedUserInfo.getMappedExternalUserId());
  }

  @Test
  void givenPuSystemUserAndNotHttpContextWhenGetLoggedUserThenReturnIt() {
    // Given
    String expectedMappedExternalUserId = SecurityUtils.SYSTEM_USERID_PREFIX + "ORGIPACODE";
    UserInfo expectedUserInfo = new UserInfo();
    expectedUserInfo.setMappedExternalUserId(expectedMappedExternalUserId);
    configureSecurityContext(expectedUserInfo);

    // When
    UserInfo result = SecurityUtils.getLoggedUser();

    // Then
    Assertions.assertSame(expectedUserInfo, result);
    Assertions.assertEquals(expectedMappedExternalUserId, expectedUserInfo.getMappedExternalUserId());
  }
//endregion

  @Test
  void givenEmptySecurityContextThenGetUserInfo(){
    // Given
    SecurityContextHolder.clearContext();
    // When
    UserInfo result = SecurityUtils.getLoggedUser();
    // Then
    Assertions.assertNull(result);
  }

  @Test
  void givenSecurityContextThenGetLoggedUserAccessToken(){
    // Given
    UserInfo expectedUserInfo = new UserInfo();
    configureSecurityContext("token", expectedUserInfo);
    // When
    String result = SecurityUtils.getAccessToken();
    // Then
    Assertions.assertEquals("token",result);
  }

  @Test
  void givenUriWhenRemovePiiFromURIThenOk(){
    String result = SecurityUtils.removePiiFromURI(URI.create("https://host/path?param1=PII&param2=noPII"));
    Assertions.assertEquals("https://host/path?param1=***&param2=***", result);
  }

  @Test
  void givenNullUriWhenRemovePiiFromURIThenOk(){
    Assertions.assertNull(SecurityUtils.removePiiFromURI(null));
  }

}
