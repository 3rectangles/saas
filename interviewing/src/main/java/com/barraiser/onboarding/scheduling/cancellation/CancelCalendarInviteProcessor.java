/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.onboarding.communication.client.CalendaringServiceClient;
import com.barraiser.onboarding.dal.InterviewDAO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CancelCalendarInviteProcessor implements CancellationProcessor {
	private final CalendaringServiceClient calendaringServiceClient;

	@Override
	public void process(final CancellationProcessingData data) throws Exception {
		final InterviewDAO interview = data.getInterviewToBeCancelled();

		this.calendaringServiceClient
				.cancelCalendarInvitesForEntityWithRescheduleCount(
						interview.getId(),
						data.getInterviewRescheduleCount());
	}
}
