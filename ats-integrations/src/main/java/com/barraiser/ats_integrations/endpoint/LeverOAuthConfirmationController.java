/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.endpoint;

import com.barraiser.ats_integrations.lever.LeverAccessManager;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@AllArgsConstructor
public class LeverOAuthConfirmationController {
	private final LeverAccessManager leverAccessManager;

	@GetMapping(path = "/lever-confirmation", params = { "code", "state" })
	public void leverConfirmation(
			@RequestParam(value = "code") final String oneTimeAuthorizationCode,
			@RequestParam(value = "state") final String partnerId) throws Exception {

		this.leverAccessManager.requestRefreshToken(
				oneTimeAuthorizationCode,
				partnerId);

		log.info(String.format(
				"Lever OAuth successful for partnerId : %s",
				partnerId));
	}

	@GetMapping(path = "/lever-confirmation", params = { "state", "error", "error_description" })
	public void leverConfirmationError(
			@RequestParam(value = "state") final String partnerId,
			@RequestParam(value = "error") final String errorType,
			@RequestParam(value = "error_description") final String errorDescription) {
		log.warn(String.format(
				"Error while authenticating with lever for partner : %s",
				partnerId));
	}
}
