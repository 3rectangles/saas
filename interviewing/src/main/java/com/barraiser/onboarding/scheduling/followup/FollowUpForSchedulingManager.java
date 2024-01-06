/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.followup;

import com.amazonaws.AmazonServiceException;
import com.barraiser.common.graphql.types.Evaluation;
import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.communication.ErrorCommunication;
import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewHistoryManager;
import com.barraiser.onboarding.interview.jira.evaluation.EvaluationServiceDeskProcessingData;
import com.barraiser.onboarding.interview.jobrole.JobRoleManager;
import com.barraiser.onboarding.scheduling.confirmation.ConfirmationConstants;
import com.barraiser.onboarding.scheduling.lifecycle.InterviewToStepFunctionExecutionDAO;
import com.barraiser.onboarding.scheduling.lifecycle.InterviewToStepFunctionExecutionRepository;
import com.barraiser.onboarding.sfn.FlowType;
import com.barraiser.onboarding.sfn.StepFunctionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.barraiser.onboarding.common.Constants.STATE_MACHINE_ARN;

@Log4j2
@Component
@AllArgsConstructor
public class FollowUpForSchedulingManager {

	public static String STEP_FUNCTION_NAME = "follow-up-for-scheduling";

	@Qualifier("applicationEnvironment")
	private final String applicationEnvironment;

	private final InterviewToStepFunctionExecutionRepository interviewToStepFunctionExecutionRepository;
	private final StepFunctionManager stepFunctionManager;
	private final ObjectMapper objectMapper;
	private final ErrorCommunication errorCommunication;
	private final InterViewRepository interViewRepository;
	private final InterviewHistoryManager interviewHistoryManager;
	private final PartnerCompanyRepository partnerCompanyRepository;
	private final JobRoleManager jobRoleManager;
	private final DynamicAppConfigProperties appConfigProperties;
	private final DateUtils utilities;
	private final String JIRA_FOLLOW_UP_DATE_FORMAT = "dd/MMM/yy hh:mm a";

	public boolean trigger(
			final EvaluationServiceDeskProcessingData data)
			throws Exception {

		final Long followUpDate = this.utilities.convertDateToEpoch(data.getFollowUpDate(),
				this.JIRA_FOLLOW_UP_DATE_FORMAT, FollowUpConstants.TIMEZONE_ASIA_KOLKATA);
		final EvaluationDAO evaluationDAO = data.getEvaluationDAO();
		final List<InterviewDAO> interviewDAOList = this.interViewRepository
				.findAllByEvaluationId(evaluationDAO.getId());
		final String interviewId = interviewDAOList.stream()
				.filter(interviewDAO -> Objects.equals(interviewDAO.getStatus(), "pending_scheduling")).findFirst()
				.map(InterviewDAO::getId).orElse(null);
		final InterviewHistoryDAO interviewHistoryDAO = this.interviewHistoryManager
				.getLatestChangeInStatusOfInterview(interviewId, "pending_scheduling");
		if (interviewHistoryDAO == null) {
			throw new IllegalArgumentException("No interview exists which is yet to be scheduled");
		}
		final Instant interviewCreatedOn = interviewHistoryDAO.getCreatedOn();
		final FollowUpForSchedulingStepFunctionDTO followUpForSchedulingStepFunctionDTO = new FollowUpForSchedulingStepFunctionDTO();
		followUpForSchedulingStepFunctionDTO
				.setEvaluation(this.objectMapper.convertValue(evaluationDAO, Evaluation.class));
		followUpForSchedulingStepFunctionDTO.setFollowUpDate(followUpDate);
		followUpForSchedulingStepFunctionDTO.setPartnerId(this.getPartnerId(evaluationDAO));
		followUpForSchedulingStepFunctionDTO
				.setTimestampToWaitUntil(this.utilities.getFormattedDateString(followUpDate,
						ConfirmationConstants.TIMEZONE_UTC, DateUtils.DATEFORMAT_ISO_8601));
		followUpForSchedulingStepFunctionDTO.setEvaluationId(evaluationDAO.getId());
		followUpForSchedulingStepFunctionDTO.setWorkflowTurn(1);
		followUpForSchedulingStepFunctionDTO
				.setExpiryTime(interviewCreatedOn.getEpochSecond() + ((long) (this.appConfigProperties
						.getInt(FollowUpConstants.DYNAMO_FOLLOW_UP_EXPIRY_TIME_IN_MINUTES)) * 60));
		final String executionArn = this.triggerStepFunction(followUpForSchedulingStepFunctionDTO);
		this.saveExecutionInformation(followUpForSchedulingStepFunctionDTO, executionArn);
		return true;
	}

	private String getPartnerId(final EvaluationDAO evaluationDAO) {
		final JobRoleDAO jobRoleDAO = this.jobRoleManager.getJobRoleFromEvaluation(evaluationDAO).get();
		return this.partnerCompanyRepository.findByCompanyId(jobRoleDAO.getCompanyId()).get().getId();
	}

	private String triggerStepFunction(
			final FollowUpForSchedulingStepFunctionDTO followUpForSchedulingStepFunctionDTO)
			throws Exception {

		final String stepFunctionArn = String.format(STATE_MACHINE_ARN, this.applicationEnvironment,
				STEP_FUNCTION_NAME);
		String executionArn = "";

		try {
			executionArn = this.stepFunctionManager.startExecution(
					stepFunctionArn,
					this.objectMapper.writeValueAsString(followUpForSchedulingStepFunctionDTO),
					UUID.randomUUID().toString());
		} catch (final AmazonServiceException e) {
			this.errorCommunication.sendFailureEmailToTech(
					this.applicationEnvironment
							+ " : "
							+ "error starting cancellation step function for interview : "
							+ followUpForSchedulingStepFunctionDTO.getEvaluationId(),
					e);
		}

		log.info(
				"Triggered {} step function for interview {}",
				stepFunctionArn,
				followUpForSchedulingStepFunctionDTO.getEvaluationId());
		return executionArn;
	}

	private void saveExecutionInformation(
			final FollowUpForSchedulingStepFunctionDTO followUpForSchedulingStepFunctionDTO,
			final String executionArn) {
		List<InterviewDAO> interviewDAOList = this.interViewRepository
				.findAllByEvaluationId(followUpForSchedulingStepFunctionDTO.getEvaluationId());
		interviewDAOList.stream().filter(interviewDAO -> interviewDAO.getStatus().equals("pending_scheduling"))
				.map(interviewDAO -> InterviewToStepFunctionExecutionDAO.builder()
						.id(UUID.randomUUID().toString())
						.interviewId(interviewDAO.getId())
						.flowType(FlowType.CANDIDATE_FOLLOW_UP_FOR_SCHEDULING)
						.ExecutionArn(executionArn)
						.rescheduleCount(interviewDAO.getRescheduleCount())
						.build())
				.forEach(this.interviewToStepFunctionExecutionRepository::save);
	}

}
