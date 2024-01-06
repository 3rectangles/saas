/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.smartRecruiters;

import com.barraiser.ats_integrations.dal.PartnerATSIntegrationDAO;
import com.barraiser.ats_integrations.smartRecruiters.requests.MessageRequest;
import com.barraiser.ats_integrations.smartRecruiters.responses.MessageResponse;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
@AllArgsConstructor
public class SmartRecruitersMessageSender {
	private final SmartRecruitersAccessManager smartRecruitersAccessManager;
	private final SmartRecruitersClient smartRecruitersClient;

	public void shareMessage(final PartnerATSIntegrationDAO partnerATSIntegrationDAO, final String content,
			final String jobId) {
		log.info(String.format(
				"Sending CandidateAddition message to SR partnerId:%s",
				partnerATSIntegrationDAO.getPartnerId()));

		final String apiKey = this.smartRecruitersAccessManager
				.getApiKey(partnerATSIntegrationDAO);

		final MessageRequest messageRequest = MessageRequest
				.builder()
				.content(content)
				.correlationId("")
				.shareWith(MessageRequest.ShareWith.builder().users(List.of()).hiringTeamOf(List.of(jobId))
						.everyone(false).openNote(true).build())
				.build();

		final MessageResponse response = this.smartRecruitersClient
				.shareMessage(apiKey, messageRequest)
				.getBody();

		if (response != null) {
			log.info(String.format(
					"SR Message shared successfully for partnerId:%s",
					partnerATSIntegrationDAO.getPartnerId()));
		}
	}
}
