/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.partner;

import com.barraiser.common.graphql.types.Evaluation;
import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.EvaluationMapper;
import com.barraiser.onboarding.interview.jobrole.JobRoleManager;
import com.barraiser.onboarding.user.PartnerEmployeeWhiteLister;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
@Log4j2
public class EvaluationManager {

	private final EvaluationRepository evaluationRepository;
	private final JobRoleManager jobRoleManager;
	private final PartnerCompanyRepository partnerCompanyRepository;
	private final DynamicAppConfigProperties appConfigProperties;
	private final DomainRepository domainRepository;
	private final EvaluationMapper evaluationMapper;

	public String getPartnerCompanyForEvaluation(final String evaluationId) {
		if (this.evaluationRepository.findById(evaluationId).isEmpty()) {
			return null;
		}
		final EvaluationDAO evaluationDAO = this.evaluationRepository.findById(evaluationId).get();
		final Optional<PartnerCompanyDAO> partnerCompanyDAO = this.partnerCompanyRepository
				.findById(evaluationDAO.getPartnerId());
		if (partnerCompanyDAO.isEmpty()) {
			return null;
		}
		return partnerCompanyDAO.get().getId();
	}

	public Boolean isDemoCompany(final String partnerCompanyId) {
		return this.appConfigProperties.getString(PartnerEmployeeWhiteLister.DYNAMO_DEMO_COMPANIES)
				.equals(partnerCompanyId);
	}

	public DomainDAO getDomainOfEvaluation(final String evaluationId) {
		final EvaluationDAO evaluationDAO = this.evaluationRepository.findById(evaluationId).get();
		final String domainId = this.jobRoleManager.getJobRoleFromEvaluation(evaluationDAO).get().getDomainId();
		final DomainDAO domain = this.domainRepository.findById(domainId).get();

		return domain;
	}

	public Evaluation getEvaluationById(final String id, final String evaluationAlgoVersion) {
		final EvaluationDAO evaluationDAO = this.evaluationRepository
				.findById(id)
				.orElseThrow(
						() -> new IllegalArgumentException("Invalid evaluationId " + id));
		if (evaluationAlgoVersion != null) {
			return this.evaluationMapper.toEvaluation(evaluationDAO, evaluationAlgoVersion);
		}
		return this.evaluationMapper.toEvaluation(evaluationDAO);
	}
}
