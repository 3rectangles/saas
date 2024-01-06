/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.common.model.CreateCalendarEventRequest;
import com.barraiser.onboarding.communication.InterviewSchedulingCommunicationService;
import com.barraiser.onboarding.communication.client.CalendaringServiceClient;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingCommunicationData;
import com.barraiser.onboarding.scheduling.scheduling.ConstructCalendarInviteForExpertProcessor;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Log4j2
@Component
@AllArgsConstructor
public class SendUpdatedCalendarInviteToExpertProcessor implements CancellationProcessor {
	private final ConstructCalendarInviteForExpertProcessor constructCalendarInviteForExpertProcessor;
	private final InterviewSchedulingCommunicationService interviewSchedulingCommunicationService;
	private final CalendaringServiceClient calendaringServiceClient;

	@Override
	public void process(final CancellationProcessingData data) throws Exception {

		final SchedulingCommunicationData schedulingCommunicationData = this.prepareDataForCommunication(data);
		final CreateCalendarEventRequest calendarCreateEventRequest = this.constructCalendarInviteForExpertProcessor
				.constructCalenderEventCreationRequest(schedulingCommunicationData);
		this.calendaringServiceClient.sendCalendarInvite(calendarCreateEventRequest);

	}

	private SchedulingCommunicationData prepareDataForCommunication(final CancellationProcessingData data)
			throws IOException {
		return this.interviewSchedulingCommunicationService
				.prepareInterviewScheduledCommunicationData(data.getInterviewThatExpertCanTake());
	}
}
