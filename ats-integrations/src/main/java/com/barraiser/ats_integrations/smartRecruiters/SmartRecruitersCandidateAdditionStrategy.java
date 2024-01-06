/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.smartRecruiters;

import com.barraiser.ats_integrations.common.ATSCandidateAdditionStrategy;
import com.barraiser.ats_integrations.dal.ATSJobPostingToBRJobRoleDAO;
import com.barraiser.ats_integrations.dal.ATSJobPostingToBRJobRoleRepository;
import com.barraiser.ats_integrations.dal.ATSToBREvaluationDAO;
import com.barraiser.ats_integrations.dal.PartnerATSIntegrationDAO;
import com.barraiser.common.ats_integrations.ATSProvider;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
@AllArgsConstructor
public class SmartRecruitersCandidateAdditionStrategy implements ATSCandidateAdditionStrategy {
	private static final String EVALUATION_STARTED_TAG = "BarRaiser Evaluation Started";
	private static final String CONTENT = "#[CANDIDATE:%s] BarRaiser Evaluation Started. View BarRaiser Evaluation Process - https://barraiser.com/partner/%s/evaluations?eid=%s";
	private final SmartRecruitersTagsUpdater smartRecruitersTagsUpdater;
	private final SmartRecruitersMessageSender smartRecruitersMessageSender;
	private final ATSJobPostingToBRJobRoleRepository atsJobPostingToBRJobRoleRepository;

	@Override
	public String atsProvider() {
		return ATSProvider.SMART_RECRUITERS.getValue();
	}

	@Override
	public void performNecessaryOperationsUponCandidateAddition(
			PartnerATSIntegrationDAO partnerATSIntegrationDAO,
			ATSToBREvaluationDAO atsToBREvaluationDAO)
			throws Exception {
		log.info(String.format(
				"Sending tags to SR candidate:%s partnerId:%s",
				atsToBREvaluationDAO.getAtsEvaluationId(),
				partnerATSIntegrationDAO.getPartnerId()));
		this.smartRecruitersTagsUpdater
				.addTags(
						partnerATSIntegrationDAO,
						atsToBREvaluationDAO.getAtsEvaluationId(),
						List.of(EVALUATION_STARTED_TAG));

		final String messageContent = this.getMessageContent(
				partnerATSIntegrationDAO,
				atsToBREvaluationDAO);

		final ATSJobPostingToBRJobRoleDAO atsJobPostingToBRJobRoleDAO = this.atsJobPostingToBRJobRoleRepository
				.findById(atsToBREvaluationDAO.getAtsJobPostingToBRJobRoleId())
				.get();

		log.info(String.format(
				"Sending CandidateAddition message to SR candidate:%s partnerId:%s",
				atsToBREvaluationDAO.getAtsEvaluationId(),
				partnerATSIntegrationDAO.getPartnerId()));

		this.smartRecruitersMessageSender
				.shareMessage(
						partnerATSIntegrationDAO,
						messageContent,
						atsJobPostingToBRJobRoleDAO.getAtsJobPostingId());
	}

	public String getMessageContent(
			final PartnerATSIntegrationDAO partnerATSIntegrationDAO,
			final ATSToBREvaluationDAO atsToBREvaluationDAO) {
		return String.format(
				CONTENT,
				atsToBREvaluationDAO.getAtsEvaluationId(),
				partnerATSIntegrationDAO.getPartnerId(),
				atsToBREvaluationDAO.getBrEvaluationId());
	}
}
