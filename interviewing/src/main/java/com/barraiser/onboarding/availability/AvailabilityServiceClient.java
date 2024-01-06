/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.availability;

import com.barraiser.commons.dto.NotificationChannel;
import com.barraiser.commons.dto.calendarManagement.UpdateCalendarEventRequest;
import com.barraiser.commons.service_discovery.BarRaiserBackendServicesNames;
import com.barraiser.onboarding.availability.DTO.BookedSlotDTO;
import com.barraiser.onboarding.availability.DTO.GetBookedSlotsRequestDTO;
import com.barraiser.onboarding.availability.DTO.GetCalendarDTO;
import com.barraiser.onboarding.availability.DTO.RemoveAvailabilitySlotDTO;
import com.barraiser.onboarding.availability.DTO.AddCalendarDTO;
import com.barraiser.onboarding.availability.DTO.RemoveCalendarDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = BarRaiserBackendServicesNames.AVAILABILITY_SERVICE)
public interface AvailabilityServiceClient {

	@DeleteMapping(value = "availability/{userId}")
	void removeAvailability(
			@PathVariable("userId") final String userId,
			@RequestBody final RemoveAvailabilitySlotDTO removeAvailabilitySlotDTO);

	@PostMapping(value = "calendar/{userId}")
	void addCalendar(
			@PathVariable("userId") final String userId,
			@RequestBody final AddCalendarDTO addCalendarDTO);

	@GetMapping("/calendar/{userId}")
	List<GetCalendarDTO> getCalendars(@PathVariable("userId") final String userId);

	@DeleteMapping("/calendar/{userId}")
	void removeCalendar(
			@PathVariable("userId") final String userId,
			@RequestBody final RemoveCalendarDTO removeCalendarDTO);

	@GetMapping("/calendar/activeSubscriptions")
	public ResponseEntity<List<NotificationChannel>> getActiveSubscriptionsOfProvider(
			@RequestParam("providerName") final String providerName);

	@PostMapping("booked-slots")
	Map<String, List<BookedSlotDTO>> getBookedSlots(@RequestBody final GetBookedSlotsRequestDTO request);

	@DeleteMapping("booked-slot/{id}")
	void deleteBookedSlot(@PathVariable("id") final String id);

	@PutMapping("booked-slot/remove-buffer/{id}")
	void removeBookedSlotBuffer(@PathVariable("id") final String id);

}
