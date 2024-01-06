/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.rest;

import com.barraiser.common.graphql.input.ScheduleInterviewInput;
import com.barraiser.common.graphql.types.Interview;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.EvaluationRepository;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.JiraUUIDRepository;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewCreatorInDB;
import com.barraiser.onboarding.interview.InterviewCreatorInJira;
import com.barraiser.onboarding.interview.InterviewService;
import com.barraiser.onboarding.scheduling.scheduling.InterviewSchedulingService;
import com.barraiser.onboarding.scheduling.scheduling.SaasInterviewSchedulingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Headers;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.barraiser.common.constants.ServiceConfigurationConstants.INTERVIEWING_SERVICE_CONTEXT_PATH;

@RestController
@Log4j2
@AllArgsConstructor
public class InterviewManagementController {

	private final InterViewRepository interViewRepository;
	private final InterviewSchedulingService interviewSchedulingService;
	private final SaasInterviewSchedulingService saasInterviewSchedulingService;
	private final EvaluationRepository evaluationRepository;
	private final InterviewCreatorInDB interviewCreatorInDB;
	private final InterviewCreatorInJira interviewCreatorInJira;
	private final JiraUUIDRepository jiraUUIDRepository;
	private final InterviewService interviewService;

	private final ObjectMapper objectMapper;

	@GetMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/evaluation/{evaluationId}/interview")
	public ResponseEntity<Interview> getInterview(@PathVariable("evaluationId") final String evaluationId,
			@RequestParam("interviewStructureId") final String interviewStructureId,
			@RequestParam("ignoreRedoInterview") final Boolean ignoreRedoInterview) {

		final List<InterviewDAO> interviews = this.interViewRepository
				.findByEvaluationIdAndInterviewStructureId(evaluationId, interviewStructureId);

		final InterviewDAO interview = interviews.stream()
				.filter(x -> this.isInterviewToBeConsidered(x, ignoreRedoInterview))
				.findFirst().get();

		return ResponseEntity.ok(this.objectMapper.convertValue(interview, Interview.class));
	}

	@PostMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/interview/schedule")
	public ResponseEntity<Void> scheduleInterview(@RequestBody final ScheduleInterviewInput scheduleInterviewInput)
			throws Exception {

		// TBD: Take care of authenticated user
		this.interviewSchedulingService.scheduleInterview(
				AuthenticatedUser.builder().userName(UUID.randomUUID().toString()).build(), scheduleInterviewInput);
		return ResponseEntity.ok().build();
	}

	@PostMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/evaluation/{evaluationId}/createInterview")
	public ResponseEntity<Interview> createInterview(@PathVariable("evaluationId") final String evaluationId,
			@RequestParam("interviewStructureId") final String interviewStructureId) {

		final EvaluationDAO evaluationDAO = this.evaluationRepository.findById(evaluationId).get();

		final InterviewDAO newInterview = this.interviewCreatorInDB
				.createInterviewInDatabase(evaluationId,
						"NULL".equals(interviewStructureId) ? null : interviewStructureId,
						evaluationDAO.getCreatedBy());

		this.interviewCreatorInJira.createInterviewsInJira(Arrays.asList(newInterview),
				this.jiraUUIDRepository.findByUuid(evaluationId).get().getJira());

		return ResponseEntity.ok(this.objectMapper.convertValue(newInterview, Interview.class));
	}

	@PostMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/saas/interview/schedule")
	public ResponseEntity<Void> scheduleSaasInterview(@RequestBody final ScheduleInterviewInput scheduleInterviewInput)
			throws Exception {

		// TBD: Take care of authenticated user
		this.saasInterviewSchedulingService.scheduleInterview(
				AuthenticatedUser.builder().userName(UUID.randomUUID().toString()).build(), scheduleInterviewInput);
		return ResponseEntity.ok().build();
	}

	private Boolean isInterviewToBeConsidered(final InterviewDAO interviewDAO,
			final Boolean ignoreRedoInterview) {

		if (ignoreRedoInterview && (interviewDAO.getRedoReasonId() != null)) {
			return Boolean.FALSE;
		}

		return Boolean.TRUE;
	}

	@PostMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/saas/interview/{interviewId}/deallocateInterviewer")
	@Headers(value = "Content-Type: application/json")
	void deallocateInterviewer(@RequestParam("interviewId") final String interviewId) {
		InterviewDAO interviewDAO = this.interViewRepository.findById(interviewId).get();

		this.interviewService.save(interviewDAO.toBuilder()
				.interviewerId(null)
				.build());
	}
}
