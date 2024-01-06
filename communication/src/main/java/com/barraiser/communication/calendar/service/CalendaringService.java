/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.calendar.service;

import com.barraiser.common.model.CreateCalendarEventRequest;

public interface CalendaringService {
	CalendarEvent createEvent(final CreateCalendarEventRequest calendarCreateEventRequest)
			throws Exception;

	void deleteEvent(String accountId, String eventId) throws Exception;

	CalendarEvent updateEvent(
			final String accountId,
			final String eventId,
			final CreateCalendarEventRequest createCalendarEventRequest) throws Exception;
}
