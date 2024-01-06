/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.calendar.google;

import com.barraiser.common.model.CreateCalendarEventRequest;
import com.barraiser.communication.calendar.service.CalendarEvent;
import com.barraiser.communication.calendar.service.CalendaringService;
import com.barraiser.communication.common.utilities.RandomGmailAccountSelector;
import com.barraiser.communication.dal.CalendarEntityDAO;
import com.barraiser.communication.dal.CalendarEntityRepository;
import com.barraiser.communication.dal.CalendarStatus;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.barraiser.communication.calendar.Constants.EVENT_UPDATE_SUBSCRIPTION_GROUP_ALL;
import static com.barraiser.communication.calendar.Constants.SENDER_PRIMARY_CALENDAR_ID;

@Log4j2
@AllArgsConstructor
@Component
public class GoogleCalendaringService implements CalendaringService {
	private final RandomGmailAccountSelector randomGmailAccountSelector;
	private final GoogleCalendarClientFactory googleCalendarClientFactory;
	private final CalendarEntityRepository calendarEntityRepository;

	@Override
	public CalendarEvent createEvent(final CreateCalendarEventRequest createEventRequest)
			throws IOException, GeneralSecurityException {

		final CalendarEvent calendarEvent = this.createGoogleCalendarEvent(createEventRequest);
		return CalendarEvent.builder()
				.accountId(calendarEvent.getAccountId())
				.eventId(calendarEvent.getEventId())
				.build();
	}

	private CalendarEvent createGoogleCalendarEvent(
			final CreateCalendarEventRequest createEventRequest)
			throws GeneralSecurityException, IOException {

		final String quotaUser = this.randomGmailAccountSelector.getAccount();
		final Calendar calendar = this.googleCalendarClientFactory.getGoogleCalendarClient(quotaUser);

		final Event eventToBeCreated = this.convertToGoogleCalendarCreateEventRequest(createEventRequest);
		final Event createdEvent = this.createCalendarEvent(calendar, eventToBeCreated);

		log.info("Created Event Link: {}\n", createdEvent.getHtmlLink());
		return CalendarEvent.builder().accountId(quotaUser).eventId(createdEvent.getId()).build();
	}

	@Override
	public void deleteEvent(final String accountId, final String eventId)
			throws GeneralSecurityException, IOException {

		final Calendar calendar = this.googleCalendarClientFactory.getGoogleCalendarClient(accountId);

		calendar.events()
				.delete(accountId, eventId)
				.setSendUpdates(EVENT_UPDATE_SUBSCRIPTION_GROUP_ALL)
				.execute();
		final Optional<CalendarEntityDAO> dao = this.calendarEntityRepository.findByEventId(eventId);

		this.calendarEntityRepository.save(
				dao.get().toBuilder().status(CalendarStatus.CANCELLED).build());
	}

	@Override
	public CalendarEvent updateEvent(
			final String accountId,
			final String eventId,
			final CreateCalendarEventRequest createCalendarEventRequest) throws Exception {

		final Calendar calendar = this.googleCalendarClientFactory.getGoogleCalendarClient(accountId);

		final Event event = calendar
				.events()
				.get(
						SENDER_PRIMARY_CALENDAR_ID,
						eventId)
				.execute();

		final Event updatedEvent = this.updateGoogleCalendarEvent(
				calendar,
				event,
				createCalendarEventRequest);

		log.info(
				"Updated Google calendar Event Link: {}\n",
				updatedEvent.getHtmlLink());

		return CalendarEvent
				.builder()
				.accountId(accountId)
				.eventId(updatedEvent.getId())
				.build();
	}

	private Event updateGoogleCalendarEvent(
			final Calendar calendar,
			final Event event,
			final CreateCalendarEventRequest createCalendarEventRequest) throws IOException {

		if (createCalendarEventRequest.getAttendeeEmails() == null
				|| createCalendarEventRequest.getAttendeeEmails().size() == 0) {
			log.info("Not updating google calender event as no attendee email is present."
					+ createCalendarEventRequest.getEntityId());
			return null;
		}

		event.setSummary(createCalendarEventRequest.getSummary());
		event.setLocation(createCalendarEventRequest.getLocation());
		event.setDescription(createCalendarEventRequest.getDescription());

		final List<EventAttendee> eventAttendees = new ArrayList<>();
		if (createCalendarEventRequest.getAttendeeEmails() != null) {
			for (final String attendeeEmail : createCalendarEventRequest.getAttendeeEmails()) {
				eventAttendees.add(
						new EventAttendee().setEmail(attendeeEmail.trim()));
			}
		}

		event.setAttendees(eventAttendees);

		final Event updatedEvent = calendar
				.events()
				.update(
						SENDER_PRIMARY_CALENDAR_ID,
						event.getId(),
						event)
				.execute();

		return updatedEvent;
	}

	private Event convertToGoogleCalendarCreateEventRequest(
			final CreateCalendarEventRequest createEventRequest) {
		final Event event = new Event()
				.setSummary(createEventRequest.getSummary())
				.setLocation(createEventRequest.getLocation())
				.setDescription(createEventRequest.getDescription());

		final DateTime startDateTime = new DateTime(createEventRequest.getStartTimeEpoch() * 1000);
		final EventDateTime start = new EventDateTime().setDateTime(startDateTime);
		event.setStart(start);

		final DateTime endDateTime = new DateTime(createEventRequest.getEndTimeEpoch() * 1000);
		final EventDateTime end = new EventDateTime().setDateTime(endDateTime);

		event.setEnd(end);

		final List<EventAttendee> eventAttendees = new ArrayList<>();

		if (createEventRequest.getAttendeeEmails() != null) {
			for (final String attendeeEmail : createEventRequest.getAttendeeEmails()) {
				if (attendeeEmail != null) {
					eventAttendees.add(new EventAttendee().setEmail(attendeeEmail.trim()));
				}
			}
		}

		event.setAttendees(eventAttendees);

		return event;
	}

	private Event createCalendarEvent(final Calendar calendar, final Event eventToBeCreated)
			throws IOException {

		if (eventToBeCreated.getAttendees() == null || eventToBeCreated.getAttendees().size() == 0) {
			log.info("Not creating google calendar invite as no attendee email is present");
			return null;
		}

		final String quotaUser = this.randomGmailAccountSelector.getAccount();
		log.info("Using account : {} quota for calendar creation", quotaUser);

		return calendar.events()
				.insert(SENDER_PRIMARY_CALENDAR_ID, eventToBeCreated)
				.setSendUpdates(EVENT_UPDATE_SUBSCRIPTION_GROUP_ALL)
				.setQuotaUser(quotaUser)
				.execute();
	}
}
