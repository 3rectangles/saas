/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling;

import com.barraiser.common.model.CreateCalendarEventRequest;
import com.barraiser.onboarding.common.MustacheFormattingUtil;
import com.barraiser.onboarding.communication.client.CalendaringServiceClient;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.barraiser.common.constants.Constants.INTERVIEW_COMMUNICATION_MASTER_EMAIL;

@Log4j2
@Component
@AllArgsConstructor
public class SendTaCalendarInviteProcessor implements SchedulingProcessor {

	public static final String SCHEDULED_INTERVIEW_SUMMARY_FOR_TA = "BarRaiser Interview Assigned";
	public static final String INTERVIEW_SCHEDULED_TA_CALENDAR_INVITE_TEMPLATE = "interview_scheduled_ta_calendar_invite_template";

	private final CalendaringServiceClient calendaringServiceClient;
	private final MustacheFormattingUtil mustacheFormattingUtil;

	@Override
	public void process(final SchedulingProcessingData data) throws IOException {
		if (!data.getExecuteTaAssignment() || !data.getIsTaAllocated())
			return;
		final SchedulingCommunicationData schedulingCommunicationData = data.getSchedulingCommunicationData();
		final CreateCalendarEventRequest calendarCreateEventRequest = this
				.constructCalenderEventCreationRequest(schedulingCommunicationData);
		this.calendaringServiceClient.sendCalendarInvite(calendarCreateEventRequest);
	}

	private CreateCalendarEventRequest constructCalenderEventCreationRequest(
			final SchedulingCommunicationData schedulingCommunicationData) throws IOException {

		final List<String> attendeeEmailIds = this.getAttendees(schedulingCommunicationData);

		final Long interviewStartTime = schedulingCommunicationData.getInterviewDAO().getStartDate();
		final String taTimeZone = schedulingCommunicationData.getTaData().get("taTimeZone");

		final Long taStartTimeEpoch = interviewStartTime;
		final Long taEndTimeEpoch = interviewStartTime
				+ schedulingCommunicationData.getInterviewStructureDAO().getDuration() * 60;

		final String summary = SCHEDULED_INTERVIEW_SUMMARY_FOR_TA;

		final String templateName = INTERVIEW_SCHEDULED_TA_CALENDAR_INVITE_TEMPLATE;

		final CreateCalendarEventRequest calendarCreateEventRequest = CreateCalendarEventRequest.builder()
				.entityId(schedulingCommunicationData.getInterviewDAO().getId())
				.entityType("interview")
				.entityRescheduleCount(schedulingCommunicationData.getInterviewDAO().getRescheduleCount())
				.senderEmail(INTERVIEW_COMMUNICATION_MASTER_EMAIL)
				.attendeeEmails(attendeeEmailIds)
				.summary(summary)
				.description(
						this.mustacheFormattingUtil.formatDataToText(
								templateName,
								schedulingCommunicationData.getTaData()))
				.startTimeEpoch(taStartTimeEpoch)
				.endTimeEpoch(taEndTimeEpoch)
				.timezone(taTimeZone)
				.recipientId(schedulingCommunicationData.getInterviewDAO().getTaggingAgent())
				.build();

		return calendarCreateEventRequest;
	}

	private List<String> getAttendees(
			final SchedulingCommunicationData schedulingCommunicationData) {
		final List<String> attendeeEmailIds = new ArrayList<>();
		final String taEmailId = schedulingCommunicationData.getTaData().get("taEmailId");
		attendeeEmailIds.add(taEmailId);
		attendeeEmailIds.add(INTERVIEW_COMMUNICATION_MASTER_EMAIL);

		return attendeeEmailIds;
	}
}
