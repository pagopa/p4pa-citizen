package it.gov.pagopa.pu.citizen.security;

import it.gov.pagopa.pu.auth.dto.generated.UserInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URI;

public class SecurityUtils {

  private SecurityUtils() {
  }

  public static final String SYSTEM_USERID_PREFIX = "WS_USER-piattaforma-unitaria_";
  public static final String HEADER_USER_ID = "X-user-id";

  /**
   * It will return user's session data from ThreadLocal
   */
  public static UserInfo getLoggedUser() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      Object principal = authentication.getPrincipal();
      if (principal instanceof UserInfo userInfo) {
        userInfo.setMappedExternalUserId(resolvePuSystemUser(userInfo.getMappedExternalUserId()));
        return userInfo;
      }
    }
    return null;
  }

  public static String resolvePuSystemUser(String mappedExternalUserId) {
    if(mappedExternalUserId != null && mappedExternalUserId.startsWith(SYSTEM_USERID_PREFIX) && RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes servletRequestAttributes){
      HttpServletRequest requestAttributes = servletRequestAttributes.getRequest();
      mappedExternalUserId = ObjectUtils.firstNonNull(requestAttributes.getHeader(HEADER_USER_ID), mappedExternalUserId);
    }
    return mappedExternalUserId;
  }

  public static String getAccessToken(){
    return SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
  }

  public static String removePiiFromURI(URI uri){
    return uri != null
      ? uri.toString().replaceAll("=[^&]*", "=***")
      : null;
  }

}
