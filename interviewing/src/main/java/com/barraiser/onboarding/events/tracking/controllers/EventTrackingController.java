/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.events.tracking.controllers;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.events.graphql.input.EventInput;
import com.barraiser.onboarding.events.tracking.services.MixpanelService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;

/**
 * TBD: Batch event tracking can be explored later.
 */
@RestController
@Log4j2
@AllArgsConstructor
public class EventTrackingController {

	private final MixpanelService mixpanelService;

	@PostMapping(value = "/mixpanel/trackEvent")
	public void trackEventsWithMixpanel(@RequestBody EventInput event,
			@RequestAttribute(name = "loggedInUser", required = false) AuthenticatedUser eventPublisher,
			@RequestAttribute("context") HashMap<String, Object> context) throws IOException {

		this.mixpanelService.trackEvent(event.getUserId(), event.getPartnerId(), eventPublisher, event.getEventType(),
				event.getEvent(), context);
	}
}
