/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.zoom;

import com.barraiser.onboarding.config.ZoomOAuthResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "zoom-auth-client", url = "https://zoom.us/oauth")
public interface ZoomAuthClient {

	@PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	ZoomOAuthResponse getToken(
			@RequestHeader("Authorization") String authorizationHeader,
			@RequestParam("grant_type") String grantType,
			@RequestParam("account_id") String accountId);

}
