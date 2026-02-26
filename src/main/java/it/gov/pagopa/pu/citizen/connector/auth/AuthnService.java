package it.gov.pagopa.pu.citizen.connector.auth;

public interface AuthnService {
  String getAccessToken(String orgIpaCode);
}
