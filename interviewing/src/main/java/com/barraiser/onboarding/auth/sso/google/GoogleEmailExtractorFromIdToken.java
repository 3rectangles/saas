package com.barraiser.onboarding.auth.sso.google;

import com.barraiser.onboarding.auth.AuthenticationException;
import com.barraiser.onboarding.auth.sso.EmailExtractorFromIdToken;
import com.barraiser.onboarding.auth.sso.SSOProvider;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Component
@Log
@RequiredArgsConstructor
public class GoogleEmailExtractorFromIdToken implements EmailExtractorFromIdToken {
    private final GoogleIdTokenVerifier verifier;

    @Override
    public String source() {
        return SSOProvider.GOOGLE;
    }

    @Override
    public String getEmailFromIdToken(String idTokenString) {
        final GoogleIdToken idToken;
        try {
            idToken = verifier.verify(idTokenString);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        if(idToken == null) {
            throw new AuthenticationException("invalid google id token");
        }
        final Boolean emailVerified = idToken.getPayload().getEmailVerified();
        if(!emailVerified) {
            throw new AuthenticationException("Google account email is not verified : " + idToken.getPayload().getEmail());
        }
        return idToken.getPayload().getEmail();
    }
}
