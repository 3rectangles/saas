/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.data_science;

import com.barraiser.data_science.DTO.FeedbackRecommendationRequestDTO;
import com.barraiser.data_science.DTO.FeedbackRecommendationResponseDTO;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "feedback-recommendation-feign-client", url = "${feedback-recommendation-url}")
public interface FeedbackRecommendationFeignClient {
	@PostMapping()
	@Headers(value = "Content-Type: application/json")
	ResponseEntity<FeedbackRecommendationResponseDTO> getFeedbackRecommendation(
			@RequestBody final FeedbackRecommendationRequestDTO feedbackRecommendationRequestDTO);
}
