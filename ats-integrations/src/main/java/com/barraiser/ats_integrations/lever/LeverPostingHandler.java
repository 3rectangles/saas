/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.lever;

import com.barraiser.ats_integrations.lever.DTO.PostingDTO;
import com.barraiser.ats_integrations.lever.responses.PostingsResponse;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
@AllArgsConstructor
public class LeverPostingHandler {
	private final LeverAccessManager leverAccessManager;
	private final LeverClient leverClient;

	public List<PostingDTO> getLeverPostings(final String partnerId)
			throws Exception {
		log.info(String.format(
				"Fetching lever Postings from Lever for partnerId %s",
				partnerId));
		return this.getPostingsFromLever(partnerId);
	}

	private List<PostingDTO> getPostingsFromLever(final String partnerId) throws Exception {
		try {
			final List<PostingDTO> postingDTOs = new ArrayList<>();
			final String authorization = this.leverAccessManager
					.getAuthorization(partnerId);
			String nextOffset = null;
			do {
				final PostingsResponse response = this.leverClient
						.getAllPostings(authorization, nextOffset).getBody();
				nextOffset = response.getNext();
				postingDTOs.addAll(response.getData());
			} while (nextOffset != null);
			return postingDTOs;
		} catch (Exception exception) {
			log.warn(
					String.format(
							"Unable to fetch postings from lever for partnerId : %s",
							partnerId),
					exception);

			throw exception;
		}
	}
}
