/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth.magicLink;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.URISyntaxException;
import java.sql.Date;
import java.time.Instant;

@Component
@RequiredArgsConstructor
@Log4j2
public class MagicLinkManager {
	private final static String MAGIC_TOKEN_QUERY_PARAMETER = "magic_token";
	private final static String ISSUER = "barraiser";
	private final static String EMAIL_CLAIM = "email";

	private String secret;

	private final AWSSecretsManager awsSecretsManager;

	public String generateMagicUrl(final String urlString, final String email, final Long expirationInSeconds)
			throws URISyntaxException {

		if (email != null) {
			final String jwt = this.generateJWT(email, expirationInSeconds);
			return new URIBuilder(urlString).addParameter(MAGIC_TOKEN_QUERY_PARAMETER, jwt).build().toString();
		} else {
			throw new IllegalArgumentException("Email ID is not present. Magix link will not be generated for user.");
		}
	}

	public String generateJWT(final String email, final Long expirationInSeconds) {
		return JWT.create()
				.withIssuer(ISSUER)
				.withIssuedAt(Date.from(Instant.now()))
				.withExpiresAt(Date.from(Instant.now().plusSeconds(expirationInSeconds)))
				.withClaim(EMAIL_CLAIM, email)
				.sign(Algorithm.HMAC256(this.secret));
	}

	public String verifyMagicToken(final String token) {
		final JWTVerifier verifier = JWT.require(Algorithm.HMAC256(this.secret))
				.withIssuer(ISSUER)
				.withClaimPresence(EMAIL_CLAIM)
				.build();
		final DecodedJWT jwt = verifier.verify(token);

		return jwt.getClaims().get(EMAIL_CLAIM).asString();
	}

	@PostConstruct
	private void init() {
		this.secret = this.awsSecretsManager.getSecretValue(
				new GetSecretValueRequest().withSecretId("MagicLinkSecret")).getSecretString();
	}
}
