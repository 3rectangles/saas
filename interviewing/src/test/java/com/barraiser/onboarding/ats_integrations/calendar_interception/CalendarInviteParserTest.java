/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.ats_integrations.calendar_interception;

import com.barraiser.ats_integrations.calendar_interception.CalendarInterceptionHelper;
import com.barraiser.ats_integrations.config.ATSPartnerConfigurationManager;
import com.barraiser.commons.eventing.schema.commons.calendar.BRCalendarEvent;
import com.barraiser.commons.eventing.schema.commons.calendar.EventAttendee;
import com.barraiser.commons.eventing.schema.commons.calendar.Organizer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CalendarInviteParserTest {

	@Mock
	private ATSPartnerConfigurationManager atsPartnerConfigurationManager;

	@InjectMocks
	private CalendarInterceptionHelper calendarInterceptionHelper;

	/**
	 * User belongs to no partner company
	 */
	@Test(expected = IllegalArgumentException.class)
	public void shouldFindNoPartner() {
		// GIVEN
		final BRCalendarEvent brGoogleCalendarEvent = BRCalendarEvent.builder()
				.organizer(Organizer.builder()
						.emailId("a@partner5.com")
						.build())
				.attendees(List.of(EventAttendee.builder()
						.emailId("b@partner55.com")
						.build(),
						EventAttendee.builder()
								.emailId("c@abc.com")
								.build()))
				.build();

		// WHEN
		when(this.atsPartnerConfigurationManager.getAllowedParticipantEmailDomains()).thenReturn(
				Map.of("partner1", List.of("partner1.com", "partner11.com", "partner111.com"),
						"partner2", List.of("partner2.com", "partner22.com", "partner222.com"),
						"partner3", List.of("partner3.com", "partner33.com", "partner333.com")));

		// THEN
		final String partnerId = this.calendarInterceptionHelper.getPartnerId(brGoogleCalendarEvent);

	}

	@Test
	public void shouldFindPartnerAsOrganizersCompany() {
		// GIVEN
		final BRCalendarEvent brGoogleCalendarEvent = BRCalendarEvent.builder()
				.organizer(Organizer.builder()
						.emailId("a@partner1.com")
						.build())
				.attendees(List.of(EventAttendee.builder()
						.emailId("b@partner2.com")
						.build(),
						EventAttendee.builder()
								.emailId("c@partner2.com")
								.build()))
				.build();

		// WHEN
		when(this.atsPartnerConfigurationManager.getAllowedParticipantEmailDomains()).thenReturn(
				Map.of("partner1", List.of("partner1.com", "partner11.com", "partner111.com"),
						"partner2", List.of("partner2.com", "partner22.com", "partner222.com"),
						"partner3", List.of("partner3.com", "partner33.com", "partner333.com")));

		// THEN
		final String partnerId = this.calendarInterceptionHelper.getPartnerId(brGoogleCalendarEvent);

		// EXPECTED OUTPUT
		Assert.assertEquals("partner1", partnerId);
	}

	@Test
	public void shouldFindPartnerAsAttendeesCompany() {
		// GIVEN
		final BRCalendarEvent brGoogleCalendarEvent = BRCalendarEvent.builder()
				.organizer(Organizer.builder()
						.emailId("a@partner5.com")
						.build())
				.attendees(List.of(EventAttendee.builder()
						.emailId("b@partner2.com")
						.build(),
						EventAttendee.builder()
								.emailId("c@partner2.com")
								.build()))
				.build();

		// WHEN
		when(this.atsPartnerConfigurationManager.getAllowedParticipantEmailDomains()).thenReturn(
				Map.of("partner1", List.of("partner1.com", "partner11.com", "partner111.com"),
						"partner2", List.of("partner2.com", "partner22.com", "partner222.com"),
						"partner3", List.of("partner3.com", "partner33.com", "partner333.com")));

		// THEN
		final String partnerId = this.calendarInterceptionHelper.getPartnerId(brGoogleCalendarEvent);

		// EXPECTED OUTPUT
		Assert.assertEquals("partner2", partnerId);
	}
}
