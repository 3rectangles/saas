/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.confirmation;

import com.barraiser.common.graphql.types.Interview;
import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.scheduling.confirmation.dto.InterviewConfirmationLifecycleDTO;
import com.barraiser.onboarding.scheduling.confirmation.util.ConfirmationUtils;
import com.barraiser.onboarding.interview.jobrole.JobRoleManager;
import com.barraiser.onboarding.user.TimezoneManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

@Component
@AllArgsConstructor
public class GetInterviewInformationProcessor implements ConfirmationProcessor {

	private final DynamicAppConfigProperties appConfigProperties;
	private final DateUtils utilities;
	private final ConfirmationUtils confirmationUtils;
	private final ObjectMapper objectMapper;
	private final JobRoleManager jobRoleManager;

	private final InterViewRepository interViewRepository;
	private final EvaluationRepository evaluationRepository;
	private final PartnerCompanyRepository partnerCompanyRepository;
	private final TimezoneManager timezoneManager;

	@Override
	public void process(InterviewConfirmationLifecycleDTO data) throws Exception {
		final InterviewDAO interviewDAO = this.interViewRepository.findById(data.getInterviewId())
				.orElseThrow(() -> new IllegalArgumentException("Interview does not exist: " + data.getInterviewId()));
		final Interview interview = this.objectMapper.convertValue(interviewDAO, Interview.class);
		Map<String, Object> lifeCycleParams = this.setLifeCycleParams(interview);
		data.setInterview(interview);
		data.setWorkflowPath((String) lifeCycleParams.get(ConfirmationConstants.LIFECYCLE_PARAM_KEY_WORKFLOW_PATH));
		data.setPartnerId(this.getPartnerId(interviewDAO));
		data.setTimestampToWaitUntil(
				(String) lifeCycleParams.get(ConfirmationConstants.LIFECYCLE_PARAM_KEY_TIMESTAMP_TO_WAIT_UNTIL));
		data.setWorkflowTurn((Integer) lifeCycleParams.get(ConfirmationConstants.LIFECYCLE_PARAM_KEY_WORKFLOW_TURN));
	}

	private Map<String, Object> setLifeCycleParams(Interview interview) {
		Map<String, Object> lifeCycleParams = new HashMap<>();
		final Long interviewScheduleTime = interview.getStartDate();
		final List<String> candidateWaitTimesPerTurn = this.appConfigProperties
				.getListOfString(ConfirmationConstants.DYNAMO_TIME_TO_WAIT_PER_TURN);
		final long timeSixHoursBeforeInterview = confirmationUtils.findTimeXMinsBeforeExcludingNonOpHrs(
				this.appConfigProperties.getInt(ConfirmationConstants.DYNAMO_TIME_BEFORE_INTERVIEW_FOR_PATH_TWO),
				interviewScheduleTime, this.timezoneManager.getTimezoneOfCandidate(interview.getId()));
		final long timeTwelveHoursBeforeInterview = confirmationUtils.findTimeXMinsBeforeExcludingNonOpHrs(
				this.appConfigProperties.getInt(ConfirmationConstants.DYNAMO_TIME_BEFORE_INTERVIEW_FOR_PATH_ONE),
				interviewScheduleTime, this.timezoneManager.getTimezoneOfCandidate(interview.getId()));

		if (Instant.now().compareTo(Instant.ofEpochSecond(timeTwelveHoursBeforeInterview)) < 0) {
			lifeCycleParams.put(ConfirmationConstants.LIFECYCLE_PARAM_KEY_WORKFLOW_PATH,
					ConfirmationConstants.WORKFLOW_PATH_ONE);
			lifeCycleParams.put(ConfirmationConstants.LIFECYCLE_PARAM_KEY_WORKFLOW_TURN,
					ConfirmationConstants.WORKFLOW_TURN_FOR_PATH_ONE);
			lifeCycleParams.put(ConfirmationConstants.LIFECYCLE_PARAM_KEY_TIMESTAMP_TO_WAIT_UNTIL,
					this.utilities.getFormattedDateString(timeTwelveHoursBeforeInterview,
							ConfirmationConstants.TIMEZONE_UTC, DateUtils.DATEFORMAT_ISO_8601));
		} else if (Instant.now().compareTo(Instant.ofEpochSecond(timeSixHoursBeforeInterview)) < 0) {
			lifeCycleParams.put(ConfirmationConstants.LIFECYCLE_PARAM_KEY_WORKFLOW_PATH,
					ConfirmationConstants.WORKFLOW_PATH_TWO);
			lifeCycleParams.put(ConfirmationConstants.LIFECYCLE_PARAM_KEY_WORKFLOW_TURN,
					ConfirmationConstants.WORKFLOW_TURN_FOR_PATH_TWO);
			final long timeTwoHoursBeforeInterview = confirmationUtils.findTimeXMinsBeforeExcludingNonOpHrs(
					Integer.parseInt(
							candidateWaitTimesPerTurn.get(ConfirmationConstants.WORKFLOW_TURN_FOR_PATH_TWO - 1)),
					interviewScheduleTime, this.timezoneManager.getTimezoneOfCandidate(interview.getId()));
			lifeCycleParams.put(ConfirmationConstants.LIFECYCLE_PARAM_KEY_TIMESTAMP_TO_WAIT_UNTIL,
					this.utilities.getFormattedDateString(timeTwoHoursBeforeInterview,
							ConfirmationConstants.TIMEZONE_UTC, DateUtils.DATEFORMAT_ISO_8601));
		}
		return lifeCycleParams;
	}

	private String getPartnerId(final InterviewDAO interviewDAO) {
		final EvaluationDAO evaluationDAO = this.evaluationRepository.findById(interviewDAO.getEvaluationId()).get();
		final JobRoleDAO jobRoleDAO = this.jobRoleManager.getJobRoleFromEvaluation(evaluationDAO).get();
		return this.partnerCompanyRepository.findByCompanyId(jobRoleDAO.getCompanyId()).get().getId();
	}
}
