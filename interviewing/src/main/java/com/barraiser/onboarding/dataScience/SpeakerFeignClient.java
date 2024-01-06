/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dataScience;

import org.json.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import feign.Headers;
import org.springframework.beans.factory.annotation.Value;

@FeignClient(value = "speaker", url = "${speaker.url}")
public interface SpeakerFeignClient {
	@PostMapping(value = "/speaker_detection/simple_v1", produces = "application/json", consumes = "application/json")
	String detectSpeakers(@RequestBody Object requestBody);
}
