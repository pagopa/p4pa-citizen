package it.gov.pagopa.pu.citizen.connector.auth;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class AuthnServiceImpl implements AuthnService {

    private final AuthAccessTokenRetriever accessTokenRetriever;

    public AuthnServiceImpl(AuthAccessTokenRetriever accessTokenRetriever) {
        this.accessTokenRetriever = accessTokenRetriever;
    }

    @Override
    public String getAccessToken(String orgIpaCode) {
        return accessTokenRetriever.getAccessToken(orgIpaCode).getAccessToken();
    }
}
