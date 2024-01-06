/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.sfn;

import com.barraiser.common.model.CreateCalendarEventRequest;
import com.barraiser.onboarding.communication.client.CalendaringServiceClient;
import com.barraiser.onboarding.interview.InterviewUtil;
import com.barraiser.onboarding.scheduling.scheduling.ConstructCalendarInviteForExpertProcessor;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingCommunicationData;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingProcessingData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class SendInterviewScheduledCalendarInviteToExpertActivity implements InterviewSchedulingActivity {
	public static final String SEND_INTERVIEW_SCHEDULED_CALENDAR_INVITE_TO_EXPERT = "send-interview-scheduled-calendar-invite-to-expert";

	private final CalendaringServiceClient calendaringServiceClient;
	private final ConstructCalendarInviteForExpertProcessor constructCalendarInviteForExpertProcessor;
	private final ObjectMapper objectMapper;
	private final InterviewUtil interviewUtil;

	@Override
	public String name() {
		return SEND_INTERVIEW_SCHEDULED_CALENDAR_INVITE_TO_EXPERT;
	}

	@Override
	public SchedulingProcessingData process(String input) throws Exception {
		final SchedulingProcessingData data = this.objectMapper.readValue(input, SchedulingProcessingData.class);
		final SchedulingCommunicationData schedulingCommunicationData = data.getSchedulingCommunicationData();

		if (!this.interviewUtil.isScheduledViaATSCalInterception(data.getInput().getSchedulingPlatform())) {
			final CreateCalendarEventRequest calendarCreateEventRequest = this.constructCalendarInviteForExpertProcessor
					.constructCalenderEventCreationRequest(schedulingCommunicationData);
			this.calendaringServiceClient.sendCalendarInvite(calendarCreateEventRequest);
		}

		return data;
	}
}
