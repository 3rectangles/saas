/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.ta;

import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.model.StartExecutionRequest;
import com.amazonaws.services.stepfunctions.model.StartExecutionResult;
import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.barraiser.onboarding.communication.InterviewSchedulingCommunicationService;
import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewService;
import com.barraiser.onboarding.scheduling.InterviewLifecycleUtil;
import com.barraiser.onboarding.scheduling.lifecycle.InterviewToStepFunctionExecutionDAO;
import com.barraiser.onboarding.scheduling.lifecycle.InterviewToStepFunctionExecutionRepository;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingProcessingData;
import com.barraiser.onboarding.sfn.FlowType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import static com.barraiser.common.utilities.DateUtils.TIMEZONE_UTC;

@Component
@AllArgsConstructor
@Log4j2
public class TaAllocationService {
	public static final String STATE_MACHINE_ARN = "arn:aws:states:ap-south-1:969111487786:stateMachine:%s-ta-allocation-lifecycle";
	private final AWSStepFunctions awsStepFunctions;
	private final Environment environment;
	private final InterviewToStepFunctionExecutionRepository interviewToStepFunctionExecutionRepository;
	private final ObjectMapper objectMapper;
	private final InterviewLifecycleUtil interviewLifecycleUtil;
	private final InterViewRepository interViewRepository;
	private final InterviewSchedulingCommunicationService interviewSchedulingCommunicationService;
	private final StaticAppConfigValues staticAppConfigValues;
	private final String TIME_TO_WAIT_FOR_ALLOCATION_FROM_DAY_START_IN_SEC = "time_to_wait_for_ta_allocation_from_day_start_sec";
	private final DynamicAppConfigProperties appConfigProperties;
	private final DateUtils dateUtils;
	private final InterviewService interviewService;

	public void startTaAllocationLifecycleExecution(final String interviewId, final String payload) {
		final StartExecutionResult executionResult = this.awsStepFunctions.startExecution(new StartExecutionRequest()
				.withStateMachineArn(
						String.format(TaAllocationService.STATE_MACHINE_ARN, this.environment.getActiveProfiles()[0]))
				.withInput(payload));

		this.interviewToStepFunctionExecutionRepository.save(InterviewToStepFunctionExecutionDAO.builder()
				.id(UUID.randomUUID().toString())
				.interviewId(interviewId)
				.ExecutionArn(executionResult.getExecutionArn())
				.flowType(FlowType.TA_ALLOCATION)
				.rescheduleCount(this.interViewRepository.findById(interviewId).get().getRescheduleCount())
				.build());

		TaAllocationService.log.info("The arn of the step function execution is {}", executionResult.getExecutionArn());
	}

	private void prepareSchedulingData(InterviewDAO interviewDAO, SchedulingProcessingData data) throws IOException {
		final ZoneId z = ZoneId.of("Asia/Kolkata");
		log.info("interview Dao: {} , scheduling Data: {} ", interviewDAO, data);
		final long waitTimeStamp = ZonedDateTime.ofInstant(Instant.ofEpochSecond(interviewDAO.getStartDate()), z)
				.toLocalDate().atStartOfDay(z).minusDays(1).toInstant().getEpochSecond()
				+ this.appConfigProperties.getInt(this.TIME_TO_WAIT_FOR_ALLOCATION_FROM_DAY_START_IN_SEC);
		data.setTimestampToWaitUntilForTaAllocationStart(this.dateUtils.getFormattedDateString(
				waitTimeStamp,
				TIMEZONE_UTC, DateUtils.DATEFORMAT_ISO_8601));
		data.setSchedulingCommunicationData(
				this.interviewSchedulingCommunicationService.prepareInterviewScheduledCommunicationData(interviewDAO));
		data.setExecuteTaAssignment(Boolean.parseBoolean(this.staticAppConfigValues.getTaAutoAllocationEnabled()));
	}

	private void triggerTaAllocationStepFunction(final SchedulingProcessingData data) throws IOException {
		final String interviewId = data.getInput().getInterviewId();
		final Boolean stepFunctionEnabled = this.interviewLifecycleUtil
				.checkIfStepFunctionIsEnabledForTaAllocation(interviewId);

		if (stepFunctionEnabled) {
			this.startTaAllocationLifecycle(data);
		} else {
			log.info("TA Allocation lifecycle is disabled");
		}

	}

	public Boolean triggerTaAllocation(String interviewId) {
		try {
			final InterviewDAO interviewDAO = this.interViewRepository.findById(interviewId).get();
			if (!InterviewStatus.CANCELLATION_DONE.getValue().equalsIgnoreCase(interviewDAO.getStatus())) {
				final SchedulingProcessingData data = new SchedulingProcessingData();
				log.info("Clearing Assigned Ta for interview Id: {}", interviewDAO.getId());
				final InterviewDAO interviewDAOSaved = this.interviewService
						.save(interviewDAO.toBuilder().taggingAgent(null).build());
				this.prepareSchedulingData(interviewDAOSaved, data);
				this.triggerTaAllocationStepFunction(data);
			}
			return true;
		} catch (Exception e) {
			log.error("Exception in triggering Ta Allocation for interview Id {}", interviewId, e.getMessage(), e);
			return false;
		}
	}

	private void startTaAllocationLifecycle(final SchedulingProcessingData data) throws IOException {
		log.info("Starting TA allocation lifecycle for {} ", data.getInput().getInterviewId());
		this.startTaAllocationLifecycleExecution(data.getInput().getInterviewId(),
				this.objectMapper.writeValueAsString(data));
	}
}
