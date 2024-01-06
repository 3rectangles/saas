/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.calendar.controllers;

import com.barraiser.common.model.CalendarEvent;
import com.barraiser.common.model.CreateCalendarEventRequest;
import com.barraiser.communication.calendar.service.CalendarService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import static com.barraiser.common.constants.ServiceConfigurationConstants.COMMUNICATION_SERVICE_CONTEXT_PATH;

@Log4j2
@RestController
@AllArgsConstructor
public class CalendarController {
	private final CalendarService calendarService;

	@PostMapping(value = COMMUNICATION_SERVICE_CONTEXT_PATH + "/calendar/event")
	public CalendarEvent createEvent(
			@RequestBody final CreateCalendarEventRequest createEventRequest) throws Exception {
		return this.calendarService.createEvent(createEventRequest);
	}

	@DeleteMapping(value = COMMUNICATION_SERVICE_CONTEXT_PATH + "/calendar/{calendarId}/event/{eventId}")
	public void deleteEvent(
			@PathVariable final String calendarId, @PathVariable final String eventId)
			throws Exception {
		this.calendarService.deleteEvent(calendarId, eventId);
	}

	@DeleteMapping(value = COMMUNICATION_SERVICE_CONTEXT_PATH + "/entity/{entityId}/events")
	void cancelCalendarInvitesForEntityWithRescheduleCount(@PathVariable final String entityId,
			@RequestParam final Integer entityRescheduleCount) {
		this.calendarService.deleteEventsForEntity(entityId, entityRescheduleCount);
	}

	@DeleteMapping(value = COMMUNICATION_SERVICE_CONTEXT_PATH + "/entity/{entityId}/events/user/{userId}")
	void cancelCalendarInviteForUserIdAndEntityWithRescheduleCount(@PathVariable final String entityId,
			@PathVariable final String userId,
			@RequestParam final Integer entityRescheduleCount) {
		this.calendarService.deleteEventsForEntityAndUserId(entityId, userId, entityRescheduleCount);
	}

	@PostMapping(value = COMMUNICATION_SERVICE_CONTEXT_PATH + "/entity/{oldEntityId}/event/user/{userId}")
	public CalendarEvent updateCalendarInvite(
			@PathVariable(name = "oldEntityId") final String oldEntityId,
			@PathVariable(name = "userId") final String userId,
			@RequestBody final CreateCalendarEventRequest createCalendarEventRequest) throws Exception {
		return this.calendarService.updateCalendarInvite(
				oldEntityId,
				userId, createCalendarEventRequest);
	}
}
