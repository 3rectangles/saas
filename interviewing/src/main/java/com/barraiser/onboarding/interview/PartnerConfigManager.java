/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.enums.RoundType;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.jobrole.JobRoleManager;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.barraiser.common.constants.Constants.PARTNERSHIP_MODEL_ID_PURE_SAAS;
import static com.barraiser.common.constants.Constants.PARTNERSHIP_MODEL_ID_SAAS_TRIAL;

@Log4j2
@Component
@AllArgsConstructor
public class PartnerConfigManager {
	private final EvaluationRepository evaluationRepository;
	private final PartnerCompanyRepository partnerCompanyRepository;
	private final InterViewRepository interViewRepository;
	private final JobRoleManager jobRoleManager;

	public boolean shouldSendSchedulingLinkToCandidate(final InterviewDAO interviewDAO) {
		final EvaluationDAO evaluationDAO = this.evaluationRepository.findById(interviewDAO.getEvaluationId()).get();
		if (evaluationDAO.getJobRoleId() == null) {
			return Boolean.FALSE;
		}
		final JobRoleDAO jobRoleDAO = this.jobRoleManager.getJobRoleFromEvaluation(evaluationDAO).get();
		final PartnerCompanyDAO partnerCompanyDAO = this.partnerCompanyRepository
				.findByCompanyId(jobRoleDAO.getCompanyId()).get();
		return partnerCompanyDAO.getIsCandidateSchedulingEnabled();
	}

	public PartnerCompanyDAO getPartnerCompanyForInterviewId(final String interviewId) {
		final InterviewDAO interviewDAO = this.interViewRepository.findById(interviewId).get();
		final EvaluationDAO evaluationDAO = this.evaluationRepository.findById(interviewDAO.getEvaluationId()).get();
		final JobRoleDAO jobRoleDAO = this.jobRoleManager.getJobRoleFromEvaluation(evaluationDAO).get();
		final PartnerCompanyDAO partnerCompanyDAO = this.partnerCompanyRepository
				.findByCompanyId(jobRoleDAO.getCompanyId()).get();
		return partnerCompanyDAO;
	}

	public void updateIsCandidateSchedulingEnabledFlag(
			final String partnerId, final Boolean isCandidateSchedulingEnabled) {
		Optional<PartnerCompanyDAO> partnerCompanyDAO = this.partnerCompanyRepository.findById(partnerId);
		if (partnerCompanyDAO.isPresent()) {
			this.partnerCompanyRepository.save(
					partnerCompanyDAO.get().toBuilder()
							.isCandidateSchedulingEnabled(isCandidateSchedulingEnabled)
							.build());
		} else {
			log.error("Partner Id: {} does not exist in the system.", partnerId);
			throw new IllegalArgumentException("An error has occurred, please contact BarRaiser support.");
		}
	}

	public String getPartnerIdFromCompanyId(String companyId) {
		return this.partnerCompanyRepository.findByCompanyId(companyId).get().getId();
	}

	public PartnerCompanyDAO getPartnerCompanyForJobRole(final String jobRoleId) {
		final JobRoleDAO jobRoleDAO = this.jobRoleManager.getJobRole(jobRoleId, 0).get();
		return this.partnerCompanyRepository.findByCompanyId(jobRoleDAO.getCompanyId()).get();
	}

	public String getPartnerCompanyForEvaluation(final String evaluationId) {
		final String companyId = this.evaluationRepository.findById(evaluationId).get().getCompanyId();
		return this.getPartnerIdFromCompanyId(companyId);
	}

	public boolean is24HourSchedulingAllowed(final String partnerId) {
		return this.partnerCompanyRepository.findById(partnerId).map(PartnerCompanyDAO::getIs24HourSchedulingAllowed)
				.orElse(false);
	}

	public boolean is24HourSchedulingAllowed(final InterviewDAO interview) {
		Boolean flag = this.getPartnerCompanyForInterviewId(interview.getId()).getIsCandidateSchedulingEnabled();
		if (flag == null)
			flag = false;
		return flag;
	}

	public String getDefaultRoundTypeForPartner(final String partnerId) {
		final String partnershipModel = this.partnerCompanyRepository.findById(partnerId).get().getPartnershipModelId();
		if (PARTNERSHIP_MODEL_ID_PURE_SAAS.equals(partnershipModel) ||
				PARTNERSHIP_MODEL_ID_SAAS_TRIAL.equals(partnershipModel)) {
			return RoundType.INTERNAL.getValue();
		}

		return null;
	}
}
