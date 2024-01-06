/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.linkedInService;

import com.barraiser.onboarding.linkedInService.dto.LinkedinPostResponseDTO;
import com.barraiser.onboarding.linkedInService.dto.LinkedinShareRequestDTO;
import com.barraiser.onboarding.linkedInService.dto.LinkedinShareResponseDTO;
import com.barraiser.onboarding.linkedInService.dto.LinkedinUrnResponseDTO;
import feign.Headers;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "linkedin-share-client", url = "https://api.linkedin.com/", configuration = LinkedinClient.Configuration.class)
public interface LinkedinClient {

	@PostMapping(value = "oauth/v2/accessToken", consumes = "application/x-www-form-urlencoded")
	LinkedinShareResponseDTO getAccessToken(
			@RequestBody LinkedinShareRequestDTO linkedinShareRequest);

	@PostMapping(value = "v2/shares", headers = { "Content-Type=application/json", "Accept=application/json" })
	@Headers(value = "Content-Type: application/json, Accept: application/json")
	LinkedinPostResponseDTO sharePost(
			@RequestHeader("Authorization") String accessToken, @RequestBody String content);

	@GetMapping(value = "v2/me")
	@ResponseBody
	LinkedinUrnResponseDTO getUrn(@RequestHeader("Authorization") String accessToken);

	class Configuration {
		@Bean
		Encoder feignFormEncoder(final ObjectFactory<HttpMessageConverters> converters) {
			return new SpringFormEncoder(new SpringEncoder(converters));
		}
	}
}
