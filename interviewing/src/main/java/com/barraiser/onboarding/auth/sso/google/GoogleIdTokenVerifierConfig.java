package com.barraiser.onboarding.auth.sso.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class GoogleIdTokenVerifierConfig {
    @Bean
    public GoogleIdTokenVerifier getGoogleIdTokenVerifier(final GoogleOAuthConfig config) {
        return new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
            .setAudience(Collections.singletonList(config.getClientId()))
            .build();
    }
}
