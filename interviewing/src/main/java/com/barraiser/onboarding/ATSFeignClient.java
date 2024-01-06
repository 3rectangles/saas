/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding;

import com.barraiser.ats_integrations.dto.PostATSNoteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ats-feign-client", url = "http://localhost:5000")
public interface ATSFeignClient {

	String SERVICE_CONTEXT_PATH = "/ats";

	@PostMapping(value = SERVICE_CONTEXT_PATH + "/post-note")
	ResponseEntity<Void> postNote(
			@RequestBody final PostATSNoteDTO input);
}
