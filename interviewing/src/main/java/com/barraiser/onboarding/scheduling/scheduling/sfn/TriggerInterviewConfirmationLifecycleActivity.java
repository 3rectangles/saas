/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.sfn;

import com.barraiser.onboarding.candidate.CandidateInformationManager;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.validators.GenericDataValidationUtil;
import com.barraiser.onboarding.scheduling.InterviewLifecycleUtil;
import com.barraiser.onboarding.scheduling.lifecycle.DTO.InterviewLifecycleDTO;
import com.barraiser.onboarding.scheduling.lifecycle.InterviewLifecycleManagementService;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingProcessingData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Log4j2
@AllArgsConstructor
@Component
public class TriggerInterviewConfirmationLifecycleActivity implements InterviewSchedulingActivity {
	public static final String TRIGGER_INTERVIEW_CONFIRMATION_LIFECYCLE = "trigger-interview-confirmation-lifecycle";
	private final InterViewRepository interViewRepository;
	private final InterviewLifecycleUtil interviewLifecycleUtil;
	private final GenericDataValidationUtil genericDataValidationUtil;
	private final CandidateInformationManager candidateInformationManager;
	private final InterviewLifecycleManagementService interviewLifecycleManagementService;
	private final ObjectMapper objectMapper;

	private void startInterviewConfirmationLifecycle(final String interviewId) throws IOException {
		log.info("Starting interview lifecycle for {} ", interviewId);

		final InterviewLifecycleDTO interviewLifecycleDTO = InterviewLifecycleDTO.builder()
				.interviewId(interviewId)
				.build();

		this.interviewLifecycleManagementService.startInterviewLifecycleExecution(interviewId,
				this.objectMapper.writeValueAsString(interviewLifecycleDTO));
	}

	private void informOperations(final String interviewId, final String errors) {
		log.info("Interview lifecycle could not be started because of data issues. Fix them and reschedule");
		try {
			this.interviewLifecycleUtil.informOperations(interviewId, "Error while scheduling interview", errors);
		} catch (final IOException ioException) {
			log.info("Exception while informing ops : {}", ioException.getStackTrace());
		}
	}

	@Override
	public String name() {
		return TRIGGER_INTERVIEW_CONFIRMATION_LIFECYCLE;
	}

	@Override
	public SchedulingProcessingData process(String input) throws Exception {
		final SchedulingProcessingData data = this.objectMapper.readValue(input, SchedulingProcessingData.class);
		final String interviewId = data.getInput().getInterviewId();
		final Boolean stepFunctionEnabled = this.interviewLifecycleUtil
				.checkIfStepFunctionIsEnabledForInterview(interviewId);

		final Boolean isCandidateAnonymous = this.candidateInformationManager
				.isCandidateAnonymous(this.getIntervieweeForInterview(data.getInput().getInterviewId()));

		if (stepFunctionEnabled && !isCandidateAnonymous) {
			// 1. Check if there are errors while triggering step function. (
			final String errors = this.genericDataValidationUtil.validateInterviewInformation(interviewId);

			if (errors.length() != 0) {
				this.informOperations(interviewId, errors);
			} else {
				this.startInterviewConfirmationLifecycle(interviewId);
			}

		} else {
			log.info("Interview lifecycle is disabled");
		}
		return data;
	}

	private String getIntervieweeForInterview(final String interviewId) {
		return this.interViewRepository.findById(interviewId).get().getIntervieweeId();
	}
}
