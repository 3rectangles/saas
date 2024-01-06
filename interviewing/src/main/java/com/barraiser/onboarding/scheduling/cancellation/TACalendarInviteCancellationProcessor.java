/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.onboarding.communication.client.CalendaringServiceClient;
import com.barraiser.onboarding.dal.InterviewDAO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TACalendarInviteCancellationProcessor implements CancellationProcessor {
	private final CalendaringServiceClient calendaringServiceClient;

	@Override
	public void process(CancellationProcessingData data) throws Exception {
		if (!data.getIsTaAssigned()) {
			return;
		}

		final InterviewDAO interviewDAO = data.getInterviewToBeCancelled();

		this.calendaringServiceClient
				.cancelCalendarInviteForUserIdAndEntityWithRescheduleCount(
						interviewDAO.getId(),
						interviewDAO.getTaggingAgent(),
						interviewDAO.getRescheduleCount());
	}
}
