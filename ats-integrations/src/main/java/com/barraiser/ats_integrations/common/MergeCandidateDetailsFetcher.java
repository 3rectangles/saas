/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.common;

import com.barraiser.ats_integrations.calendar_interception.dto.CandidateDetails;
import com.barraiser.ats_integrations.dal.PartnerATSIntegrationDAO;
import com.barraiser.ats_integrations.dal.PartnerATSIntegrationRepository;
import com.barraiser.ats_integrations.merge.DTO.ApplicationDTO;
import com.barraiser.ats_integrations.merge.DTO.AttachmentDTO;
import com.barraiser.ats_integrations.merge.DTO.CandidateDTO;
import com.barraiser.ats_integrations.merge.MergeATSClient;
import com.barraiser.ats_integrations.merge.MergeAccessManager;
import com.barraiser.common.ats_integrations.ATSProvider;
import com.barraiser.commons.dto.ats.enums.ATSAggregator;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class MergeCandidateDetailsFetcher implements ATSCandidateDetailsFetcher {
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
	public CandidateDetails getCandidateDetails(final String evaluationId, final String partnerId,
			final String atsProvider) {
		final PartnerATSIntegrationDAO partnerATSIntegration = this.partnerATSIntegrationRepository
				.findByPartnerIdAndAtsProvider(partnerId, atsProvider).get();

		final String authHeader = this.mergeAccessManager.getAuthorizationHeader();
		final String token = this.mergeAccessManager.getXAccountToken(partnerATSIntegration);

		final ApplicationDTO applicationDTO = this.mergeATSClient.getRemoteApplication(
				authHeader,
				token,
				evaluationId).getBody().getResults().get(0);

		final CandidateDTO candidateDTO = this.mergeATSClient.getCandidate(
				authHeader,
				token,
				applicationDTO.getCandidate()).getBody();

		String emailId = null;
		String firstName = null;
		String lastName = null;
		String mobileNumber = null;
		String resumeLink = null;

		if (candidateDTO.getEmailAddresses() != null && !candidateDTO.getEmailAddresses().isEmpty()) {
			emailId = candidateDTO.getEmailAddresses().get(0).getValue();
		}
		if (candidateDTO.getFirstName() != null) {
			firstName = candidateDTO.getFirstName();
		}
		if (candidateDTO.getLastName() != null) {
			lastName = candidateDTO.getLastName();
		}
		if (candidateDTO.getPhoneNumbers() != null && !candidateDTO.getPhoneNumbers().isEmpty()) {
			mobileNumber = candidateDTO.getPhoneNumbers().get(0).getValue();
		}

		for (String attachmentId : candidateDTO.getAttachments()) {
			final AttachmentDTO attachmentDTO = this.mergeATSClient.getAttachment(
					authHeader,
					token,
					attachmentId).getBody();

			if (attachmentDTO.getAttachmentType().equals("RESUME")) {
				resumeLink = attachmentDTO.getFileUrl();
				break;
			}
		}

		// TODO: Multiple mailIds, phoneNumbers, links are possible
		return CandidateDetails.builder()
				.emailId(emailId)
				.firstName(firstName)
				.lastName(lastName)
				.mobileNumber(mobileNumber)
				.resumeLink(resumeLink)
				.build();
	}
}
