/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.calendar_interception;

import com.barraiser.ats_integrations.common.client.CalendarClient;
import com.barraiser.ats_integrations.common.client.PartnerInformationServiceFeignClient;
import com.barraiser.ats_integrations.config.ATSPartnerConfigurationManager;
import com.barraiser.ats_integrations.dal.ATSProcessedEventsDAO;
import com.barraiser.ats_integrations.dal.ATSProcessedEventsRepository;
import com.barraiser.common.graphql.types.MeetingInterceptionConfiguration;
import com.barraiser.commons.dto.enums.CalendarProvider;
import com.barraiser.commons.eventing.schema.commons.calendar.*;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.barraiser.ats_integrations.common.Constants.ATS_SCHEDULING_LINK_REGEX_LIST;
import static com.barraiser.ats_integrations.common.Constants.SAAS_TRIAL_PARTNERSHIP_MODEL_ID;

@AllArgsConstructor
@Component
@Log4j2
public class CalendarInterceptionHelper {
	private final RegexMatchingHelper regexMatchingHelper;
	private final ATSProcessedEventsRepository atsProcessedEventsRepository;
	private final PartnerInformationServiceFeignClient partnerInformationServiceFeignClient;
	private final ATSPartnerConfigurationManager atsPartnerConfigurationManager;
	private final CalendarClient calendarClient;
	private final String BR_EVENT_URL = "app.barraiser.com";
	private final String ZOOM_MEETING_URL_SUFFIX = "zoom.us";
	// todo: add to config file

	private static final String BROUTLOOKCALENDAREVENT_STRING = "BROutlookCalendarEvent";
	private static final String BRGOOGLECALENDAREVENT_STRING = "BRGoogleCalendarEvent";

	public Boolean isATSSchedulingEvent(final String inviteBody) {

		if (inviteBody == null) {
			return Boolean.FALSE;
		}

		for (String expertJoiningLinkRegex : ATS_SCHEDULING_LINK_REGEX_LIST) {
			final List<String> matchedValues = this.regexMatchingHelper.getMatchedValuesForRegex(inviteBody,
					expertJoiningLinkRegex);

			if (matchedValues.size() > 0) {
				return Boolean.TRUE;
			}
		}

		return Boolean.FALSE;
	}

	public Boolean isCancelledEvent(final BRCalendarEvent event) {
		return EventStatus.CANCELLED.equals(event.getStatus());
	}

	public Boolean isOfThePast(final BRCalendarEvent event) {
		return event.getStart() != null
				&& event.getStart().toEpochSecond() < Instant.now().minus(Duration.ofMinutes(15)).getEpochSecond();
	}

	public Boolean isMeetingLinkNotPresent(final BRCalendarEvent event) {
		return event.getConferencingSolutionConfig() == null && event.getLocation() == null;
	}

	@SneakyThrows
	public Boolean isInterceptedEvent(final BRCalendarEvent event) {
		return this.getAtsMeetingLink(event).contains(BR_EVENT_URL);
	}

	private String getAtsMeetingLink(final BRCalendarEvent event) {

		// In case of outlook events, we update the location of the event, so we need to
		// check if location contains intercepted event URL
		if (event.getLocation() != null && event.getLocation().contains(BR_EVENT_URL)) {
			return event.getLocation();
		}

		if (event.getConferencingSolutionConfig() != null) {
			for (final ConferencingSolutionConfig.ConferenceEntryChannelConfig conferenceEntryChannelConfig : event
					.getConferencingSolutionConfig().getConferenceEntryChannelConfig()) {
				if (ConferenceEntryChannel.VIDEO.equals(conferenceEntryChannelConfig.getEntryPointType())) {
					return conferenceEntryChannelConfig.getJoiningLink();
				}
			}
		}

		if (event.getLocation() != null && event.getLocation().contains(ZOOM_MEETING_URL_SUFFIX)) {
			return event.getLocation();
		}

		return "";
	}

	public Boolean isEventProcessed(final BRCalendarEvent event) {
		Optional<ATSProcessedEventsDAO> atsProcessedEventsDAOOptional = this.atsProcessedEventsRepository
				.findByCalendarEntityIdAndCalendarEventStartTimeAndCalendarEventEndTime(event.getProviderEventId(),
						event.getStart().toEpochSecond(), event.getEnd().toEpochSecond());

		if (atsProcessedEventsDAOOptional.isPresent()) {
			log.info(String.format(
					"Skipping event with id: %s, startTime %s, endTime %s as it is a processed event",
					event.getProviderEventId(), event.getStart(), event.getEnd()));
		}

		return atsProcessedEventsDAOOptional.isPresent();
	}

	public Boolean isKeywordPresent(final BRCalendarEvent event, final String partnerId) {
		for (MeetingInterceptionConfiguration meetingInterceptionConfiguration : partnerInformationServiceFeignClient
				.getPartnerMeetingConfiguration()) {
			for (String keyword : meetingInterceptionConfiguration.getKeyword()) {
				if ((event.getDescription() + " " + event.getSummary()).toLowerCase().contains(keyword.toLowerCase())) {
					if (meetingInterceptionConfiguration.getPartnerId().equals(partnerId)) {
						return Boolean.TRUE;
					}
				}
			}
		}
		return Boolean.FALSE;
	}

	public String getPartnerId(final BRCalendarEvent event) {

		Map<String, List<String>> partnerToAllowedEmailDomainsMapping = this.atsPartnerConfigurationManager
				.getAllowedParticipantEmailDomains();

		for (Map.Entry<String, List<String>> partnerToAllowedEmailDomains : partnerToAllowedEmailDomainsMapping
				.entrySet()) {

			final String partnerId = partnerToAllowedEmailDomains.getKey();

			final List<String> allowedParticipantEmailDomains = partnerToAllowedEmailDomains.getValue();

			// Step 1: Check if organizer email domain in the list of allowed domains for
			// the partner
			if (this.isUserEmailDomainAllowedList(event.getOrganizer().getEmailId(), allowedParticipantEmailDomains)) {
				return partnerId;
			}
		}

		for (Map.Entry<String, List<String>> partnerToAllowedEmailDomains : partnerToAllowedEmailDomainsMapping
				.entrySet()) {

			if (event.getAttendees() == null) {
				return null;
			}

			final String partnerId = partnerToAllowedEmailDomains.getKey();

			final List<String> allowedParticipantEmailDomains = partnerToAllowedEmailDomains.getValue();

			// Step 2: Check if any of the participant email domains in list of allowed
			// domains for the partner

			// NOTE: Attendees also has the organizer, but in this case since the priority
			// is on the organizer there is no need to actually
			// skip the organizer
			for (EventAttendee eventAttendee : event.getAttendees()) {
				if (this.isUserEmailDomainAllowedList(eventAttendee.getEmailId(),
						allowedParticipantEmailDomains)) {
					return partnerId;
				}
			}
		}

		return null;
	}

	private Boolean isUserEmailDomainAllowedList(final String userEmailId,
			final List<String> allowedEmailDomainsList) {
		return allowedEmailDomainsList.contains(userEmailId.split("@")[1]);
	}

	public Boolean isPartnerSaasTrial(final String partnerId) {
		return this.partnerInformationServiceFeignClient
				.getPartnerById(partnerId)
				.getPartnershipModelId()
				.equals(SAAS_TRIAL_PARTNERSHIP_MODEL_ID);

	}

	public CalendarProvider getCalendarProvider(final String eventType) {
		if (eventType.equals(BROUTLOOKCALENDAREVENT_STRING)) {
			return CalendarProvider.OUTLOOK;
		}
		if (eventType.equals(BRGOOGLECALENDAREVENT_STRING)) {
			return CalendarProvider.GOOGLE;
		}
		return null;
	}

	public List<String> getPocEmails(final BRCalendarEvent event) {
		// Fetching all emails among attendees that are calendar connected
		List<String> pocEmails = new ArrayList<>();

		if (this.calendarClient.isActiveCalendar(event.getOrganizer().getEmailId())) {
			pocEmails.add(event.getOrganizer().getEmailId());
		}

		if (!(event.getAttendees() == null)) {
			event.getAttendees().stream()
					.filter(attendee -> !attendee.getEmailId().equals(event.getOrganizer().getEmailId()))
					.filter(attendee -> this.calendarClient.isActiveCalendar(attendee.getEmailId()))
					.map(EventAttendee::getEmailId)
					.forEach(pocEmails::add);
		}

		return pocEmails;
	}
}
