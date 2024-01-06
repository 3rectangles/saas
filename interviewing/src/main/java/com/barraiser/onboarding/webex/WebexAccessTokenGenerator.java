/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.webex;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.barraiser.onboarding.webex.dto.GetWebexAccessTokenRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.Instant;
import java.util.Base64;

@Component
@Log4j2
@RequiredArgsConstructor
// TODO: remove hard codings
public class WebexAccessTokenGenerator {
	private final WebexClient webexClient;

	public String generateCandidateAccessToken(final String userId) {
		return this.generateGuestAccessToken(
				userId,
				"Candidate",
				"Y2lzY29zcGFyazovL3VybjpURUFNOnVzLXdlc3QtMl9yL09SR0FOSVpBVElPTi9kMTQxZTM5Ny04YTBlLTRmNWEtYjhlNy0yYWU3ZDMxYTc5MTA",
				"vGWpOeagNLaxtbf+pGl6D+UTgD1wfdS3X8xfhjSckoQ=");
	}

	public String generateInterviewerAccessToken(final String userId) {
		return this.generateGuestAccessToken(
				userId,
				"Interviewer",
				"Y2lzY29zcGFyazovL3VybjpURUFNOnVzLXdlc3QtMl9yL09SR0FOSVpBVElPTi84ZDNmYjFlMC02NjU1LTQzNjEtYjEyZC1mNmQ5NDFkOTBlNzc",
				"YadmDsxYOvBycA10YgFc6yuOIbeAnEVwlqteCuIC4ZU=");
	}

	private String generateGuestAccessToken(final String userId, final String name, final String issuerId,
			final String secret) {
		final String jwt = JWT.create()
				.withIssuer(issuerId)
				.withSubject(userId)
				.withClaim("name", name)
				.withExpiresAt(Date.from(Instant.now().plusSeconds(120)))
				.sign(Algorithm.HMAC256(Base64.getDecoder().decode(secret)));

		return this.webexClient.getGuestAccessToken(this.getAuthHeader(jwt)).getToken();
	}

	private String getAuthHeader(final String token) {
		return String.format("Bearer %s", token);
	}

	private String generateAdminAccessToken() {
		final String accessToken = this.webexClient.getAccessToken(GetWebexAccessTokenRequestDTO.builder()
				.grantType("refresh_token")
				.clientId("C7d513c6cde2a975ccd3e265358eac800421517b41ca3fa8e2986b1542d7c8582")
				.clientSecret("d6b3046896069b1615739e3b7b816c50c0481ad12a8bf91a1fb0ce2794d94318")
				.refreshToken(
						"M2Q4NzE5NzktNDgwMS00Yjc0LWIwOTktM2QwMThiYWUyYWQyYTk2NGU2ZWMtZmQz_P0A1_60cc4980-5de6-431d-8e7c-f0af81e00608")
				.build()).getAccessToken();
		return accessToken;
	}

	public String getAdminAuthHeader() {
		return this.getAuthHeader(this.generateAdminAccessToken());
	}
}
