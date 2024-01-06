/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.lever;

import com.barraiser.ats_integrations.lever.requests.NotesRequestBody;
import com.barraiser.ats_integrations.lever.responses.NotesResponse;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@AllArgsConstructor
public class LeverNotesHandler {
	private final LeverAccessManager leverAccessManager;
	private final LeverClient leverClient;

	public void addNoteToLeverOpportunity(
			final String partnerId,
			final String opportunityId,
			final String note) throws Exception {
		final NotesRequestBody requestBody = NotesRequestBody
				.builder()
				.value(note)
				.notifyFollowers(true)
				.build();

		NotesResponse response = this.addNoteToOpportunityOnLever(
				partnerId,
				opportunityId,
				requestBody);

		if (response == null) {
			log.warn(String.format(
					"Unable to add note for lever opportunityId %s for partnerId %s",
					opportunityId,
					partnerId));
		}
	}

	private NotesResponse addNoteToOpportunityOnLever(
			final String partnerId,
			final String opportunityId,
			final NotesRequestBody requestBody) throws Exception {
		try {
			String authorization = this.leverAccessManager
					.getAuthorization(partnerId);

			return this.leverClient
					.addNotesToOpportunity(
							authorization,
							opportunityId,
							requestBody)
					.getBody();
		} catch (Exception exception) {
			log.warn(
					String.format(
							"Unable to add links to lever opportunity opportunityId %s for partnerId %s",
							opportunityId,
							partnerId),
					exception);

			throw exception;
		}
	}
}
