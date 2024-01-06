/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user.resume.client;

import com.barraiser.onboarding.user.resume.dto.ResumeRedactionRequest;
import com.barraiser.onboarding.user.resume.dto.ResumeRedactionResponse;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "resume-redaction-feign-client", url = "https://lrgdaq3tca.execute-api.ap-south-1.amazonaws.com")
public interface ResumeRedactionFeignClient {
	@PostMapping(value = "/prod")
	ResumeRedactionResponse redactResume(@RequestBody ResumeRedactionRequest updateBody);
}
