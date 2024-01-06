/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling;

import com.barraiser.common.model.CreateCalendarEventRequest;
import com.barraiser.onboarding.common.MustacheFormattingUtil;
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
public class ConstructCalendarInviteForExpertProcessor {
	public static final String SCHEDULED_INTERVIEW_SUMMARY_FOR_EXPERT = "Interview Scheduled";
	private final MustacheFormattingUtil mustacheFormattingUtil;

	public CreateCalendarEventRequest constructCalenderEventCreationRequest(
			final SchedulingCommunicationData schedulingCommunicationData) throws IOException {

		final List<String> attendeeEmailIds = this.getAttendees(schedulingCommunicationData);

		final Long interviewStartTime = schedulingCommunicationData.getStartDate() != null
				? schedulingCommunicationData.getStartDate()
				: schedulingCommunicationData.getInterviewDAO().getStartDate();
		final String expertTimeZone = schedulingCommunicationData.getExpertData().get("expertTimeZone");

		final Long expertStartTimeEpoch = interviewStartTime
				+ schedulingCommunicationData
						.getInterviewStructureDAO()
						.getExpertJoiningTime()
						* 60;
		final Long expertEndTimeEpoch = interviewStartTime
				+ schedulingCommunicationData.getInterviewStructureDAO().getDuration() * 60;

		final String summary = SCHEDULED_INTERVIEW_SUMMARY_FOR_EXPERT;

		final String templateName = schedulingCommunicationData
				.getInterviewRoundTypeConfigurationDAO()
				.getInterviewScheduledExpertCalendarInviteTemplate();

		final CreateCalendarEventRequest calendarCreateEventRequest = CreateCalendarEventRequest.builder()
				.entityId(schedulingCommunicationData.getInterviewDAO().getId())
				.entityType("interview")
				.entityRescheduleCount(schedulingCommunicationData.getInterviewDAO().getRescheduleCount())
				.senderEmail(INTERVIEW_COMMUNICATION_MASTER_EMAIL)
				.attendeeEmails(attendeeEmailIds)
				.summary(summary)
				.description(
						this.mustacheFormattingUtil.formatObjectDataToText(
								templateName,
								schedulingCommunicationData.getCommunicationData()))
				.startTimeEpoch(expertStartTimeEpoch)
				.endTimeEpoch(expertEndTimeEpoch)
				.timezone(expertTimeZone)
				.recipientId(schedulingCommunicationData.getExpertId())
				.build();

		return calendarCreateEventRequest;

	}

	private List<String> getAttendees(
			final SchedulingCommunicationData schedulingCommunicationData) {
		final List<String> attendeeEmailIds = new ArrayList<>();
		final String expertEmailId = schedulingCommunicationData.getExpertData().get("expertEmailId");
		attendeeEmailIds.add(expertEmailId);
		attendeeEmailIds.add(INTERVIEW_COMMUNICATION_MASTER_EMAIL);

		return attendeeEmailIds;
	}

}
