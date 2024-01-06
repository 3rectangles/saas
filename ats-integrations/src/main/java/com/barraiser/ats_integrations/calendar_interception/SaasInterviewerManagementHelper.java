/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.calendar_interception;

import com.barraiser.ats_integrations.config.ATSPartnerConfigurationManager;
import com.barraiser.ats_integrations.errorhandling.ATSAnomalyException;
import com.barraiser.commons.eventing.schema.commons.calendar.BRCalendarEvent;
import com.barraiser.commons.eventing.schema.commons.calendar.EventAttendee;
import com.barraiser.commons.eventing.schema.commons.calendar.Organizer;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
@Log4j2
public class SaasInterviewerManagementHelper {
	private final ATSPartnerConfigurationManager atsPartnerConfigurationManager;
	private final static String NO_INTERVIEWER_MESSAGE = "There are no interviewers in this interview.";

	public String getInterviewerEmail(final String partnerId, final BRCalendarEvent event)
			throws ATSAnomalyException {

		final List<String> internalInterviewersEmailDomains = this.atsPartnerConfigurationManager
				.getInternalInterviewersEmailDomains(partnerId);

		// Adding this check because when an event is created on Google Calendar UI,
		// and there are no guests attached, attendees becomes null.
		// This does not happen when event is created through ATS, at that time
		// Organizer will be among the attendees even if there are no other guests.
		if (event.getAttendees() != null) {
			// Skip Organizer
			final List<String> interviewerEmails = event.getAttendees().stream()
					.filter(attendee -> !this.isOrganizer(attendee, event.getOrganizer()))
					.map(EventAttendee::getEmailId)
					.filter(emailId -> this.ofDomain(emailId, internalInterviewersEmailDomains))
					.collect(Collectors.toList());

			if (interviewerEmails.size() > 0) {
				return interviewerEmails.get(0);
			}
		}

		// Check if organizer is interviewer
		if (this.ofDomain(event.getOrganizer().getEmailId(), internalInterviewersEmailDomains)) {
			return event.getOrganizer().getEmailId();
		}

		// Should never come
		throw new ATSAnomalyException(NO_INTERVIEWER_MESSAGE, NO_INTERVIEWER_MESSAGE,
				1001);

	}

	public List<String> getInterviewAttendeeEmails(final BRCalendarEvent event, final String interviewerEmailId,
			final String partnerId) {

		if (event.getAttendees() == null) {
			return null;
		}

		final List<String> internalInterviewersEmailDomains = this.atsPartnerConfigurationManager
				.getInternalInterviewersEmailDomains(partnerId);

		final List<String> attendeeEmailsList = new ArrayList<>();
		for (EventAttendee attendee : event.getAttendees()) {
			if (!attendee.getEmailId().equals(interviewerEmailId)) {
				String emailId = attendee.getEmailId();
				if (this.ofDomain(emailId, internalInterviewersEmailDomains)) {
					attendeeEmailsList.add(emailId);
				}
			}
		}

		// Adding organizer to attendee list by checking if not the same as interviewer
		// Organizer is not part of event.getAttendees() in several cases
		// Also check if organizer has the same domain as configured for interviewers
		if (!event.getOrganizer().getEmailId().equals(interviewerEmailId) &&
				!attendeeEmailsList.contains(event.getOrganizer().getEmailId()) &&
				this.ofDomain(event.getOrganizer().getEmailId(), internalInterviewersEmailDomains)) {
			attendeeEmailsList.add(event.getOrganizer().getEmailId());
		}

		return attendeeEmailsList;

	}

	private Boolean isOrganizer(final EventAttendee attendee, final Organizer organizer) {
		return attendee.getEmailId().equals(organizer.getEmailId());
	}

	private Boolean ofDomain(final String email, final List<String> domains) {
		for (String domain : domains) {
			if (email.endsWith(domain)) {
				return Boolean.TRUE;
			}
		}

		return Boolean.FALSE;
	}

}
