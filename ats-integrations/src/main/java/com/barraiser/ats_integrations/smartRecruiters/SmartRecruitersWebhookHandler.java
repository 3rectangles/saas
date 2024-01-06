/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.smartRecruiters;

import com.barraiser.ats_integrations.dal.PartnerATSIntegrationDAO;
import com.barraiser.ats_integrations.dal.PartnerATSIntegrationRepository;
import com.barraiser.ats_integrations.smartRecruiters.DTO.CandidateDTO;
import com.barraiser.ats_integrations.smartRecruiters.POJO.SmartRecruitersAddEvaluationCreationData;
import com.barraiser.ats_integrations.smartRecruiters.requests.SmartRecruitersWebhookRequestBody;
import com.barraiser.common.ats_integrations.ATSProvider;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Log4j2
@AllArgsConstructor
public class SmartRecruitersWebhookHandler {
	private static final String INTERVIEW = "INTERVIEW";

	private final SmartRecruitersCandidateFetcher smartRecruitersCandidateFetcher;
	private final PartnerATSIntegrationRepository partnerATSIntegrationRepository;
	private final SmartRecruitersAddEvaluationCreationDataPopulator smartRecruitersAddEvaluationCreationDataPopulator;
	private final SmartRecruitersAddEvaluationEventGenerator smartRecruitersAddEvaluationEventGenerator;

	public void handleWebhook(
			final SmartRecruitersWebhookRequestBody requestBody,
			final String partnerId)
			throws Exception {
		Optional<PartnerATSIntegrationDAO> partnerATSIntegrationDAO = this.partnerATSIntegrationRepository
				.findByPartnerIdAndAtsProvider(
						partnerId,
						ATSProvider.SMART_RECRUITERS.getValue());

		if (partnerATSIntegrationDAO.isEmpty()) {
			log.info(String.format(
					"Unable to find ATS credential for partnerId:%s atsProvider:%s",
					partnerId,
					ATSProvider.SMART_RECRUITERS.getValue()));

			return;
		}

		final CandidateDTO candidateDTO = this.smartRecruitersCandidateFetcher
				.getCandidate(
						partnerATSIntegrationDAO.get(),
						requestBody.getCandidateId());

		if (!INTERVIEW.equalsIgnoreCase(candidateDTO.getPrimaryAssignment().getStatus())) {
			log.info("The status is not INTERVIEW");
			return;
		}

		final SmartRecruitersAddEvaluationCreationData addEvaluationCreationData = this.smartRecruitersAddEvaluationCreationDataPopulator
				.getAddEvaluationCreationData(
						partnerATSIntegrationDAO.get(),
						requestBody,
						candidateDTO);

		if (addEvaluationCreationData == null) {
			log.info(String.format(
					"Unable to populate required data to add evaluation partnerId:%s candidateId:%s jobId:%s",
					partnerId,
					requestBody.getCandidateId(),
					requestBody.getJobId()));
			return;
		}

		this.smartRecruitersAddEvaluationEventGenerator
				.generateAddEvaluationEvent(
						partnerATSIntegrationDAO.get(),
						addEvaluationCreationData);
	}
}
