/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.passthrough;

import com.barraiser.common.responses.PassthroughRequest;
import com.barraiser.common.responses.PassthroughResponse;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.AuthenticationException;
import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@Log4j2
@AllArgsConstructor
public class PassthroughController {

	private static final String CONTENT_TYPE = "Content-Type";
	private static final String REQUEST_PROPERTY_APP_JSON = "application/json";
	private static final String ERROR_STATUS_CODE = "400";
	private static final String HOST_URL_MAPPING_KEY = "passthrough_url_host_mapping";
	private final DynamicAppConfigProperties dynamicAppConfigProperties;

	@PostMapping(value = "/passthrough", produces = "application/json", consumes = "application/json")
	public PassthroughResponse passthrough(
			@RequestBody final PassthroughRequest passthroughRequest,
			@RequestAttribute(name = "loggedInUser") AuthenticatedUser user) {

		if (user == null) {
			throw new AuthenticationException("No authenticated user found");
		}

		try {
			// Fetching Host and URL regex mappings from DynamoDB
			final String queryHostValuesJson = dynamicAppConfigProperties.getString(HOST_URL_MAPPING_KEY);
			final TypeReference<List<Map<String, String>>> typeReference = new TypeReference<>() {
			};
			final List<Map<String, String>> queryHostValues = dynamicAppConfigProperties
					.getObjectFromJson(queryHostValuesJson, typeReference);

			// Constructing the URL and sending the request
			URL url = new URL(getHost(queryHostValues, passthroughRequest.getRequestPath())
					+ passthroughRequest.getRequestPath());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(passthroughRequest.getRequestMethod());
			connection.setRequestProperty(CONTENT_TYPE, REQUEST_PROPERTY_APP_JSON);
			connection.setDoOutput(true);

			// Fetching the output
			try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
				outputStream.writeBytes(passthroughRequest.getRequestBody());
				outputStream.flush();
			}

			int responseCode = connection.getResponseCode();

			try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
				String inputLine;
				StringBuilder response = new StringBuilder();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}

				return PassthroughResponse.builder()
						.remoteResponse(response.toString())
						.remoteStatus(String.valueOf(responseCode))
						.inputPath(url.getPath())
						.build();
			}
		} catch (IOException e) {
			log.error("Error in calling passthrough: ", e, e);
		}

		return PassthroughResponse.builder()
				.remoteStatus(ERROR_STATUS_CODE)
				.inputPath(passthroughRequest.getRequestPath())
				.build();

	}

	private String getHost(final List<Map<String, String>> urlHostMapping, final String requestUrl) {
		for (Map<String, String> mapping : urlHostMapping) {
			final String urlPattern = mapping.keySet().iterator().next();
			final String hostValue = mapping.get(urlPattern);

			if (this.isMatchingRegex(urlPattern, requestUrl)) {
				return hostValue;
			}
		}
		return null;
	}

	private Boolean isMatchingRegex(final String regexValue, final String value) {
		final Pattern patternString = Pattern.compile(regexValue, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		Matcher matcher = patternString.matcher(value);
		if (matcher.find()) {
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

}
