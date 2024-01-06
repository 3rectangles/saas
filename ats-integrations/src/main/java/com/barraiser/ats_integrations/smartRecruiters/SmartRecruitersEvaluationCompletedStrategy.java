/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.smartRecruiters;

import com.barraiser.ats_integrations.common.ATSEvaluationCompletedStrategy;
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
public class SmartRecruitersEvaluationCompletedStrategy implements ATSEvaluationCompletedStrategy {
	private static final String EVALUATION_COMPLETED_TAG = "BarRaiser Evaluation Completed";
	private static final String CONTENT = "#[CANDIDATE:%s] BarRaiser Evaluation Completed. View BarRaiser Report - https://barraiser.com/candidate-evaluation/%s";

	private final SmartRecruitersTagsUpdater smartRecruitersTagsUpdater;
	private final SmartRecruitersMessageSender smartRecruitersMessageSender;
	private final ATSJobPostingToBRJobRoleRepository atsJobPostingToBRJobRoleRepository;

	@Override
	public String atsProvider() {
		return ATSProvider.SMART_RECRUITERS.getValue();
	}

	@Override
	public void performNecessaryOperationsUponEvaluationCompletion(
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
						List.of(EVALUATION_COMPLETED_TAG));

		final String messageContent = this.getMessageContent(atsToBREvaluationDAO);

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

	private String getMessageContent(final ATSToBREvaluationDAO atsToBREvaluationDAO) {
		return String.format(
				CONTENT,
				atsToBREvaluationDAO.getAtsEvaluationId(),
				atsToBREvaluationDAO.getBrEvaluationId());
	}
}
