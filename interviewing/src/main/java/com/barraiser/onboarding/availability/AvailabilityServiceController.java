/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.availability;

import com.barraiser.commons.dto.NotificationChannel;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Log4j2
@AllArgsConstructor
public class AvailabilityServiceController {
	private final AvailabilityServiceClient availabilityServiceClient;

	@GetMapping("/calendar/activeSubscriptions")
	public ResponseEntity<List<NotificationChannel>> getActiveSubscriptionsOfProvider(
			@RequestParam("providerName") final String providerName) {
		return availabilityServiceClient.getActiveSubscriptionsOfProvider(providerName);
	}
}
