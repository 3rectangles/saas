/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.data_science;

import com.barraiser.data_science.DTO.QuestionCategoryPredictionRequestDTO;
import com.barraiser.data_science.DTO.QuestionCategoryPredictionResponseDTO;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "question-category-prediction-feign-client", url = "${question-category-prediction-url}")
public interface QuestionCategoryPredictionFeignClient {
	@PostMapping()
	@Headers(value = "Content-Type: application/json")
	ResponseEntity<QuestionCategoryPredictionResponseDTO> getQuestionCategoryPrediction(
			@RequestBody final QuestionCategoryPredictionRequestDTO requestBody);
}
