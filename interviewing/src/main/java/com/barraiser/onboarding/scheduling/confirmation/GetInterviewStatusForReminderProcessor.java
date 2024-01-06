/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.confirmation;

import com.barraiser.common.graphql.types.Interview;
import com.barraiser.onboarding.dal.InterviewHistoryDAO;
import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.interview.InterviewHistoryManager;
import com.barraiser.onboarding.scheduling.confirmation.dto.InterviewConfirmationLifecycleDTO;
import com.barraiser.onboarding.scheduling.lifecycle.DTO.InterviewConfirmationStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GetInterviewStatusForReminderProcessor implements ConfirmationProcessor {

	private final InterviewConfirmationManager interviewConfirmationManager;
	private final InterviewHistoryManager interviewHistoryManager;

	@Override
	public void process(InterviewConfirmationLifecycleDTO data) throws Exception {
		final String interviewConfirmationStatus = this.interviewConfirmationManager
				.getInterviewConfirmationStatus(data.getInterviewId());
		final boolean isInterviewCancelled = this.getInterviewStatus(data.getInterview());
		if (isInterviewCancelled)
			data.setInterviewConfirmationStatus(InterviewConfirmationStatus.DENIED.name());
		else
			data.setInterviewConfirmationStatus(interviewConfirmationStatus);
	}

	private boolean getInterviewStatus(Interview interview) {
		final InterviewHistoryDAO interviewHistoryDAO = this.interviewHistoryManager
				.getLatestByFieldValueAndReschedulingCount(
						interview.getId(),
						InterviewStatus.CANCELLATION_DONE.getValue(),
						interview.getRescheduleCount());
		return interviewHistoryDAO != null;
	}
}
