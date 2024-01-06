/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment;

import com.amazonaws.AmazonServiceException;
import com.barraiser.onboarding.common.Constants;
import com.barraiser.onboarding.communication.ErrorCommunication;
import com.barraiser.onboarding.communication.InterviewSchedulingCommunicationService;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO.ExpertAllocationStepFunctionDTO;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO.ExpertAllocatorData;
import com.barraiser.onboarding.scheduling.expertAssignment.DataValidationProcessor;
import com.barraiser.onboarding.scheduling.lifecycle.InterviewToStepFunctionExecutionDAO;
import com.barraiser.onboarding.scheduling.lifecycle.InterviewToStepFunctionExecutionRepository;
import com.barraiser.onboarding.scheduling.scheduling.ExpertSchedulingCommunicationData;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingCommunicationData;
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
import java.io.IOException;
import java.util.UUID;

@Log4j2
@Component
@AllArgsConstructor
public class ExpertAllocator {
	public static String STEP_FUNCTION_NAME = "expert-allocation-flow";
	@Qualifier("applicationEnvironment")
	private final String applicationEnvironment;

	private final InterViewRepository interViewRepository;
	private final BlockExpertAvailabilityProcessor blockExpertAvailabilityProcessor;
	private final AssignInterviewerToInterviewProcessor assignInterviewerToInterviewProcessor;
	private final ExpertUtil expertUtil;
	private final InterviewSchedulingCommunicationService interviewSchedulingCommunicationService;
	private final ObjectMapper objectMapper;
	private final StepFunctionManager stepFunctionManager;
	private final ErrorCommunication errorCommunication;
	private final InterviewToStepFunctionExecutionRepository interviewToStepFunctionExecutionRepository;
	private final DataValidationProcessor dataValidationProcessor;
	private final ExpertCostCalculationProcessor expertCostCalculationProcessor;

	// Expert allocation involves assigning the expert to interview,
	// sending communications to expert, calculating cost that will be given to
	// expert upon completion of
	// interview, handling in case of duplicate expert

	@Transactional
	public void allocateExpertToInterview(ExpertAllocatorData data) throws Exception {
		data = this.prepareDataForExpertAllocation(data);
		// 1. validate data to see if allocation of expert is possible
		this.dataValidationProcessor.process(data);

		// 2. book expert slot
		this.blockExpertAvailabilityProcessor.process(data);

		// 3. assign expert to interview
		this.assignInterviewerToInterviewProcessor.process(data);

		// 4. calculating cost that will be given to expert upon interview completion
		this.expertCostCalculationProcessor.process(data);

		final String executionArn = this.triggerStepFunction(data);
		this.saveExecutionInformation(data, executionArn);
	}

	private ExpertAllocatorData prepareDataForExpertAllocation(final ExpertAllocatorData data) throws IOException {
		final InterviewDAO interviewDAO = this.interViewRepository.findById(data.getInterviewId()).get();
		data.setInterview(interviewDAO);
		final ExpertSchedulingCommunicationData expertSchedulingCommunicationData = this.interviewSchedulingCommunicationService
				.prepareInterviewScheduledCommunicationDataForExpert(data);
		data.setStartDate(data.getStartDate() == null ? interviewDAO.getStartDate() : data.getStartDate());
		data.setSchedulingCommunicationData(this.objectMapper.convertValue(expertSchedulingCommunicationData,
				SchedulingCommunicationData.class));
		data.setIsExpertDuplicate(this.expertUtil.isExpertDuplicate(data.getInterviewerId()));
		return data;
	}

	private String triggerStepFunction(final ExpertAllocatorData data) throws Exception {
		final ExpertAllocationStepFunctionDTO stepFunctionDTO = ExpertAllocationStepFunctionDTO.builder()
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
							+ "error starting expert allocation step function for interview : "
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
			final ExpertAllocatorData data,
			final String executionArn) {

		this.interviewToStepFunctionExecutionRepository.save(
				InterviewToStepFunctionExecutionDAO.builder()
						.id(UUID.randomUUID().toString())
						.interviewId(data.getInterviewId())
						.flowType(FlowType.EXPERT_ALLOCATION)
						.ExecutionArn(executionArn)
						.rescheduleCount(
								data.getInterview().getRescheduleCount())
						.build());
	}
}
