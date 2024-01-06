/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.common;

import com.barraiser.ats_integrations.calendar_interception.dto.CandidateDetails;
import com.barraiser.ats_integrations.dal.PartnerATSIntegrationDAO;
import com.barraiser.ats_integrations.dal.PartnerATSIntegrationRepository;
import com.barraiser.ats_integrations.lever.DTO.*;
import com.barraiser.ats_integrations.lever.MergeLeverResumeDownloader;
import com.barraiser.ats_integrations.merge.DTO.ApplicationDTO;
import com.barraiser.ats_integrations.merge.DTO.PassthroughInputDTO;
import com.barraiser.ats_integrations.merge.DTO.PassthroughResponseDTO;
import com.barraiser.ats_integrations.merge.DTO.RemoteDataDTO;
import com.barraiser.ats_integrations.merge.MergeATSClient;
import com.barraiser.ats_integrations.merge.MergeAccessManager;
import com.barraiser.common.ats_integrations.ATSProvider;
import com.barraiser.commons.dto.ats.enums.ATSAggregator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class LeverCandidateDetailsFetcher implements ATSCandidateDetailsFetcher {

	private final MergeLeverResumeDownloader mergeLeverResumeDownloader;
	private final MergeATSClient mergeATSClient;
	private final MergeAccessManager mergeAccessManager;
	private final PartnerATSIntegrationRepository partnerATSIntegrationRepository;
	private final ObjectMapper objectMapper;

	private static final String EXPAND_APPLICATION_PATH = "?expand=applications&expand=stage";
	private static final String OPPORTUNITY_APPLICATION_PATH = "/opportunities/{opportunity_id}/applications/{application_id}";
	private static final String OPPORTUNITY_STRING_FORMAT_PATH = "opportunities/%s";
	private static final String OPPORTUNITY_RESUME_STRING_FORMAT_PATH = "/opportunities/%s/resumes";
	private static final String GET_METHOD = "GET";

	@Override
	public ATSProvider atsProvider() {
		return ATSProvider.LEVER;
	}

	@Override
	public ATSAggregator atsAggregator() {
		return null;
	}

	@SneakyThrows
	@Override
	public CandidateDetails getCandidateDetails(final String evaluationId, final String partnerId,
			final String atsProvider) {
		/*
		 * Fetch candidate details(name, email, resume, phone) from Lever through merge
		 * passthrough and remote data api
		 */
		// Step 1: Fetch Merge credentials
		final PartnerATSIntegrationDAO partnerATSIntegration = this.partnerATSIntegrationRepository
				.findByPartnerIdAndAtsProvider(partnerId, atsProvider).get();

		final String authHeader = this.mergeAccessManager.getAuthorizationHeader();
		final String token = this.mergeAccessManager.getXAccountToken(partnerATSIntegration);

		// Step 2: Fetch Application with remote data from Merge
		final ApplicationDTO applicationDTO = this.mergeATSClient.getRemoteApplicationWithRemoteData(
				authHeader, token, evaluationId).getBody().getResults().get(0);

		// Step 3: Convert remote data into DTO object based on path and extract
		// opportunityId
		final String opportunityId = fetchOpportunityIdFromRemoteData(applicationDTO.getRemoteData());

		// Step 4: Call passthrough API to fetch application through opportunityId and
		// ATSEvaluationId
		final PassthroughResponseDTO passthroughApplication = this.mergeATSClient.callPassthrough(
				authHeader, token, PassthroughInputDTO.builder()
						.path(String.format(OPPORTUNITY_STRING_FORMAT_PATH, opportunityId))
						.method(GET_METHOD).build())
				.getBody();

		final OpportunityDTO opportunityDTO = objectMapper.convertValue(
				passthroughApplication.getResponse().getData(),
				OpportunityDTO.class);

		// Step 5: Call passthrough API to fetch resume
		final PassthroughResponseDTO passthroughResponseDTO = this.mergeATSClient.callPassthrough(
				authHeader, token, PassthroughInputDTO.builder()
						.path(String.format(OPPORTUNITY_RESUME_STRING_FORMAT_PATH, opportunityId))
						.method(GET_METHOD)
						.build())
				.getBody();

		final List<ResumeDTO> resumeDTOList = objectMapper.convertValue(passthroughResponseDTO.getResponse().getData(),
				objectMapper.getTypeFactory().constructCollectionType(List.class, ResumeDTO.class));

		// Step 6: Upload Resume document and fetch link
		String resumeLink = null;
		if (!resumeDTOList.isEmpty()) {
			resumeLink = this.getLeverResumeLink(opportunityId, resumeDTOList.get(0), authHeader,
					token);
		}

		final String firstName = opportunityDTO.getName().split("\\s+", 2)[0];
		final String lastName = opportunityDTO.getName().split("\\s+", 2).length >= 2
				? opportunityDTO.getName().split("\\s+", 2)[1]
				: null;

		return CandidateDetails.builder()
				.emailId(opportunityDTO.getEmails().isEmpty() ? null : opportunityDTO.getEmails().get(0))
				.firstName(firstName)
				.lastName(lastName)
				.mobileNumber(
						opportunityDTO.getPhones().isEmpty() ? null : opportunityDTO.getPhones().get(0).getValue())
				.resumeLink(resumeLink)
				.build();
	}

	@SneakyThrows
	private String getLeverResumeLink(final String opportunityId, final ResumeDTO resumeDTO, final String authHeader,
			final String token) {
		return this.mergeLeverResumeDownloader.getResumeFile(opportunityId, resumeDTO, authHeader, token).getUrl();

	}

	private String fetchOpportunityIdFromRemoteData(List<RemoteDataDTO> remoteDataDTOS) {
		for (RemoteDataDTO remoteDataDTO : remoteDataDTOS) {
			if (remoteDataDTO.getPath().contains(EXPAND_APPLICATION_PATH)) {
				final OpportunityExpandedDTO opportunityExpandedDTO = objectMapper.convertValue(remoteDataDTO.getData(),
						OpportunityExpandedDTO.class);
				return opportunityExpandedDTO.getId();
			}

			if (remoteDataDTO.getPath().contains(OPPORTUNITY_APPLICATION_PATH)) {
				final DataDTO dataDTO = objectMapper.convertValue(remoteDataDTO.getData(), DataDTO.class);
				final LeverApplicationDTO leverApplicationDTO = objectMapper.convertValue(dataDTO.getData(),
						LeverApplicationDTO.class);
				return leverApplicationDTO.getOpportunityId();
			}
		}
		throw new RuntimeException("Failed to fetch Opportunity Id from Lever");
	}
}
