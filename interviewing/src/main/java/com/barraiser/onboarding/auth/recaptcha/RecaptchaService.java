/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth.recaptcha;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;

@Log4j2
@AllArgsConstructor
@Component
public class RecaptchaService {

	private final StaticAppConfigValues staticAppConfigValues;
	private final String GOOGLE_RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";
	private final ObjectMapper objectMapper;

	public Boolean isRecaptchaValid(String responseToken) throws URISyntaxException, IOException {

		final String params = "?secret=" + this.staticAppConfigValues.getRecaptchaSecretKey() + "&response="
				+ responseToken;
		final URIBuilder uriBuilder = new URIBuilder(GOOGLE_RECAPTCHA_VERIFY_URL + params);
		final HttpPost httpPost = new HttpPost(uriBuilder.build());
		final HttpClient client = HttpClientBuilder.create().build();
		final HttpResponse response = client.execute(httpPost);
		final RecaptchaChallengeResponse recaptchaChallengeResponse = this.objectMapper.readValue(
				response.getEntity().getContent(),
				RecaptchaChallengeResponse.class);

		if (recaptchaChallengeResponse.isSuccess()) {
			log.info("Score of recaptcha:{}", recaptchaChallengeResponse.getScore());
			return true;
		}
		return false;
	}
}
