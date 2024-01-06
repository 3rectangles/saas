/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.expert_deallocation;

import com.amazonaws.AmazonServiceException;
import com.barraiser.onboarding.availability.AvailabilityManager;
import com.barraiser.onboarding.availability.DTO.BookedSlotDTO;
import com.barraiser.onboarding.common.Constants;
import com.barraiser.onboarding.communication.ErrorCommunication;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.interview.CancellationReasonManager;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewUtil;
import com.barraiser.onboarding.scheduling.expert_deallocation.dto.ExpertDeAllocatorData;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO.ExpertDeallocationStepFunctionDTO;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.ReleaseExpertAvailabilityProcessor;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.RemoveInterviewerFromInterviewProcessor;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.UpdateInterviewCostProcessor;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.config.UpdateStatusProcessor;
import com.barraiser.onboarding.scheduling.lifecycle.InterviewToStepFunctionExecutionDAO;
import com.barraiser.onboarding.scheduling.lifecycle.InterviewToStepFunctionExecutionRepository;
import com.barraiser.onboarding.sfn.FlowType;
import com.barraiser.onboarding.sfn.StepFunctionManager;
import com.barraiser.onboarding.user.expert.ExpertUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.UUID;

@Log4j2
@Component
@AllArgsConstructor
public class ExpertDeallocator {
	public static String STEP_FUNCTION_NAME = "expert-deallocation-flow";
	@Qualifier("applicationEnvironment")
	private final String applicationEnvironment;

	private final ReleaseExpertAvailabilityProcessor releaseExpertAvailabilityProcessor;
	private final InterViewRepository interViewRepository;
	private final RemoveInterviewerFromInterviewProcessor removeInterviewerFromInterviewProcessor;
	private final ExpertUtil expertUtil;
	private final StepFunctionManager stepFunctionManager;
	private final ErrorCommunication errorCommunication;
	private final InterviewToStepFunctionExecutionRepository interviewToStepFunctionExecutionRepository;
	private final ObjectMapper objectMapper;
	private final CancellationReasonManager cancellationReasonManager;
	private final UpdateStatusProcessor updateStatusProcessor;
	private final AvailabilityManager availabilityManager;
	private final InterviewUtil interviewUtil;
	private final UpdateInterviewCostProcessor updateInterviewCostProcessor;

	@Transactional
	public void deallocateExpertForInterview(ExpertDeAllocatorData data) throws Exception {
		data = this.prepareData(data);
		if (data.getBookedSlot() != null && data.getBookedSlot().getBuffer() != 0) {
			this.releaseExpertAvailabilityProcessor.process(data);
			if (!data.getIsExpertDuplicate()) {
				if (!InterviewStatus.CANCELLATION_DONE.getValue().equals(data.getInterview().getStatus())) {
					this.updateStatusProcessor.process(data);
					this.removeInterviewerFromInterviewProcessor.process(data);
				}
				this.updateInterviewCostProcessor.process(data);
			}
			final String executionArn = this.triggerStepFunction(data);
			this.saveExecutionInformation(data, executionArn);
		}
	}

	private ExpertDeAllocatorData prepareData(final ExpertDeAllocatorData data) {
		final InterviewDAO interviewDAO = this.interViewRepository.findById(data.getInterviewId()).get();
		final Long startTimeOfExpertForInterview = this.interviewUtil
				.getExpertStartTimeForInterview(interviewDAO);
		final BookedSlotDTO bookedSlot = interviewDAO.getInterviewerId() != null
				? this.availabilityManager.findInterviewingBookedSlot(interviewDAO.getInterviewerId(),
						startTimeOfExpertForInterview, interviewDAO.getEndDate())
				: null;
		data.setInterview(interviewDAO);
		data.setOriginalInterviewerId(interviewDAO.getInterviewerId());
		data.setIsInterviewCancelledByExpert(
				this.cancellationReasonManager.isCancelledByExpert(data.getDeAllocationReason()));
		data.setIsExpertDuplicate(interviewDAO.getInterviewerId() != null
				? this.expertUtil.isExpertDuplicate(interviewDAO.getInterviewerId())
				: null);
		data.setBookedSlot(bookedSlot);
		return data;
	}

	private String triggerStepFunction(final ExpertDeAllocatorData data) throws Exception {
		final ExpertDeallocationStepFunctionDTO stepFunctionDTO = ExpertDeallocationStepFunctionDTO.builder()
				.data(data)
				.build();
		final String stepFunctionArn = String.format(Constants.STATE_MACHINE_ARN, this.applicationEnvironment,
				STEP_FUNCTION_NAME);
		String executionArn = "";

		try {
			executionArn = this.stepFunctionManager.startExecution(
					stepFunctionArn,
					this.objectMapper.writeValueAsString(stepFunctionDTO),
					UUID.randomUUID().toString());
		} catch (final AmazonServiceException | JsonProcessingException e) {
			this.errorCommunication.sendFailureEmailToTech(
					this.applicationEnvironment
							+ " : "
							+ "error starting expert deallocator step function for interview : "
							+ data.getInterviewId(),
					e);
		}

		log.info(
				"Triggered {} step function for interview {}",
				stepFunctionArn,
				data.getInterviewId());
		return executionArn;
	}

	private void saveExecutionInformation(
			final ExpertDeAllocatorData data,
			final String executionArn) {

		this.interviewToStepFunctionExecutionRepository.save(
				InterviewToStepFunctionExecutionDAO.builder()
						.id(UUID.randomUUID().toString())
						.interviewId(data.getInterviewId())
						.flowType(FlowType.EXPERT_DEALLOCATION)
						.ExecutionArn(executionArn)
						.rescheduleCount(
								data.getInterview().getRescheduleCount())
						.build());
	}
}
