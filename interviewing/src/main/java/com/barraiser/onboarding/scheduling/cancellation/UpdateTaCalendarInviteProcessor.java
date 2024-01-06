/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.common.model.CreateCalendarEventRequest;
import com.barraiser.onboarding.common.MustacheFormattingUtil;
import com.barraiser.onboarding.communication.client.CalendaringServiceClient;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

import static com.barraiser.common.constants.Constants.INTERVIEW_COMMUNICATION_MASTER_EMAIL;

@Log4j2
@Component
@AllArgsConstructor
public class UpdateTaCalendarInviteProcessor implements CancellationProcessor {

	public static final String SCHEDULED_INTERVIEW_SUMMARY_FOR_TA = "BarRaiser Interview Assigned";
	public static final String INTERVIEW_REASSIGNED_TA_CALENDAR_INVITE_TEMPLATE = "interview_reassigned_ta_calendar_invite_template";

	private final CalendaringServiceClient calendaringServiceClient;
	private final MustacheFormattingUtil mustacheFormattingUtil;
	private final UserDetailsRepository userDetailsRepository;

	@Override
	public void process(final CancellationProcessingData data) throws IOException {
		if (!data.getIsTaAutoAllocationEnabled()
				|| Objects.isNull(data.getInterviewForTaReassignment())) {
			return;
		}

		Map<String, String> taData = this.formDataForTa(data.getInterviewForTaReassignment());

		final CreateCalendarEventRequest calendarCreateEventRequest = this.constructCalenderEventCreationRequest(
				data.getInterviewForTaReassignment(),
				taData);
		// remove try catch once we move send calendar invite to ta from zapier to
		// backend in case where our team manually assigns ta
		try {
			this.calendaringServiceClient
					.updateCalendarInvite(
							data
									.getInterviewToBeCancelled()
									.getId(),
							data
									.getInterviewForTaReassignment()
									.getTaggingAgent(),
							calendarCreateEventRequest);
		} catch (final Exception e) {
			log.error(e, e);
		}
	}

	private CreateCalendarEventRequest constructCalenderEventCreationRequest(
			final InterviewDAO interviewDAO, final Map<String, String> taData) throws IOException {

		final List<String> attendeeEmailIds = this.getAttendees(interviewDAO, taData);

		final Long interviewStartTime = interviewDAO.getStartDate();
		final String taTimeZone = taData.get("ta_time_zone");

		final Long taStartTimeEpoch = interviewStartTime;
		final Long taEndTimeEpoch = interviewDAO.getEndDate();

		final String summary = SCHEDULED_INTERVIEW_SUMMARY_FOR_TA;

		final String templateName = INTERVIEW_REASSIGNED_TA_CALENDAR_INVITE_TEMPLATE;

		final CreateCalendarEventRequest calendarCreateEventRequest = CreateCalendarEventRequest.builder()
				.entityId(interviewDAO.getId())
				.entityType("interview")
				.entityRescheduleCount(interviewDAO.getRescheduleCount())
				.senderEmail(INTERVIEW_COMMUNICATION_MASTER_EMAIL)
				.attendeeEmails(attendeeEmailIds)
				.summary(summary)
				.description(
						this.mustacheFormattingUtil.formatDataToText(
								templateName,
								taData))
				.startTimeEpoch(taStartTimeEpoch)
				.endTimeEpoch(taEndTimeEpoch)
				.timezone(taTimeZone)
				.recipientId(interviewDAO.getTaggingAgent())
				.build();

		return calendarCreateEventRequest;
	}

	private List<String> getAttendees(
			final InterviewDAO interviewDAO, Map<String, String> taData) {
		final List<String> attendeeEmailIds = new ArrayList<>();
		final String taEmailId = taData.get("ta_emailId");
		attendeeEmailIds.add(taEmailId);
		attendeeEmailIds.add(INTERVIEW_COMMUNICATION_MASTER_EMAIL);

		return attendeeEmailIds;
	}

	private final Map<String, String> formDataForTa(final InterviewDAO interviewDAO) {
		final Map<String, String> taData = new HashMap<>();
		final UserDetailsDAO taDetail = this.userDetailsRepository.findById(interviewDAO.getTaggingAgent())
				.get();

		final String taFirstName = taDetail.getFirstName();
		final String taTimeZone = taDetail.getTimezone();

		taData.put("ta_name", taFirstName);
		taData.put("ta_emailId", taDetail.getEmail());
		taData.put("interview_id", interviewDAO.getId());
		taData.put("ta_time_zone", taTimeZone);

		return taData;
	}
}
