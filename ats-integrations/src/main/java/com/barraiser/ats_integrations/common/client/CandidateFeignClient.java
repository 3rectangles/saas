/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.common.client;

import com.barraiser.common.graphql.UserDetailsInput;
import com.barraiser.common.graphql.input.UpdateCandidateInput;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static com.barraiser.common.constants.ServiceConfigurationConstants.INTERVIEWING_SERVICE_CONTEXT_PATH;

@FeignClient(name = "candidate-feign-client", url = "http://localhost:5000")
public interface CandidateFeignClient {

	@GetMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/evaluation/{evaluationId}/getCandidate")
	@Headers(value = "Content-Type: application/json")
	ResponseEntity<String> getCandidateId(
			@PathVariable("evaluationId") final String evaluationId);

	@PutMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/candidateUserDetails")
	@Headers(value = "Content-Type: application/json")
	ResponseEntity<String> updateCandidate(
			@RequestBody UpdateCandidateInput updateCandidateInput);

}
