/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.lever;

import com.barraiser.ats_integrations.lever.requests.AddTagsRequestBody;
import com.barraiser.ats_integrations.lever.responses.OpportunityResponse;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
@AllArgsConstructor
public class LeverTagsHandler {
	private final LeverAccessManager leverAccessManager;
	private final LeverClient leverClient;

	public void addTagsToLeverOpportunity(
			final String partnerId,
			final String opportunityId,
			final List<String> tags) throws Exception {

		final AddTagsRequestBody requestBody = AddTagsRequestBody
				.builder()
				.tags(tags)
				.build();

		final OpportunityResponse response = this.addTagsToOpportunityOnLever(
				partnerId,
				opportunityId,
				requestBody);

		if (response == null) {
			log.warn(String.format(
					"Unable to add links for lever opportunityId %s for partnerId %s",
					opportunityId,
					partnerId));
		}
	}

	private OpportunityResponse addTagsToOpportunityOnLever(
			final String partnerId,
			final String opportunityId,
			final AddTagsRequestBody requestBody) throws Exception {
		try {
			String authorization = this.leverAccessManager
					.getAuthorization(partnerId);

			return this.leverClient
					.addTagsToOpportunity(
							authorization,
							opportunityId,
							requestBody)
					.getBody();
		} catch (Exception exception) {
			log.warn(
					String.format(
							"Unable to add tags to lever opportunity opportunityId %s for partnerId %s",
							opportunityId,
							partnerId),
					exception);

			throw exception;
		}
	}
}
