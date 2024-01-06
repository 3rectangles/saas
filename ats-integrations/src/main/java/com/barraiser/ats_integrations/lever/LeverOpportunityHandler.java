/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.lever;

import com.barraiser.ats_integrations.lever.DTO.OpportunityDTO;
import com.barraiser.ats_integrations.lever.responses.OpportunityResponse;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@AllArgsConstructor
public class LeverOpportunityHandler {
	private final LeverClient leverClient;
	private final LeverAccessManager leverAccessManager;

	public OpportunityDTO getOpportunity(
			final String opportunityId,
			final String partnerId) throws Exception {
		log.info(String.format(
				"Fetching opportunity with Id %s from lever from partnerId %s",
				opportunityId,
				partnerId));

		return this.getOpportunityFromLever(
				opportunityId,
				partnerId)
				.getData();
	}

	private OpportunityResponse getOpportunityFromLever(
			final String opportunityId,
			final String partnerId) throws Exception {
		try {
			String authorization = this.leverAccessManager.getAuthorization(partnerId);

			return this.leverClient
					.getOpportunity(
							authorization,
							opportunityId)
					.getBody();
		} catch (Exception exception) {
			log.warn(
					String.format(
							"Unable to fetch opportunity from lever opportunityId %s partnerId %s",
							opportunityId,
							partnerId));

			throw exception;
		}
	}
}
