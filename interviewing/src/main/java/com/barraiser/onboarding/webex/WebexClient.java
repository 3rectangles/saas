/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.webex;

import com.barraiser.onboarding.webex.dto.CreateWebexMeetingRequestDTO;
import com.barraiser.onboarding.webex.dto.CreateWebexMeetingResponseDTO;
import com.barraiser.onboarding.webex.dto.GetWebexAccessTokenRequestDTO;
import com.barraiser.onboarding.webex.dto.GetWebexAccessTokenResponseDTO;
import com.barraiser.onboarding.webex.dto.GetWebexGuestAccessTokenResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "webex-client", url = "https://webexapis.com")
public interface WebexClient {
	@PostMapping("/v1/meetings")
	CreateWebexMeetingResponseDTO createMeeting(@RequestBody CreateWebexMeetingRequestDTO request,
			@RequestHeader("Authorization") final String token);

	@PostMapping("/v1/jwt/login")
	GetWebexGuestAccessTokenResponseDTO getGuestAccessToken(@RequestHeader("Authorization") final String token);

	@PostMapping("/v1/access_token")
	GetWebexAccessTokenResponseDTO getAccessToken(@RequestBody final GetWebexAccessTokenRequestDTO request);
}
