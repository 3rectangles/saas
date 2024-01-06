/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.calendar_interception.gcal.pipeline;

import com.barraiser.ats_integrations.calendar_interception.CalendarInterceptionHelper;
import com.barraiser.ats_integrations.calendar_interception.RegexMatchingHelper;
import com.barraiser.ats_integrations.calendar_interception.SchedulingProcessing;
import com.barraiser.ats_integrations.calendar_interception.dto.SchedulingData;
import com.barraiser.ats_integrations.calendar_interception.variable_mapping.ReplacementCommunicationVariablesAccumulator;
import com.barraiser.ats_integrations.common.client.CalendarClient;
import com.barraiser.ats_integrations.dal.ATSCommunicationTemplateConfigDAO;
import com.barraiser.ats_integrations.dal.ATSCommunicationTemplateConfigRepository;
import com.barraiser.commons.dto.calendarManagement.UpdateCalendarEventRequest;
import com.barraiser.commons.dto.enums.CalendarProvider;
import com.barraiser.commons.dto.enums.ConferenceEntryChannel;
import com.barraiser.commons.dto.enums.ConferencingProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Component
public class CalendarInviteUpdator implements SchedulingProcessing {

	private final ATSCommunicationTemplateConfigRepository atsCommunicationTemplateConfigRepository;
	private final CalendarClient calendarClient;
	private final RegexMatchingHelper regexMatchingHelper;
	private final ReplacementCommunicationVariablesAccumulator replacementCommunicationVariablesAccumulator;
	private final CalendarInterceptionHelper calendarInterceptionHelper;

	private final static String EVENT_TYPE_SCHEDULING = "SCHEDULING";
	private final static String JOIN_MEETING_TITLE = "Interview With Barraiser";
	private static final String BR_INTERVIEW_FLOW_LINK = "BR_INTERVIEW_FLOW_LINK";
	private static final String SELF_SERVE_BODY_REPLACEMENT_GOOGLE_TEMPLATE = "Feedback link - ${BR_INTERVIEW_FEEDBACK_LINK}\n"
			+
			"Fall back meeting joining link - ${INTERVIEW_JOINING_FALLBACK_LINK}\n\n\n" +
			"${ORIGINAL_EVENT}";

	private static final String SELF_SERVE_BODY_REPLACEMENT_OUTLOOK_TEMPLATE = "<body> Feedback link - <a href= \" ${BR_INTERVIEW_FEEDBACK_LINK} \" > ${BR_INTERVIEW_FEEDBACK_LINK} </a><br>"
			+
			"Fall back meeting joining link - <a href= \" ${INTERVIEW_JOINING_FALLBACK_LINK} \" > ${INTERVIEW_JOINING_FALLBACK_LINK} </a><br><br><br>  ${ORIGINAL_EVENT}";

	@Override
	public void process(SchedulingData data) throws IOException {

		data.setReplacementInviteVariableValueMapping(this.replacementCommunicationVariablesAccumulator
				.getReplacementValues(data));

		final Optional<ATSCommunicationTemplateConfigDAO> atsCommunicationTemplateConfigDAOOptional = this.atsCommunicationTemplateConfigRepository
				.findByPartnerIdAndAtsProviderAndEventType(data.getPartnerId(), data.getAtsProvider(),
						EVENT_TYPE_SCHEDULING);

		final CalendarProvider calendarProvider = this.calendarInterceptionHelper
				.getCalendarProvider(data.getBrCalendarEvent().getEventType());

		String updatedMeetingSubject;
		String updatedMeetingBody = null;
		if (atsCommunicationTemplateConfigDAOOptional.isPresent()) {
			ATSCommunicationTemplateConfigDAO atsCommunicationTemplateConfigDAO = atsCommunicationTemplateConfigDAOOptional
					.get();

			updatedMeetingSubject = atsCommunicationTemplateConfigDAO.getSubjectReplacementTemplate() != null
					? this.regexMatchingHelper.replaceValues(
							atsCommunicationTemplateConfigDAO.getSubjectReplacementTemplate(),
							data.getReplacementInviteVariableValueMapping())
					: data.getBrCalendarEvent().getSummary();

			updatedMeetingBody = atsCommunicationTemplateConfigDAO.getBodyReplacementTemplate() != null
					? this.regexMatchingHelper.replaceValues(
							atsCommunicationTemplateConfigDAO.getBodyReplacementTemplate(),
							data.getReplacementInviteVariableValueMapping())
					: data.getBrCalendarEvent().getDescription();
		} else {
			updatedMeetingSubject = data.getBrCalendarEvent().getSummary();

			if (calendarProvider.equals(CalendarProvider.OUTLOOK)) {
				updatedMeetingBody = this.regexMatchingHelper.replaceValues(
						SELF_SERVE_BODY_REPLACEMENT_OUTLOOK_TEMPLATE, data.getReplacementInviteVariableValueMapping());
			}
			if (calendarProvider.equals(CalendarProvider.GOOGLE)) {
				updatedMeetingBody = this.regexMatchingHelper.replaceValues(
						SELF_SERVE_BODY_REPLACEMENT_GOOGLE_TEMPLATE,
						data.getReplacementInviteVariableValueMapping());
			}
		}

		final String updatedJoiningLink = this.getUpdatedMeetingLink(data);

		for (String pocEmail : data.getPocEmails()) {
			this.updateMeetingInvite(pocEmail, data.getOriginalInviteEventId(), updatedMeetingSubject,
					updatedMeetingBody,
					updatedJoiningLink,
					calendarProvider);
		}
	}

	private String getUpdatedMeetingLink(final SchedulingData schedulingData) {
		return schedulingData.getReplacementInviteVariableValueMapping().get(BR_INTERVIEW_FLOW_LINK);
	}

	private void updateMeetingInvite(final String userEmail, final String eventId, final String subject,
			final String inviteBody,
			final String meetingLink,
			final CalendarProvider calendarProvider) {
		this.calendarClient.updateEvent(userEmail, eventId, UpdateCalendarEventRequest.builder()
				.summary(subject)
				.description(inviteBody)
				.meetingLink(meetingLink)
				.calendarProvider(calendarProvider)
				.conferencingSolutionConfig(UpdateCalendarEventRequest.ConferencingSolutionConfig.builder()
						.joiningLinkTitle(JOIN_MEETING_TITLE)
						.conferencingProvider(ConferencingProvider.BARRAISER_INTERVIEWING_PLATFORM)
						.conferenceEntryChannelConfig(
								List.of(
										UpdateCalendarEventRequest.ConferencingSolutionConfig.ConferenceEntryChannelConfig
												.builder()
												.entryPointType(ConferenceEntryChannel.VIDEO)
												.joiningLink(meetingLink)
												.build()))
						.build())
				.build());

	}

}
