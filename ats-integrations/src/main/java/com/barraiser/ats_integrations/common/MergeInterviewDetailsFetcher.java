/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.common;

import com.barraiser.ats_integrations.calendar_interception.dto.AtsInterview;
import com.barraiser.ats_integrations.dal.PartnerATSIntegrationDAO;
import com.barraiser.ats_integrations.dal.PartnerATSIntegrationRepository;
import com.barraiser.ats_integrations.merge.DTO.ApplicationDTO;
import com.barraiser.ats_integrations.merge.DTO.InterviewDTO;
import com.barraiser.ats_integrations.merge.MergeATSClient;
import com.barraiser.ats_integrations.merge.MergeAccessManager;
import com.barraiser.common.ats_integrations.ATSProvider;
import com.barraiser.commons.dto.ats.enums.ATSAggregator;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MergeInterviewDetailsFetcher implements ATSInterviewDetailsFetcher {
	private final MergeATSClient mergeATSClient;
	private final MergeAccessManager mergeAccessManager;
	private final PartnerATSIntegrationRepository partnerATSIntegrationRepository;

	@Override
	public ATSProvider atsProvider() {
		return null;
	}

	@Override
	public ATSAggregator atsAggregator() {
		return ATSAggregator.MERGE;
	}

	@Override
	@SneakyThrows
	public AtsInterview getInterviewDetails(final String interviewStructureId, final String evaluationId,
			final String partnerId, final String atsProvider) {
		final PartnerATSIntegrationDAO partnerATSIntegration = this.partnerATSIntegrationRepository
				.findByPartnerIdAndAtsProvider(partnerId, atsProvider).get();

		final String authHeader = this.mergeAccessManager.getAuthorizationHeader();
		final String token = this.mergeAccessManager.getXAccountToken(partnerATSIntegration);

		final ApplicationDTO application = this.mergeATSClient
				.getRemoteApplication(authHeader, token, evaluationId)
				.getBody().getResults().get(0);

		// InterviewStructureId needs to be replaced, not doing now as atsinterview is
		// not used anywhere
		final InterviewDTO interview = this.mergeATSClient.getInterview(
				authHeader,
				token,
				application.getId(), interviewStructureId).getBody().getResults().get(0);

		return AtsInterview.builder()
				.id(interview.getId())
				.interviewStructureId(interviewStructureId)
				.evaluationId(evaluationId)
				.build();
	}

	@Override
	@SneakyThrows
	public AtsInterview getInterviewDetails(final String interviewId, final String partnerId,
			final String atsProvider) {
		final PartnerATSIntegrationDAO partnerATSIntegration = this.partnerATSIntegrationRepository
				.findByPartnerIdAndAtsProvider(partnerId, atsProvider).get();

		final String authHeader = this.mergeAccessManager.getAuthorizationHeader();
		final String token = this.mergeAccessManager.getXAccountToken(partnerATSIntegration);

		final InterviewDTO interview = this.mergeATSClient.getRemoteInterview(
				authHeader,
				token,
				interviewId).getBody().getResults().get(0);

		return AtsInterview.builder()
				.id(interview.getId())
				.interviewStructureId(interview.getJobInterviewStage())
				.evaluationId(this.getRemoteEvaluationId(authHeader, token, interview.getApplication()))
				.build();
	}

	private String getRemoteEvaluationId(final String authHeader, final String token, final String applicationId) {
		final ApplicationDTO application = this.mergeATSClient.getApplication(
				authHeader,
				token,
				applicationId).getBody();

		return application.getRemoteId();
	}
}
