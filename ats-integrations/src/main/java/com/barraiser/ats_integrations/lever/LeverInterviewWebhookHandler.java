/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.lever;

import com.barraiser.ats_integrations.dal.ATSInterviewToCandidateMappingDAO;
import com.barraiser.ats_integrations.dal.ATSInterviewToCandidateMappingRepository;
import com.barraiser.ats_integrations.lever.requests.LeverWebhookInterviewCreatedRequestBody;
import com.barraiser.common.ats_integrations.ATSProvider;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@Log4j2
@AllArgsConstructor
public class LeverInterviewWebhookHandler {

	private final ATSInterviewToCandidateMappingRepository atsInterviewToCandidateMappingRepository;

	public void handleInterviewCreation(
			final LeverWebhookInterviewCreatedRequestBody requestBody,
			final String partnerId)
			throws Exception {
		log.info("Adding lever interview");

		final Optional<ATSInterviewToCandidateMappingDAO> atsInterviewToCandidateMappingDAOOptional = this.atsInterviewToCandidateMappingRepository
				.findByAtsInterviewId(requestBody.getData().getInterviewId());

		if (atsInterviewToCandidateMappingDAOOptional.isEmpty()) {
			this.atsInterviewToCandidateMappingRepository.save(
					ATSInterviewToCandidateMappingDAO
							.builder()
							.id(UUID.randomUUID().toString())
							.atsProvider(ATSProvider.LEVER.getValue())
							.atsInterviewId(requestBody.getData().getInterviewId())
							.atsCandidateId(requestBody.getData().getOpportunityId())
							.partnerId(partnerId)
							.build());
		}
	}
}
