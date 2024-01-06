/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.common.client;

import com.barraiser.common.graphql.input.ScheduleInterviewInput;
import com.barraiser.common.graphql.types.Interview;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.barraiser.common.constants.ServiceConfigurationConstants.INTERVIEWING_SERVICE_CONTEXT_PATH;

@FeignClient(name = "interview-management-feign-client", url = "http://localhost:5000")
public interface InterviewManagementFeignClient {

	@GetMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/evaluation/{evaluationId}/interview")
	@Headers(value = "Content-Type: application/json")
	ResponseEntity<Interview> getInterview(@PathVariable("evaluationId") final String evaluationId,
			@RequestParam("interviewStructureId") final String interviewStructureId,
			@RequestParam("ignoreRedoInterview") final Boolean ignoreRedoInterview);

	@PostMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/interview/schedule")
	@Headers(value = "Content-type: application/json")
	ResponseEntity<Boolean> scheduleInterview(@RequestBody ScheduleInterviewInput scheduleInterviewInput);

	@PostMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/evaluation/{evaluationId}/createInterview")
	@Headers(value = "Content-Type: application/json")
	ResponseEntity<Interview> createInterview(
			@PathVariable("evaluationId") final String evaluationId,
			@RequestParam(value = "interviewStructureId") final String interviewStructureId);

	@PostMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/saas/interview/schedule")
	public ResponseEntity<Void> scheduleSaasInterview(@RequestBody final ScheduleInterviewInput scheduleInterviewInput);

	@PostMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/saas/interview/{interviewId}/deallocateInterviewer")
	@Headers(value = "Content-Type: application/json")
	void deallocateInterviewer(@RequestParam("interviewId") final String interviewId);
}
