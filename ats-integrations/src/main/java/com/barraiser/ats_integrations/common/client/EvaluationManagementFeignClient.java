/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.common.client;

import com.barraiser.commons.dto.evaluationManagement.AddEvaluationRequest;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static com.barraiser.common.constants.ServiceConfigurationConstants.INTERVIEWING_SERVICE_CONTEXT_PATH;

@FeignClient(name = "evaluation-management-feign-client", url = "http://localhost:5000")
public interface EvaluationManagementFeignClient {

	@PostMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/evaluation")
	@Headers(value = "Content-Type: application/json")
	ResponseEntity<String> addEvaluation(@RequestBody AddEvaluationRequest addEvaluationRequest);

}
