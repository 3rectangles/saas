/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.data_science;

import com.barraiser.common.requests.CancellationPredictionRequest;
import com.barraiser.common.responses.CancellationPredictionResponse;

import feign.Headers;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "cancellation-predication-feign-client", url = "${cancellation-prediction-api-url}")
public interface CancellationPredictionFeignClient {

	@PostMapping()
	@Headers(value = "Content-Type: application/json")
	CancellationPredictionResponse getCancellationProbability(
			@RequestBody CancellationPredictionRequest cancellationPredictionRequest);
}
