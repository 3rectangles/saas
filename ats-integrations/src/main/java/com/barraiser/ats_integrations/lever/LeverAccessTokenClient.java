/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.lever;

import com.barraiser.ats_integrations.lever.requests.LeverAccessTokenRequestBody;
import com.barraiser.ats_integrations.lever.requests.LeverRefreshAccessTokenRequestBody;
import com.barraiser.ats_integrations.lever.responses.LeverAccessTokenResponseBody;
import com.barraiser.ats_integrations.lever.responses.LeverRefreshAccessTokenResponseBody;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "lever-access-token-client", url = "${lever.access-token-client}")
public interface LeverAccessTokenClient {
	@PostMapping
	@Headers("content-type: application/x-www-form-urlencoded")
	ResponseEntity<LeverAccessTokenResponseBody> requestAccessToken(
			@RequestBody LeverAccessTokenRequestBody request);

	@PostMapping
	@Headers("content-type: application/x-www-form-urlencoded")
	ResponseEntity<LeverRefreshAccessTokenResponseBody> refreshAccessToken(
			@RequestBody LeverRefreshAccessTokenRequestBody request);
}
