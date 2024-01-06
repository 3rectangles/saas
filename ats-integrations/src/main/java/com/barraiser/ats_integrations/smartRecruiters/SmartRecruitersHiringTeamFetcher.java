/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.smartRecruiters;

import com.barraiser.ats_integrations.dal.PartnerATSIntegrationDAO;
import com.barraiser.ats_integrations.smartRecruiters.DTO.HiringTeamDTO;
import com.barraiser.ats_integrations.smartRecruiters.DTO.HiringTeamMemberDTO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
@AllArgsConstructor
public class SmartRecruitersHiringTeamFetcher {
	private final SmartRecruitersAccessManager smartRecruitersAccessManager;
	private final SmartRecruitersClient smartRecruitersClient;

	public List<HiringTeamMemberDTO> getHiringTeam(
			final PartnerATSIntegrationDAO partnerATSIntegrationDAO,
			final String jobId) {
		final String apiKey = this.smartRecruitersAccessManager
				.getApiKey(partnerATSIntegrationDAO);

		final HiringTeamDTO hiringTeamDTO = this.smartRecruitersClient
				.getHiringTeam(
						apiKey,
						jobId)
				.getBody();

		return hiringTeamDTO.getContent();
	}
}
