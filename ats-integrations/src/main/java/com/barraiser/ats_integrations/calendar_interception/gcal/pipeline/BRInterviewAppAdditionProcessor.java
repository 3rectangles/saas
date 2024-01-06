/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.calendar_interception.gcal.pipeline;

import com.barraiser.ats_integrations.calendar_interception.CalendarInterceptionHelper;
import com.barraiser.ats_integrations.calendar_interception.RegexMatchingHelper;
import com.barraiser.ats_integrations.calendar_interception.SchedulingProcessing;
import com.barraiser.ats_integrations.calendar_interception.dto.SchedulingData;
import com.barraiser.ats_integrations.common.client.CalendarClient;
import com.barraiser.ats_integrations.errorhandling.ATSAnomalyException;
import com.barraiser.commons.dto.calendarManagement.AddBRAppToEventRequest;
import com.barraiser.commons.dto.enums.CalendarProvider;
import com.barraiser.commons.dto.enums.ConferencingProvider;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

import static com.barraiser.common.utilities.UrlUtil.getDecodedURL;

@Log4j2
@AllArgsConstructor
@Component
public class BRInterviewAppAdditionProcessor implements SchedulingProcessing {

	private final CalendarInterceptionHelper calendarInterceptionHelper;
	private final RegexMatchingHelper regexMatchingHelper;
	private final CalendarClient calendarClient;

	private static final String TEAMS_CHAT_THREAD_REGEX = "(?<=join\\/)[^\\/]+(@thread\\.v2)";
	private static final String MEETING_CHAT_THREAD_ID = "meetingChatThreadId";

	@Override
	public void process(SchedulingData data) throws IOException, ATSAnomalyException {
		this.installBRAssistantApp(data);
	}

	private void installBRAssistantApp(SchedulingData data) {
		CalendarProvider calendarProvider = this.calendarInterceptionHelper
				.getCalendarProvider(data.getBrCalendarEvent().getEventType());

		// Checking for Outlook calendar to ensure OAuth provider of user email is
		// outlook.
		if (calendarProvider.equals(CalendarProvider.OUTLOOK)) {
			if (data.getAtsMeetingLink().contains("teams.microsoft")) {
				final String outlookChatThreadId = regexMatchingHelper
						.getMatchedValuesForRegex(getDecodedURL(data.getAtsMeetingLink()), TEAMS_CHAT_THREAD_REGEX)
						.get(0);

				HashMap<String, String> requestConfig = new HashMap<>();

				requestConfig.put(MEETING_CHAT_THREAD_ID, outlookChatThreadId);

				this.calendarClient.addApplication(
						AddBRAppToEventRequest.builder()
								.interviewId(data.getBrInterviewId())
								.conferencingProvider(ConferencingProvider.TEAMS)
								.userEmail(data.getPocEmails().get(0))
								.requestConfig(requestConfig)
								.build());
			}
		}
	}

}
