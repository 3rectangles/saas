/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.smartRecruiters;

import com.barraiser.ats_integrations.dal.PartnerATSIntegrationDAO;
import com.barraiser.ats_integrations.smartRecruiters.DTO.CandidateDTO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@AllArgsConstructor
public class SmartRecruitersCandidateFetcher {
	private final SmartRecruitersAccessManager smartRecruitersAccessManager;
	private final SmartRecruitersClient smartRecruitersClient;

	public CandidateDTO getCandidate(
			final PartnerATSIntegrationDAO partnerATSIntegrationDAO,
			final String candidateId) {
		log.info(String.format(
				"Fetching candidate from SR partnerId:%s candidateId:%s",
				partnerATSIntegrationDAO.getPartnerId(),
				candidateId));

		final String apiKey = this.smartRecruitersAccessManager
				.getApiKey(partnerATSIntegrationDAO);

		return this.smartRecruitersClient
				.getCandidate(apiKey, candidateId)
				.getBody();
	}
}
