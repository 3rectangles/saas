/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.common;

import com.barraiser.ats_integrations.calendar_interception.dto.AtsInterview;
import com.barraiser.ats_integrations.dal.PartnerATSIntegrationDAO;
import com.barraiser.ats_integrations.dal.PartnerATSIntegrationRepository;
import com.barraiser.ats_integrations.lever.DTO.DataDTO;
import com.barraiser.ats_integrations.lever.DTO.LeverApplicationDTO;
import com.barraiser.ats_integrations.lever.DTO.LeverInterviewDTO;
import com.barraiser.ats_integrations.lever.DTO.OpportunityExpandedDTO;
import com.barraiser.ats_integrations.merge.DTO.ApplicationDTO;
import com.barraiser.ats_integrations.merge.DTO.InterviewDTO;
import com.barraiser.ats_integrations.merge.DTO.RemoteDataDTO;
import com.barraiser.ats_integrations.merge.MergeATSClient;
import com.barraiser.ats_integrations.merge.MergeAccessManager;
import com.barraiser.common.ats_integrations.ATSProvider;
import com.barraiser.commons.dto.ats.enums.ATSAggregator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LeverInterviewDetailsFetcher implements ATSInterviewDetailsFetcher {
	private final PartnerATSIntegrationRepository partnerATSIntegrationRepository;
	private final MergeAccessManager mergeAccessManager;
	private final MergeATSClient mergeATSClient;
	private final ObjectMapper objectMapper;

	private static final String EXPAND_APPLICATION_PATH = "?expand=applications&expand=stage";
	private static final String OPPORTUNITY_APPLICATION_PATH = "/opportunities/{opportunity_id}/applications/{application_id}";

	@Override
	public ATSProvider atsProvider() {
		return ATSProvider.LEVER;
	}

	@Override
	public ATSAggregator atsAggregator() {
		return null;
	}

	@Override
	@SneakyThrows
	public AtsInterview getInterviewDetails(final String interviewStructureId, final String evaluationId,
			final String partnerId, final String atsProvider) {
		return null;

	}

	@Override
	@SneakyThrows
	public AtsInterview getInterviewDetails(final String interviewId, final String partnerId,
			final String atsProvider) {

		// Get Merge Account Token based on partner
		final PartnerATSIntegrationDAO partnerATSIntegration = this.partnerATSIntegrationRepository
				.findByPartnerIdAndAtsProvider(partnerId, atsProvider).get();

		final String authHeader = this.mergeAccessManager.getAuthorizationHeader();
		final String token = this.mergeAccessManager.getXAccountToken(partnerATSIntegration);

		// Call Merge API to fetch Interview based on ats interviewId in input
		final InterviewDTO interviewDTO = this.mergeATSClient.getRemoteInterviewWithRemoteData(
				authHeader, token, interviewId).getBody().getResults().get(0);

		// Extract Feedback Template from Interview Remote data response
		final LeverInterviewDTO leverInterviewDTO = objectMapper.convertValue(
				interviewDTO.getRemoteData().get(0).getData(), LeverInterviewDTO.class);

		// Call Merge API to fetch application including remote response based on
		// interview response
		final ApplicationDTO applicationDTO = this.mergeATSClient.getApplicationWithRemoteData(
				authHeader, token, interviewDTO.getApplication()).getBody();

		for (RemoteDataDTO remoteDataDTO : applicationDTO.getRemoteData()) {
			if (remoteDataDTO.getPath().contains(EXPAND_APPLICATION_PATH)) {
				// From Application DTO Extract OpportunityExpanded -> Use to extract Job Role
				// id
				final OpportunityExpandedDTO opportunityExpandedDTO = objectMapper.convertValue(remoteDataDTO.getData(),
						OpportunityExpandedDTO.class);

				// Return object containing
				// remote ids of interview,interview structure, evaluation, job role
				return AtsInterview.builder()
						.id(interviewDTO.getRemoteId())
						.evaluationId(applicationDTO.getRemoteId())
						.jobRoleId(opportunityExpandedDTO.getApplications().get(0).getPosting())
						.interviewStructureId(leverInterviewDTO.getFeedbackTemplate())
						.remoteData(opportunityExpandedDTO.getId())
						.build();
			}
			if (remoteDataDTO.getPath().contains(OPPORTUNITY_APPLICATION_PATH)) {
				// From Application DTO Extract LeverApplication -> Use to extract Job Role
				// id
				final DataDTO dataDTO = objectMapper.convertValue(remoteDataDTO.getData(), DataDTO.class);

				final LeverApplicationDTO leverApplicationDTO = objectMapper.convertValue(dataDTO.getData(),
						LeverApplicationDTO.class);

				// Return object containing
				// remote ids of interview,interview structure, evaluation, job role
				return AtsInterview.builder()
						.id(interviewDTO.getRemoteId())
						.evaluationId(applicationDTO.getRemoteId())
						.jobRoleId(leverApplicationDTO.getPosting())
						.interviewStructureId(leverInterviewDTO.getFeedbackTemplate())
						.remoteData(leverApplicationDTO.getOpportunityId())
						.build();

			}
		}

		throw new RuntimeException("Remote Data not found for Lever interview");
	}

}
