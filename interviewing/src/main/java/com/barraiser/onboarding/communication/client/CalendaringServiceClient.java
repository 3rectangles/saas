/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.communication.client;

import com.barraiser.common.model.CalendarEvent;
import com.barraiser.common.model.CreateCalendarEventRequest;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import static com.barraiser.common.constants.ServiceConfigurationConstants.COMMUNICATION_SERVICE_CONTEXT_PATH;

@FeignClient(name = "calendaring-service-client", url = "http://localhost:5000")
public interface CalendaringServiceClient {

	@PostMapping(value = COMMUNICATION_SERVICE_CONTEXT_PATH + "/calendar/event")
	@Headers(value = "Content-Type: application/json")
	CalendarEvent sendCalendarInvite(@RequestBody CreateCalendarEventRequest createEventRequest);

	@DeleteMapping(value = COMMUNICATION_SERVICE_CONTEXT_PATH + "/calendar/{calendarId}/event/{eventId}")
	void cancelCalendarInvite(@PathVariable String calendarId, @PathVariable String eventId);

	@DeleteMapping(value = COMMUNICATION_SERVICE_CONTEXT_PATH + "/entity/{entityId}/events")
	void cancelCalendarInvitesForEntityWithRescheduleCount(@PathVariable final String entityId,
			@RequestParam final Integer entityRescheduleCount);

	@DeleteMapping(value = COMMUNICATION_SERVICE_CONTEXT_PATH + "/entity/{entityId}/events/user/{userId}")
	void cancelCalendarInviteForUserIdAndEntityWithRescheduleCount(@PathVariable final String entityId,
			@PathVariable final String userId,
			@RequestParam final Integer entityRescheduleCount);

	@PostMapping(value = COMMUNICATION_SERVICE_CONTEXT_PATH + "/entity/{oldEntityId}/event/user/{userId}")
	@Headers(value = "Content-Type: application/json")
	CalendarEvent updateCalendarInvite(
			@PathVariable(name = "oldEntityId") final String oldEntityId,
			@PathVariable(name = "userId") final String userId,
			@RequestBody final CreateCalendarEventRequest createCalendarEventRequest);
}
