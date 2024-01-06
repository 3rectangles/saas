/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.ats_integrations.calendar_interception;

import com.barraiser.ats_integrations.errorhandling.ATSAnomalyException;
import com.barraiser.commons.eventing.schema.commons.calendar.BRCalendarEvent;
import com.barraiser.commons.eventing.schema.commons.calendar.EventAttendee;
import com.barraiser.commons.eventing.schema.commons.calendar.Organizer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(MockitoJUnitRunner.class)
public class SchedulingSetupDataAccumulatorTest {

	private final static String ATS_NO_INTERVIEWER = "There are no interviewers in this interview.";

	@Test(expected = ATSAnomalyException.class)
	public void ShouldFindNoInterviewer() throws ATSAnomalyException {
		List<EventAttendee> attendees = new ArrayList<>();

		attendees.add(EventAttendee.builder()
				.emailId("abc@abc.com")
				.build());

		attendees.add(EventAttendee.builder()
				.emailId("cba@cba.com")
				.build());

		BRCalendarEvent event = BRCalendarEvent.builder()
				.attendees(attendees)
				.organizer(Organizer.builder()
						.emailId("abc@abc.com")
						.build())
				.build();

		this.getInterviewerEmailTest("", event);
	}

	@Test
	public void ShouldFindOrganizerAsInterviewer() throws ATSAnomalyException {
		List<EventAttendee> attendees = new ArrayList<>();

		attendees.add(EventAttendee.builder()
				.emailId("abc@barraiser.com")
				.build());

		attendees.add(EventAttendee.builder()
				.emailId("cba@cba.com")
				.build());

		attendees.add(EventAttendee.builder()
				.emailId("cba@cbaaa.com")
				.build());

		BRCalendarEvent event = BRCalendarEvent.builder()
				.attendees(attendees)
				.organizer(Organizer.builder()
						.emailId("abc@barraiser.com")
						.build())
				.build();

		Assert.assertEquals(this.getInterviewerEmailTest("", event), "abc@barraiser.com");
	}

	@Test
	public void ShoudlFindInterviewerFromAttendees() throws ATSAnomalyException {
		List<EventAttendee> attendees = new ArrayList<>();

		attendees.add(EventAttendee.builder()
				.emailId("abc@barraiser.com")
				.build());

		attendees.add(EventAttendee.builder()
				.emailId("cba@barraiser.com")
				.build());

		attendees.add(EventAttendee.builder()
				.emailId("cba@barraiser.com")
				.build());

		BRCalendarEvent event = BRCalendarEvent.builder()
				.attendees(attendees)
				.organizer(Organizer.builder()
						.emailId("abc@barraiser.com")
						.build())
				.build();

		Assert.assertEquals(this.getInterviewerEmailTest("", event), "cba@barraiser.com");
	}

	public String getInterviewerEmailTest(final String partnerId, final BRCalendarEvent event)
			throws ATSAnomalyException {

		final List<String> internalInterviewersEmailDomains = List.of("barraiser.com");

		final List<String> interviewerEmails = event.getAttendees().stream()
				.filter(attendee -> !isOrganizer(attendee, event.getOrganizer()))
				.map(EventAttendee::getEmailId)
				.filter(emailId -> this.ofDomain(emailId, internalInterviewersEmailDomains))
				.collect(Collectors.toList());

		if (interviewerEmails.size() > 0) {
			return interviewerEmails.get(0);
		}

		// Check if organizer is interviewer
		if (this.ofDomain(event.getOrganizer().getEmailId(), internalInterviewersEmailDomains)) {
			return event.getOrganizer().getEmailId();
		}

		throw new ATSAnomalyException(ATS_NO_INTERVIEWER, ATS_NO_INTERVIEWER,
				1001);

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
