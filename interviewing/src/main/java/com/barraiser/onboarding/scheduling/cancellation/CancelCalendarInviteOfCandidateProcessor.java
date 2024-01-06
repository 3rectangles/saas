/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.onboarding.communication.client.CalendaringServiceClient;
import com.barraiser.onboarding.dal.InterviewDAO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CancelCalendarInviteOfCandidateProcessor implements CancellationProcessor {
	private final CalendaringServiceClient calendaringServiceClient;

	@Override
	public void process(CancellationProcessingData data) throws Exception {
		final InterviewDAO interviewDAO = data.getPreviousStateOfCancelledInterview();

		this.calendaringServiceClient
				.cancelCalendarInviteForUserIdAndEntityWithRescheduleCount(
						interviewDAO.getId(),
						interviewDAO.getIntervieweeId(),
						interviewDAO.getRescheduleCount());
	}
}
