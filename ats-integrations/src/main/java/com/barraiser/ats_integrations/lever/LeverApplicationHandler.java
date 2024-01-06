/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.lever;

import com.barraiser.ats_integrations.lever.DTO.LeverApplicationDTO;
import com.barraiser.ats_integrations.lever.responses.ApplicationResponse;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@AllArgsConstructor
public class LeverApplicationHandler {
	private final LeverClient leverClient;
	private final LeverAccessManager leverAccessManager;

	public LeverApplicationDTO getApplication(
			final String opportunityId,
			final String applicationId,
			final String partnerId) throws Exception {

		return this.getApplicationFromLever(
				opportunityId,
				applicationId,
				partnerId)
				.getData();
	}

	private ApplicationResponse getApplicationFromLever(
			final String opportunityId,
			final String applicationId,
			final String partnerId) throws Exception {
		try {
			String authorization = this.leverAccessManager.getAuthorization(partnerId);

			return this.leverClient
					.getApplication(
							authorization,
							opportunityId,
							applicationId)
					.getBody();
		} catch (Exception exception) {
			log.warn(
					String.format(
							"Unable to fetch application from lever applicationId %s opportunityId %s partnerId %s",
							applicationId,
							opportunityId,
							partnerId));

			throw exception;
		}
	}
}
