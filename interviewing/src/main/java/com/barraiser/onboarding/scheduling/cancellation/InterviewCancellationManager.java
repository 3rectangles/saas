/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.stepfunctions.model.ExecutionStatus;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.barraiser.onboarding.communication.ErrorCommunication;
import com.barraiser.onboarding.communication.InterviewCancellationCommunicationService;
import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.dal.CancellationReasonRepository;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.interview.CancellationReasonManager;
import com.barraiser.onboarding.interview.InterviewUtil;
import com.barraiser.onboarding.interview.PartnerConfigManager;
import com.barraiser.onboarding.scheduling.InterviewLifecycleUtil;
import com.barraiser.onboarding.scheduling.cancellation.sfn.DTO.InterviewCancellationStepFunctionDTO;
import com.barraiser.onboarding.scheduling.lifecycle.InterviewToStepFunctionExecutionDAO;
import com.barraiser.onboarding.scheduling.lifecycle.InterviewToStepFunctionExecutionRepository;
import com.barraiser.onboarding.sfn.FlowType;
import com.barraiser.onboarding.sfn.StepFunctionManager;
import com.barraiser.onboarding.user.expert.ExpertUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.barraiser.onboarding.common.Constants.STATE_MACHINE_ARN;
import static com.barraiser.onboarding.scheduling.scheduling.match_interviewers.AllocateTaProcessor.DYNAMO_TIME_TO_WAIT_BEFORE_INFORMING_OPS_IN_SEC;

@Log4j2
@Component
@AllArgsConstructor
public class InterviewCancellationManager {

	public static final String DYNAMO_HARD_CANCELLATION_ENABLED_KEY = "sfn-hard-cancellation-enabled";
	public static String STEP_FUNCTION_NAME = "interview-cancellation-flow";

	@Qualifier("applicationEnvironment")
	private final String applicationEnvironment;

	private final DataValidationProcessor dataValidationProcessor;
	private final DynamicAppConfigProperties appConfigProperties;
	private final StaticAppConfigValues staticAppConfigValues;
	private final InterviewToStepFunctionExecutionRepository interviewToStepFunctionExecutionRepository;
	private final InterviewCancellationCommunicationService interviewCancellationCommunicationService;
	private final StepFunctionManager stepFunctionManager;
	private final ObjectMapper objectMapper;
	private final ErrorCommunication errorCommunication;
	private final CancellationReasonManager cancellationReasonManager;
	private final ExpertUtil expertUtil;
	private final CancelInterviewProcessor cancelInterviewProcessor;
	private final InterviewLifecycleUtil interviewLifecycleUtil;
	private final CancellationReasonRepository cancellationReasonRepository;
	private final PartnerConfigManager partnerConfigManager;
	private final InterviewUtil interviewUtil;

	public boolean processInterviewCancelledInConfirmationFlow(
			final InterviewDAO interview, final String cancelledBy, final String source)
			throws Exception {
		if (this.appConfigProperties.getBoolean(DYNAMO_HARD_CANCELLATION_ENABLED_KEY)) {
			return this.cancel(interview, cancelledBy, source);
		} else {
			this.interviewCancellationCommunicationService.communicateInterviewCancellationToOps(
					interview.getId(), "");
		}
		return true;
	}

	public boolean cancel(
			final InterviewDAO interview, final String cancelledBy, final String source) throws Exception {

		final Boolean stepFunctionEnabled = Boolean.parseBoolean(
				this.staticAppConfigValues.getInterviewLifecycleManagementEnabled());
		final Boolean isCancellationFlowTriggeringNeeded = this.isCancellationFlowTriggeringNeeded(interview);

		if (stepFunctionEnabled && isCancellationFlowTriggeringNeeded) {
			final CancellationProcessingData cancellationData = new CancellationProcessingData();
			cancellationData.setInterviewId(interview.getId());
			cancellationData.setInterviewRescheduleCount(interview.getRescheduleCount());
			cancellationData.setInterviewToBeCancelled(interview);
			cancellationData.setCancellationTimeOfInterview(
					Long.parseLong(interview.getCancellationTime()));
			cancellationData.setUserCancellingTheInterview(cancelledBy);

			cancellationData.setIsNonReschedulableInterview(
					this.cancellationReasonRepository
							.findById(interview.getCancellationReasonId())
							.get()
							.getNonReschedulableReason());

			cancellationData.setSourceOfCancellation(source);

			this.dataValidationProcessor.process(cancellationData);
			this.prepareDataForCancellation(cancellationData);

			this.cancelInterviewProcessor.cancelInterview(cancellationData);
			final String executionArn = this.triggerStepFunction(cancellationData);
			this.saveExecutionInformation(cancellationData, executionArn);
			return true;
		}
		return false;
	}

	private String triggerStepFunction(final CancellationProcessingData cancellationProcessingData)
			throws Exception {
		final InterviewCancellationStepFunctionDTO stepFunctionDTO = InterviewCancellationStepFunctionDTO.builder()
				.data(cancellationProcessingData)
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
							+ "error starting cancellation step function for interview : "
							+ cancellationProcessingData.getInterviewId(),
					e);
		}

		log.info(
				"Triggered {} step function for interview {}",
				stepFunctionArn,
				cancellationProcessingData.getInterviewId());
		return executionArn;
	}

	private Boolean isCancellationFlowTriggeringNeeded(final InterviewDAO interview)
			throws Exception {

		final Optional<InterviewToStepFunctionExecutionDAO> interviewToStepFunctionExecutionDAO = this.interviewToStepFunctionExecutionRepository
				.findTopByInterviewIdAndFlowTypeAndRescheduleCountOrderByCreatedOnDesc(
						interview.getId(),
						FlowType.INTERVIEW_CANCELLATION,
						interview.getRescheduleCount());
		Boolean isCancellationFlowTriggeringNeeded = true;

		if (interviewToStepFunctionExecutionDAO.isPresent()
				&& this.isExecutionSuccessfulOrRunning(
						interviewToStepFunctionExecutionDAO.get().getExecutionArn())) {
			isCancellationFlowTriggeringNeeded = false;
		}

		return isCancellationFlowTriggeringNeeded;
	}

	private Boolean isExecutionSuccessfulOrRunning(final String executionArn) throws Exception {
		final String executionStatus = this.stepFunctionManager.getExecutionInformation(executionArn).getStatus();
		return (ExecutionStatus.SUCCEEDED.toString().equalsIgnoreCase(executionStatus)
				|| ExecutionStatus.RUNNING.toString().equalsIgnoreCase(executionStatus));
	}

	private void saveExecutionInformation(
			final CancellationProcessingData cancellationProcessingData,
			final String executionArn) {

		this.interviewToStepFunctionExecutionRepository.save(
				InterviewToStepFunctionExecutionDAO.builder()
						.id(UUID.randomUUID().toString())
						.interviewId(cancellationProcessingData.getInterviewId())
						.flowType(FlowType.INTERVIEW_CANCELLATION)
						.ExecutionArn(executionArn)
						.rescheduleCount(
								cancellationProcessingData
										.getPreviousStateOfCancelledInterview()
										.getRescheduleCount())
						.build());
	}

	private void prepareDataForCancellation(final CancellationProcessingData data) {
		data.setIsOriginalInterviewScheduledWithDuplicate(
				data.getInterviewToBeCancelled().getInterviewerId() != null ? this.expertUtil.isExpertDuplicate(
						data.getInterviewToBeCancelled().getInterviewerId()) : null);
		data.setIsTaAssigned(Objects.nonNull(data.getInterviewToBeCancelled().getTaggingAgent()));

		data.setIsTaAutoAllocationEnabled(this.isTaAutoAllocationEnabled(data));
		data.setIsInterviewCancelledByExpert(
				this.cancellationReasonManager.isCancelledByExpert(
						data.getInterviewToBeCancelled().getCancellationReasonId()));
		// hack just for braintrust
		data.setPartnerId(this.partnerConfigManager.getPartnerCompanyForInterviewId(data.getInterviewId()).getId());
	}

	private boolean isTaAutoAllocationEnabled(final CancellationProcessingData data) {
		return !this.interviewUtil.getRoundTypesThatNeedNoTaggingAgent()
				.contains(data.getInterviewToBeCancelled().getInterviewRound()) &&
				this.interviewLifecycleUtil.checkIfStepFunctionIsEnabledForTaAllocation(
						data.getInterviewId())
				&& Objects.nonNull(data.getInterviewToBeCancelled().getTaggingAgent())
				&& (data.getInterviewToBeCancelled().getStartDate()
						- (long) this.appConfigProperties.getInt(
								DYNAMO_TIME_TO_WAIT_BEFORE_INFORMING_OPS_IN_SEC) >= 0);
	}
}
