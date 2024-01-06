/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.zoom;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.barraiser.onboarding.config.ZoomNewConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Log4j2
public class ZoomSignatureGenerator {
	@Autowired
	@Qualifier("zoomNewConfig")
	private final ZoomNewConfig newConfig;

	private String createSignature(final String meetingId, final Boolean isHost) {
		Algorithm algorithm = Algorithm.HMAC256(newConfig.getApiSecret());

		long currentTimeSeconds = Instant.now().getEpochSecond();
		long expirationTimeSeconds = currentTimeSeconds + 60 * 60 * 2;

		Map<String, Object> headerClaims = new HashMap<>();
		headerClaims.put("alg", "HS256");
		headerClaims.put("typ", "JWT");

		JWTCreator.Builder builder = JWT.create()
				.withHeader(headerClaims)
				.withClaim("appKey", newConfig.getApiKey())
				.withClaim("sdkKey", newConfig.getApiKey())
				.withIssuer(newConfig.getApiKey())
				.withClaim("mn", meetingId)
				.withClaim("role", isHost ? 1 : 0)
				.withClaim("iat", currentTimeSeconds)
				.withClaim("exp", expirationTimeSeconds)
				.withClaim("tokenExp", expirationTimeSeconds);

		return builder.sign(algorithm);

	}

	public String createSignatureForParticipant(final String meetingId) {
		return this.createSignature(meetingId, false);
	}

	public String createSignatureForHost(final String meetingId) {
		return this.createSignature(meetingId, true);
	}
}
