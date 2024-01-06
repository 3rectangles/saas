/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment;

import com.amazonaws.AmazonServiceException;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.communication.ErrorCommunication;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.PartnerConfigManager;
import com.barraiser.onboarding.scheduling.cancellation.DataValidatorForExpertReassignmentProcessor;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO.ExpertReassignmentData;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO.ExpertReassignmentStepFunctionDTO;
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
import java.util.UUID;

import static com.barraiser.onboarding.common.Constants.STATE_MACHINE_ARN;

@Log4j2
@Component
@AllArgsConstructor
public class ExpertReassignmentManager {
	public static String STEP_FUNCTION_NAME = "expert-reassignment-flow";

	@Qualifier("applicationEnvironment")
	private final String applicationEnvironment;
	private final ObjectMapper objectMapper;
	private final StepFunctionManager stepFunctionManager;
	private final DeAllocateExpertProcessor deAllocateExpertProcessor;
	private final InterviewToStepFunctionExecutionRepository interviewToStepFunctionExecutionRepository;
	private final ErrorCommunication errorCommunication;
	private final InterViewRepository interViewRepository;
	private final PartnerConfigManager partnerConfigManager;
	private final DataValidatorForExpertReassignmentProcessor dataValidatorForExpertReassignmentProcessor;

	public Boolean reassignExpert(final String interviewId, final String reassignmentReason,
			final AuthenticatedUser reassignedBy)
			throws Exception {
		final ExpertReassignmentData data = this.prepareData(interviewId, reassignmentReason,
				reassignedBy.getUserName());
		this.dataValidatorForExpertReassignmentProcessor.validate(interviewId, reassignedBy);
		this.deAllocateExpertProcessor.process(data);
		final String executionArn = this.triggerStepFunction(data);
		this.saveExecutionInformation(data, executionArn);
		return true;
	}

	private ExpertReassignmentData prepareData(final String interviewId, final String reassignmentReason,
			final String reassignedBy) {
		final ExpertReassignmentData data = new ExpertReassignmentData();
		final InterviewDAO interviewDAO = this.interViewRepository.findById(interviewId).get();
		data.setInterviewId(interviewId);
		data.setCancellationRequestedTimeOfInterview(Instant.now().getEpochSecond());
		data.setReassignmentReason(reassignmentReason);
		data.setReassignedBy(reassignedBy);
		data.setInterview(interviewDAO);
		data.setIsCandidateSchedulingEnabled(
				this.partnerConfigManager.shouldSendSchedulingLinkToCandidate(interviewDAO));
		return data;
	}

	private String triggerStepFunction(final ExpertReassignmentData expertReassignmentData)
			throws Exception {
		final ExpertReassignmentStepFunctionDTO stepFunctionDTO = ExpertReassignmentStepFunctionDTO.builder()
				.data(expertReassignmentData)
				.build();
		final String stepFunctionArn = String.format(STATE_MACHINE_ARN, this.applicationEnvironment,
				STEP_FUNCTION_NAME);
		String executionArn = "";

		try {
			executionArn = this.stepFunctionManager.startExecution(
					stepFunctionArn,
					this.objectMapper.writeValueAsString(stepFunctionDTO),
					UUID.randomUUID().toString());
		} catch (final AmazonServiceException e) {
			this.errorCommunication.sendFailureEmailToTech(
					this.applicationEnvironment
							+ " : "
							+ "error starting  step function for interview : "
							+ expertReassignmentData.getInterviewId(),
					e);
		}

		log.info(
				"Triggered {} step function for interview {}",
				stepFunctionArn,
				expertReassignmentData.getInterviewId());
		return executionArn;
	}

	private void saveExecutionInformation(
			final ExpertReassignmentData expertReassignmentData,
			final String executionArn) {

		this.interviewToStepFunctionExecutionRepository.save(
				InterviewToStepFunctionExecutionDAO.builder()
						.id(UUID.randomUUID().toString())
						.interviewId(expertReassignmentData.getInterviewId())
						.flowType(FlowType.EXPERT_REASSIGNMENT)
						.ExecutionArn(executionArn)
						.rescheduleCount(
								expertReassignmentData
										.getInterview()
										.getRescheduleCount())
						.build());
	}
}
