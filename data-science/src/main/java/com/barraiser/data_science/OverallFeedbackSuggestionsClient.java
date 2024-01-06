/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.data_science;

import com.barraiser.data_science.DTO.OverallFeedbackSuggestionRequestDTO;
import com.barraiser.data_science.DTO.OverallFeedbackSuggestionResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "overall-feedback-suggestions-client", url = "${overall-feedback-suggestions-url}")
public interface OverallFeedbackSuggestionsClient {
	@PostMapping(value = "/overall_feedback/strengths/v1", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<OverallFeedbackSuggestionResponseDTO> fetchStrengthFeedbackSuggestion(
			@RequestBody final List<OverallFeedbackSuggestionRequestDTO> overallFeedbackRequestDTOList);

	@PostMapping(value = "/overall_feedback/area_of_improvement/v1", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<OverallFeedbackSuggestionResponseDTO> fetchAresOfImprovementsFeedbackSuggestion(
			@RequestBody final List<OverallFeedbackSuggestionRequestDTO> overallFeedbackRequestDTOList);
}
