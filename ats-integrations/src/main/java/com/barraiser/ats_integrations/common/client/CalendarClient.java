/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.common.client;

import com.barraiser.commons.dto.calendarManagement.AddBRAppToEventRequest;
import com.barraiser.commons.dto.calendarManagement.UpdateCalendarEventRequest;
import com.barraiser.commons.service_discovery.BarRaiserBackendServicesNames;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = BarRaiserBackendServicesNames.AVAILABILITY_SERVICE, contextId = "Calendaring")
public interface CalendarClient {

	@PutMapping(value = "/{userEmail}/event/{eventId}")
	@Headers(value = "Content-Type: application/json")
	ResponseEntity<Void> updateEvent(@PathVariable("userEmail") final String userEmail,
			@PathVariable("eventId") final String eventId,
			@RequestBody UpdateCalendarEventRequest updateCalendarEventRequest);

	@PutMapping(value = "/addBRApplication")
	ResponseEntity<Void> addApplication(@RequestBody AddBRAppToEventRequest addBRAppToEventRequest);

	@GetMapping("/calendar/isActive/{emailId}")
	Boolean isActiveCalendar(@PathVariable("emailId") final String emailId);
}
