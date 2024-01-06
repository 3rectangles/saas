/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.smartRecruiters;

import com.barraiser.ats_integrations.dal.PartnerATSIntegrationDAO;
import com.barraiser.ats_integrations.smartRecruiters.DTO.TagsDTO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
@AllArgsConstructor
public class SmartRecruitersTagsUpdater {
	private final SmartRecruitersAccessManager smartRecruitersAccessManager;
	private final SmartRecruitersClient smartRecruitersClient;

	public void addTags(
			final PartnerATSIntegrationDAO partnerATSIntegrationDAO,
			final String candidateId,
			final List<String> tags) {
		log.info(String.format(
				"Sending tags to SR candidate:%s partnerId:%s",
				candidateId,
				partnerATSIntegrationDAO.getPartnerId()));

		final String apiKey = this.smartRecruitersAccessManager
				.getApiKey(partnerATSIntegrationDAO);

		final TagsDTO requestBody = TagsDTO
				.builder()
				.tags(tags)
				.build();

		this.smartRecruitersClient
				.addTags(apiKey, candidateId, requestBody);
	}
}
