/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.lever;

import com.barraiser.ats_integrations.lever.responses.UserResponse;
import com.barraiser.ats_integrations.lever.DTO.UserDTO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@AllArgsConstructor
public class LeverUserHandler {
	private final LeverAccessManager leverAccessManager;
	private final LeverClient leverClient;

	public UserDTO getUser(
			final String userId,
			final String partnerId)
			throws Exception {
		log.info(String.format(
				"Fetching user from lever for userId %s for partnerId %s",
				userId,
				partnerId));

		return this.getUserFromLever(
				userId,
				partnerId)
				.getData();
	}

	private UserResponse getUserFromLever(
			final String userId,
			final String partnerId)
			throws Exception {
		try {
			final String authorization = this.leverAccessManager
					.getAuthorization(partnerId);

			return this.leverClient
					.getUser(
							authorization,
							userId)
					.getBody();
		} catch (Exception exception) {
			log.warn(
					String.format(
							"Unable to get user from lever for userId %s for partnerId %s",
							userId,
							partnerId),
					exception);

			throw exception;
		}
	}
}
