/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.sfn;

import com.barraiser.common.model.CreateCalendarEventRequest;
import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.common.MustacheFormattingUtil;
import com.barraiser.onboarding.communication.client.CalendaringServiceClient;
import com.barraiser.onboarding.interview.InterviewService;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingCommunicationData;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingProcessingData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.barraiser.common.constants.Constants.INTERVIEW_COMMUNICATION_MASTER_EMAIL;

@Log4j2
@Component
@AllArgsConstructor
public class SendInterviewScheduledCalendarInviteToCandidateActivity implements InterviewSchedulingActivity {
	public static final String SEND_INTERVIEW_SCHEDULED_CALENDAR_INVITE_TO_CANDIDATE = "send-interview-scheduled-calendar-invite-to-candidate";
	public static final String SCHEDULED_INTERVIEW_SUMMARY_FOR_CANDIDATE = "%s (%s) Interview - %s";
	public static final String INTERVIEW_DATE_TIME_FORMAT = "dd MMM uuuu, hh:mm a z";

	private final CalendaringServiceClient calendaringServiceClient;
	private final MustacheFormattingUtil mustacheFormattingUtil;
	private final DateUtils utilities;
	private final ObjectMapper objectMapper;
	private final InterviewService interviewService;

	private CreateCalendarEventRequest constructCalenderEventCreationRequest(
			final SchedulingCommunicationData schedulingCommunicationData) throws IOException {

		final List<String> attendeeEmailIds = this.getAttendees(schedulingCommunicationData);

		final Long interviewStartTime = schedulingCommunicationData.getInterviewDAO().getStartDate();
		final String candidateTimeZone = schedulingCommunicationData.getCandidateData().get("candidateTimeZone");

		final Long candidateStartTimeEpoch = interviewStartTime;
		final Long candidateEndTimeEpoch = interviewStartTime
				+ schedulingCommunicationData.getInterviewStructureDAO().getDuration() * 60;

		final String candidateStartDatetime = this.utilities.getFormattedDateString(
				candidateStartTimeEpoch, candidateTimeZone, INTERVIEW_DATE_TIME_FORMAT);
		final String candidateEndDatetime = this.utilities.getFormattedDateString(
				candidateEndTimeEpoch, candidateTimeZone, INTERVIEW_DATE_TIME_FORMAT);

		final HashMap<String, Object> inviteDataMap = new HashMap<String, Object>();
		inviteDataMap.putAll(schedulingCommunicationData.getCommunicationData());
		inviteDataMap.put("candidate_start_datetime", candidateStartDatetime);
		inviteDataMap.put("candidate_end_datetime", candidateEndDatetime);

		final InterviewDAO interviewDAO = this.interviewService
				.findById(schedulingCommunicationData.getInterviewDAO().getId());
		inviteDataMap.put("meeting_link", interviewDAO.getMeetingLink());
		final String summary = String.format(
				SCHEDULED_INTERVIEW_SUMMARY_FOR_CANDIDATE,
				schedulingCommunicationData.getCommunicationData().get("company_name"),
				schedulingCommunicationData.getCommunicationData().get("job_role_name"),
				schedulingCommunicationData.getCommunicationData().get("candidate_name"));

		final String templateName = schedulingCommunicationData
				.getInterviewRoundTypeConfigurationDAO()
				.getInterviewScheduledCandidateCalendarInviteTemplate();

		return CreateCalendarEventRequest.builder()
				.entityId(schedulingCommunicationData.getInterviewDAO().getId())
				.entityType("interview")
				.entityRescheduleCount(schedulingCommunicationData.getInterviewDAO().getRescheduleCount())
				.senderEmail(INTERVIEW_COMMUNICATION_MASTER_EMAIL)
				.attendeeEmails(attendeeEmailIds)
				.summary(summary)
				.description(
						this.mustacheFormattingUtil.formatObjectDataToText(
								templateName, inviteDataMap))
				.startTimeEpoch(candidateStartTimeEpoch)
				.endTimeEpoch(candidateEndTimeEpoch)
				.timezone(candidateTimeZone)
				.recipientId(schedulingCommunicationData.getInterviewDAO().getIntervieweeId())
				.build();
	}

	private List<String> getAttendees(
			final SchedulingCommunicationData schedulingCommunicationData) {
		final List<String> attendeeEmailIds = new ArrayList<>();
		final String candidateEmailId = schedulingCommunicationData.getCandidateData().get("candidateEmailId");
		attendeeEmailIds.add(candidateEmailId);
		attendeeEmailIds.addAll(schedulingCommunicationData.getPocEmails());
		attendeeEmailIds.add(INTERVIEW_COMMUNICATION_MASTER_EMAIL);

		return attendeeEmailIds;
	}

	@Override
	public String name() {
		return SEND_INTERVIEW_SCHEDULED_CALENDAR_INVITE_TO_CANDIDATE;
	}

	@Override
	public SchedulingProcessingData process(String input) throws Exception {
		final SchedulingProcessingData data = this.objectMapper.readValue(input, SchedulingProcessingData.class);
		final SchedulingCommunicationData schedulingCommunicationData = data.getSchedulingCommunicationData();

		if (!schedulingCommunicationData.getIsCandidateAnonymous()) {
			final CreateCalendarEventRequest calendarCreateEventRequest = this
					.constructCalenderEventCreationRequest(schedulingCommunicationData);
			this.calendaringServiceClient.sendCalendarInvite(calendarCreateEventRequest);
		}

		return data;
	}
}
