/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.endpoint;

import com.barraiser.ats_integrations.smartRecruiters.SmartRecruitersWebhookHandler;
import com.barraiser.ats_integrations.smartRecruiters.requests.SmartRecruitersWebhookRequestBody;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Log4j2
@AllArgsConstructor
public class SmartRecruitersWebhookController {
	private final SmartRecruitersWebhookHandler smartRecruitersWebhookHandler;

	@PostMapping(value = "/smart-recruiters-webhook", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity smartRecruitersWebhook(
			@RequestParam("partner_id") final String partnerId,
			@RequestHeader("X-Token") final String token,
			@RequestHeader("event-name") final String eventName,
			@RequestHeader("event-version") final String eventVersion,
			@RequestHeader("event-id") final String eventId,
			@RequestHeader("Link") final String link,
			@RequestHeader("smartrecruiters-signature") final String signature,
			@RequestHeader("smartrecruiters-timestamp") final Long timeStamp,
			@RequestBody final SmartRecruitersWebhookRequestBody requestBody)
			throws Exception {
		this.smartRecruitersWebhookHandler
				.handleWebhook(
						requestBody,
						partnerId);

		return ResponseEntity
				.status(202)
				.build();
	}

	@PostMapping(value = "/smart-recruiters-webhook")
	ResponseEntity verifySmartRecruitersWebhook(
			@RequestParam("partner_id") final String partnerId,
			@RequestHeader("X-Hook-Secret") final String xHookSecret) {
		log.info(String.format(
				"Activating Smart Recruiter webhook for partnerId:%s",
				partnerId));

		final HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("X-Hook-Secret", xHookSecret);

		return ResponseEntity
				.ok()
				.headers(responseHeaders)
				.build();
	}
}
