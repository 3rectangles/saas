/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user;

import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.jobrole.JobRoleManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class CompanyManager {
	private final PartnerCompanyRepository partnerCompanyRepository;
	private final CompanyRepository companyRepository;
	private final EvaluationRepository evaluationRepository;
	private final JobRoleManager jobRoleManager;

	public Optional<CompanyDAO> getCompany(final String id) {
		return this.companyRepository.findById(id);
	}

	public CompanyDAO getTargetCompanyOfInterview(final InterviewDAO interview) {
		final Optional<EvaluationDAO> evaluationDAO = this.evaluationRepository.findById(interview.getEvaluationId());
		final Optional<JobRoleDAO> jobRoleDAO = this.jobRoleManager.getJobRoleFromEvaluation(evaluationDAO.get());
		return this.companyRepository.findById(jobRoleDAO.get().getCompanyId()).get();
	}

	public CompanyDAO getOrCreateCompany(final String companyName) {
		CompanyDAO companyDAO = this.companyRepository.findByNameIgnoreCase(companyName).orElse(null);
		if (companyDAO == null) {
			companyDAO = this.companyRepository.save(CompanyDAO.builder()
					.id(UUID.randomUUID().toString())
					.name(companyName).build());
		}
		return companyDAO;
	}

	public CompanyDAO getCompanyForPartner(final String partnerId) {
		return this.companyRepository.findById(this.partnerCompanyRepository.findById(partnerId).get().getCompanyId())
				.get();
	}

}
