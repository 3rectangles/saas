/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.smartRecruiters;

import com.barraiser.ats_integrations.dal.PartnerATSIntegrationDAO;
import com.barraiser.ats_integrations.smartRecruiters.DTO.AttachmentDTO;
import com.barraiser.ats_integrations.smartRecruiters.DTO.AttachmentsDTO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
@AllArgsConstructor
public class SmartRecruitersAttachmentsFetcher {
	private final SmartRecruitersAccessManager smartRecruitersAccessManager;
	private final SmartRecruitersClient smartRecruitersClient;

	public List<AttachmentDTO> getAttachments(
			final PartnerATSIntegrationDAO partnerATSIntegrationDAO,
			final String candidateId,
			final String jobId) {
		final String apiKey = this.smartRecruitersAccessManager
				.getApiKey(partnerATSIntegrationDAO);

		final AttachmentsDTO attachmentsDTO = this.smartRecruitersClient
				.getAttachments(
						apiKey,
						candidateId,
						jobId)
				.getBody();

		return attachmentsDTO.getContent();
	}

}
