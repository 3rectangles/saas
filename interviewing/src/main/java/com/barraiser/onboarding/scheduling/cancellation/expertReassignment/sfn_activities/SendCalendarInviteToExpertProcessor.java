/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment.sfn_activities;

import com.barraiser.common.model.CreateCalendarEventRequest;
import com.barraiser.onboarding.communication.client.CalendaringServiceClient;
import com.barraiser.onboarding.interview.InterviewUtil;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO.ExpertAllocatorData;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingCommunicationData;
import com.barraiser.onboarding.scheduling.scheduling.ConstructCalendarInviteForExpertProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Log4j2
@Component
@AllArgsConstructor
public class SendCalendarInviteToExpertProcessor implements ExpertAllocatorSfnActivity {
	public static final String SEND_CALENDAR_INVITE_TO_EXPERT_ACTIVITY_NAME = "send-calendar-invite-to-expert";

	private final CalendaringServiceClient calendaringServiceClient;
	private final ConstructCalendarInviteForExpertProcessor constructCalendarInviteForExpertProcessor;
	private final ObjectMapper objectMapper;
	private final InterviewUtil interviewUtil;

	@Override
	public String name() {
		return SEND_CALENDAR_INVITE_TO_EXPERT_ACTIVITY_NAME;
	}

	@Override
	public ExpertAllocatorData process(final String input) throws IOException {
		final ExpertAllocatorData data = this.objectMapper.readValue(input, ExpertAllocatorData.class);
		final SchedulingCommunicationData schedulingCommunicationData = data.getSchedulingCommunicationData();

		if (!this.interviewUtil.isScheduledViaATSCalInterception(data.getSchedulingPlatform())) {
			final CreateCalendarEventRequest calendarCreateEventRequest = this.constructCalendarInviteForExpertProcessor
					.constructCalenderEventCreationRequest(schedulingCommunicationData);

			if (Boolean.TRUE.equals(data.getIsOnlyCandidateChanged())
					&& data.getPreviousInterviewOfExpert() != null) {

				this.calendaringServiceClient
						.updateCalendarInvite(
								data.getPreviousInterviewOfExpert(),
								data.getInterviewerId(),
								calendarCreateEventRequest);
			} else {
				this.calendaringServiceClient
						.sendCalendarInvite(calendarCreateEventRequest);
			}
		}

		return data;
	}
}
