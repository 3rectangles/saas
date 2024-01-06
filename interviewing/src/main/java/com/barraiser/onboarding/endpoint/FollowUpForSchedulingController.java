/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.endpoint;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Log4j2
@RestController
@AllArgsConstructor
public class FollowUpForSchedulingController {

	final IvrResponseRepository ivrResponseRepository;

	@PostMapping(value = "/candidate-follow-up-for-scheduling")
	public void saveFollowUpStatus(
			@RequestBody final FollowUpForSchedulingWebhookRequestBody requestBody)
			throws Exception {
		log.info(
				"The variables in the follow-up for scheduling ivr request are : {},{},{}",
				requestBody.callAnswered,
				requestBody.messageBirdFlowId, requestBody.phone);
		this.ivrResponseRepository.save(IvrResponseDAO.builder()
				.id(UUID.randomUUID().toString())
				.callAnswered(requestBody.callAnswered)
				.messageBirdFlowId(requestBody.messageBirdFlowId)
				.phone(requestBody.phone)
				.ivrResponse(null)
				.build());
	}
}
