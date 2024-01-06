/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.calendar_interception.gcal.pipeline;

import com.barraiser.ats_integrations.calendar_interception.CalendarInterceptionHelper;
import com.barraiser.ats_integrations.calendar_interception.RegexMatchingHelper;
import com.barraiser.ats_integrations.calendar_interception.SaasInterviewerManagementHelper;
import com.barraiser.ats_integrations.calendar_interception.SchedulingProcessing;
import com.barraiser.ats_integrations.calendar_interception.dto.SchedulingData;
import com.barraiser.ats_integrations.common.client.JobRoleInterviewStructureMappingFeignClient;
import com.barraiser.ats_integrations.common.client.JobRoleManagementFeignClient;
import com.barraiser.ats_integrations.errorhandling.ATSAnomalyException;
import com.barraiser.common.graphql.types.JobRole;
import com.barraiser.commons.eventing.schema.commons.calendar.BRCalendarEvent;
import com.barraiser.commons.eventing.schema.commons.calendar.ConferenceEntryChannel;
import com.barraiser.commons.eventing.schema.commons.calendar.ConferencingSolutionConfig;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@AllArgsConstructor
@Component
@Log4j2
public class FullyRelaxedMeetingSchedulingSetupDataAccumulator implements SchedulingProcessing {
	private final JobRoleInterviewStructureMappingFeignClient jobRoleInterviewStructureMappingFeignClient;
	private final JobRoleManagementFeignClient jobRoleManagementFeignClient;
	private final SaasInterviewerManagementHelper saasInterviewerManagementHelper;
	private final CalendarInterceptionHelper calendarInterceptionHelper;

	private final static String ZOOM_MEETING_URL_SUFFIX = "zoom.us";
	private final static String ZOOM_MEET_REGEX = "^http.*zoom.*$";

	private final RegexMatchingHelper regexMatchingHelper;

	@Override
	public void process(SchedulingData data) throws IOException, ATSAnomalyException {
		data.setInterviewDuration(this.getInterviewDuration(data.getBrCalendarEvent()));

		data.setPocEmails(this.calendarInterceptionHelper.getPocEmails(data.getBrCalendarEvent()));

		data.setInterviewStart(this.getInterviewStartDate(data.getBrCalendarEvent()));
		data.setInterviewEnd(this.getInterviewEndDate(data.getBrCalendarEvent()));
		data.setOriginalInviteBody(data.getBrCalendarEvent().getDescription());
		data.setOriginalInviteEventId(data.getBrCalendarEvent().getProviderEventId());
		data.setAtsMeetingLink(this.getAtsMeetingLink(data));

		data.setInterviewerEmailId(this.saasInterviewerManagementHelper.getInterviewerEmail(data.getPartnerId(),
				data.getBrCalendarEvent()));

		data.setInterviewAttendeeEmails(
				this.saasInterviewerManagementHelper.getInterviewAttendeeEmails(data.getBrCalendarEvent(), data
						.getInterviewerEmailId(), data.getPartnerId()));

		// Check if partner has more than one interview structure(default),
		// if yes, set Interview Structure as null
		// else, set it to default
		data.setBrInterviewStructureId(this.getInterviewStructureId(data.getPartnerId()));

		data.setBrJobRoleId(this.getJobRoleId(data.getBrInterviewStructureId()));

	}

	private Double getInterviewDuration(BRCalendarEvent event) {
		return (double) Duration.between(event.getStart(), event.getEnd()).toMinutes();
	}

	private Long getInterviewStartDate(final BRCalendarEvent event) {
		return event.getStart().toEpochSecond();
	}

	private Long getInterviewEndDate(final BRCalendarEvent event) {
		return event.getEnd().toEpochSecond();
	}

	private String getAtsMeetingLink(final SchedulingData data) throws ATSAnomalyException {

		if (data.getBrCalendarEvent().getConferencingSolutionConfig() != null) {
			for (final ConferencingSolutionConfig.ConferenceEntryChannelConfig conferenceEntryChannelConfig : data
					.getBrCalendarEvent()
					.getConferencingSolutionConfig().getConferenceEntryChannelConfig()) {
				if (ConferenceEntryChannel.VIDEO.equals(conferenceEntryChannelConfig.getEntryPointType())) {
					return conferenceEntryChannelConfig.getJoiningLink();
				}
			}
		} else if (data.getBrCalendarEvent().getLocation().contains(ZOOM_MEETING_URL_SUFFIX)) {
			List<String> matchedRegex = regexMatchingHelper.getMatchedValuesForRegex(
					data.getBrCalendarEvent().getLocation(),
					ZOOM_MEET_REGEX);
			if (!matchedRegex.isEmpty()) {
				return matchedRegex.get(0);
			}
		}

		log.warn("Conferencing Solution Config is not present");
		throw new ATSAnomalyException("", "", 1001);
	}

	private String getInterviewStructureId(final String partnerId) {
		return this.jobRoleInterviewStructureMappingFeignClient.getInterviewStructureId(partnerId);
	}

	private String getJobRoleId(final String interviewStructureId) {
		if (interviewStructureId == null) {
			return null;
		}

		final JobRole jobRole = this.jobRoleManagementFeignClient.getJobRole(interviewStructureId)
				.getBody();

		return jobRole == null ? null : jobRole.getId();
	}
}
