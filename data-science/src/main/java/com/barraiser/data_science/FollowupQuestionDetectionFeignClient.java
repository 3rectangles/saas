/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.data_science;

import com.barraiser.data_science.DTO.FollowupQuestionDetectionRequestDTO;
import com.barraiser.data_science.DTO.FollowupQuestionDetectionResponseDTO;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "followup-question-detection-feign-client", url = "${followup-question-detection-url}")
public interface FollowupQuestionDetectionFeignClient {
	@PostMapping()
	@Headers(value = "Content-Type: application/json")
	ResponseEntity<FollowupQuestionDetectionResponseDTO> getFollowupQuestionDetection(
			@RequestBody final FollowupQuestionDetectionRequestDTO requestBody);
}
