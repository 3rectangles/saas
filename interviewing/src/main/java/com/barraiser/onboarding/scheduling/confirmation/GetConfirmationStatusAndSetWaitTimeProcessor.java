/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.confirmation;

import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.scheduling.confirmation.dto.InterviewConfirmationLifecycleDTO;
import com.barraiser.onboarding.scheduling.confirmation.util.ConfirmationUtils;
import com.barraiser.onboarding.user.TimezoneManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.barraiser.onboarding.scheduling.confirmation.ConfirmationConstants.*;

@Component
@AllArgsConstructor
public class GetConfirmationStatusAndSetWaitTimeProcessor implements ConfirmationProcessor {

	private final InterviewConfirmationManager interviewConfirmationManager;
	private final DynamicAppConfigProperties appConfigProperties;
	private final DateUtils utilities;
	private final ConfirmationUtils confirmationUtils;
	private final TimezoneManager timezoneManager;

	@Override
	public void process(InterviewConfirmationLifecycleDTO data) throws Exception {
		int nextWorkFlowTurn = data.getWorkflowTurn() + 1;
		final String interviewConfirmationStatus = this.interviewConfirmationManager
				.getInterviewConfirmationStatus(data.getInterviewId());
		Long waitTimeForNextNotification = null;
		Long interviewStartDate = data.getInterview().getStartDate();

		if (interviewConfirmationStatus.equals(InterviewStatus.CONFIRMED.getValue())
				|| nextWorkFlowTurn == WORKFLOW_TURN_FOR_REMINDER_FLOW) {
			waitTimeForNextNotification = this.getWaitTimeForReminderFlow(interviewStartDate);
		} else {
			final List<String> candidateWaitTimesPerTurn = this.appConfigProperties
					.getListOfString(DYNAMO_TIME_TO_WAIT_PER_TURN);
			waitTimeForNextNotification = this.confirmationUtils.findTimeXMinsBeforeExcludingNonOpHrs(
					Integer.parseInt(candidateWaitTimesPerTurn.get(nextWorkFlowTurn - 1)),
					interviewStartDate, this.timezoneManager.getTimezoneOfCandidate(data.getInterviewId()));
		}

		data.setInterviewConfirmationStatus(interviewConfirmationStatus);
		data.setWorkflowTurn(nextWorkFlowTurn);
		data.setTimestampToWaitUntil(this.utilities.getFormattedDateString(waitTimeForNextNotification,
				TIMEZONE_UTC, DateUtils.DATEFORMAT_ISO_8601));
	}

	private Long getWaitTimeForReminderFlow(Long interviewStartDate) {
		final int interviewReminderWaitTime = this.appConfigProperties
				.getInt(DYNAMO_INTERVIEW_REMINDER_WAIT_TIME);
		return interviewStartDate
				- ((long) interviewReminderWaitTime * SECONDS_IN_A_MINUTE);
	}
}
