/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.followup;

import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.EvaluationRepository;
import com.barraiser.onboarding.dal.EvaluationStatus;
import com.barraiser.onboarding.interview.evaluation.EvaluationChangeHistoryManager;
import com.barraiser.onboarding.sfn.StepFunctionProcessor;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class GetFollowUpStatusAndSetWaitTime implements StepFunctionProcessor<FollowUpForSchedulingStepFunctionDTO> {

	private final EvaluationChangeHistoryManager evaluationChangeHistoryManager;
	private final EvaluationRepository evaluationRepository;
	public static final String FOLLOW_UP_DATE_FIELD_KEY = "follow_up_date";
	public static final String JIRA_FOLLOW_UP_DATE_FORMAT = "dd/MMM/yy hh:mm a";

	private final DynamicAppConfigProperties appConfigProperties;
	private final DateUtils utilities;
	private final GetAdjustedFollowUpTime getAdjustedFollowUpTime;

	@Override
	public String getFlowIdentifier(FollowUpForSchedulingStepFunctionDTO data) {
		return data.getEvaluationId();
	}

	@Override
	public void process(FollowUpForSchedulingStepFunctionDTO data) throws Exception {
		final int nextWorkFlowTurn = data.getWorkflowTurn() + 1;
		final String currentFollowUpDate = this.evaluationChangeHistoryManager
				.getCurrentFieldValue(FOLLOW_UP_DATE_FIELD_KEY, data.getEvaluationId());
		Long epoch = null;
		if (!currentFollowUpDate.isEmpty()) {
			epoch = this.utilities.convertDateToEpoch(currentFollowUpDate, JIRA_FOLLOW_UP_DATE_FORMAT,
					FollowUpConstants.TIMEZONE_ASIA_KOLKATA);
		}
		final EvaluationDAO evaluationDAO = evaluationRepository.findById(data.getEvaluationId()).get();
		final List<String> schedulingWaitTimePerTurn = this.appConfigProperties
				.getListOfString(FollowUpConstants.DYNAMO_TIME_TO_WAIT_PER_TURN);
		final long waitTimeForNextTurnInEpoch = this.getAdjustedFollowUpTime.findTimeXMinsAfterExcludingNonOpHrs(
				Integer.parseInt(schedulingWaitTimePerTurn.get(nextWorkFlowTurn - 1)),
				data.getFollowUpDate());
		data.setWorkflowTurn(nextWorkFlowTurn);
		data.setTimestampToWaitUntil(this.utilities.getFormattedDateString(waitTimeForNextTurnInEpoch,
				FollowUpConstants.TIMEZONE_UTC, DateUtils.DATEFORMAT_ISO_8601));
		data.setFollowUpStatus(!Objects.equals(evaluationDAO.getStatus(), EvaluationStatus.WAITING_CANDIDATE.getValue())
				|| !Objects.equals(epoch, data.getFollowUpDate())
						? FollowUpConstants.FLOW_TERMINATE_DUE_TO_STATUS_CHANGE
						: data.getExpiryTime() < (Instant.now().getEpochSecond())
								? FollowUpConstants.FLOW_TERMINATE_DUE_TO_EXPIRY
								: FollowUpConstants.FLOW_CONTINUE);
	}
}
