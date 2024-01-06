/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth.sso;

import com.barraiser.onboarding.auth.sso.dto.SignInWithSSODTO;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Log4j2
@RequiredArgsConstructor
public class SSOService {
	private final List<SSOConfig> configs;
	private final List<EmailExtractorFromIdToken> emailExtractors;
	private final SSOAuthenticator authenticator;

	public List<ResponseCookie> signIn(final SignInWithSSODTO signInDTO) throws IOException {
		final SSOConfig config = this.configs.stream().filter(c -> c.source().equals(signInDTO.getSource())).findFirst()
				.get();
		final AuthorizationCodeFlow flow = this.initializeFlow(config);
		final TokenResponse tokens = flow.newTokenRequest(signInDTO.getCode())
				.setRedirectUri(signInDTO.getRedirectURI()).execute();
		final String email = this.getEmailFromIdToken(tokens.get("id_token").toString(), signInDTO.getSource());
		if (signInDTO.getEvaluationIdToSignUpPartnerFor() != null) {
			this.authenticator.signUpPartnerForBGS(email, signInDTO.getEvaluationIdToSignUpPartnerFor());
		}
		return this.authenticator.authenticateSSOUser(signInDTO.getSource(), email, tokens.getRefreshToken());
	}

	private AuthorizationCodeFlow initializeFlow(final SSOConfig config) {
		return new AuthorizationCodeFlow.Builder(BearerToken.authorizationHeaderAccessMethod(),
				new NetHttpTransport(),
				new JacksonFactory(),
				new GenericUrl(config.getTokenEndpoint()),
				new BasicAuthentication(config.getClientId(), config.getClientSecret()),
				config.getClientId(),
				config.getAuthorizationEndpoint())
						.build();
	}

	private String getEmailFromIdToken(final String idToken, final String source) {
		final EmailExtractorFromIdToken emailExtractor = this.emailExtractors.stream()
				.filter(e -> e.source().equals(source)).findFirst().get();
		return emailExtractor.getEmailFromIdToken(idToken);
	}
}
