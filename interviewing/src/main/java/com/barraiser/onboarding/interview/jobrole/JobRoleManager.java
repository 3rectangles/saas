/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jobrole;

import com.barraiser.common.dal.VersionedEntityId;
import com.barraiser.onboarding.dal.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class JobRoleManager {

	private final JobRoleRepository jobRoleRepository;
	private final EvaluationRepository evaluationRepository;

	public Optional<JobRoleDAO> getJobRole(final String id, final Integer version) {
		return this.jobRoleRepository.findByEntityId(new VersionedEntityId(id, version));
	}

	public Optional<JobRoleDAO> getJobRoleFromEvaluation(final EvaluationDAO evaluation) {
		return this.jobRoleRepository.findByEntityId(
				new VersionedEntityId(evaluation.getJobRoleId(), evaluation.getJobRoleVersion()));
	}

	public void saveJobRole(final JobRoleDAO jobRoleDAO) {
		this.jobRoleRepository.save(jobRoleDAO);
	}

	public List<JobRoleDAO> getJobRolesByCompanyId(final String companyId) {
		return this.jobRoleRepository.findByCompanyId(companyId);
	}

	public List<JobRoleDAO> getLatestJobRolesByCompanyId(final String companyId) {
		return this.jobRoleRepository.findLatestByCompanyIdAndDeprecatedOnIsNull(companyId);
	}

	public List<JobRoleDAO> getJobRoleFromDomainId(final String domainId) {
		return this.jobRoleRepository.findAllByDomainId(domainId);
	}

	public List<JobRoleDAO> getJobRoleFromCategoryId(final String categoryId) {
		return this.jobRoleRepository.findAllByCategory(categoryId);
	}

	public Optional<JobRoleDAO> getJobRoleFromEvaluation(final String evaluationId) {
		final EvaluationDAO evaluationDAO = this.evaluationRepository.findById(evaluationId).get();
		return this.getJobRoleFromEvaluation(evaluationDAO);
	}

	public Optional<JobRoleDAO> getLatestVersionOfJobRole(final String jobRoleId) {
		return this.jobRoleRepository.findTopByEntityIdIdOrderByEntityIdVersionDesc(jobRoleId);
	}

	public Long getJobRoleActiveCandidateCountAggregate(final String jobRoleId, final Integer jobRoleVersion) {
		Long activeCandidateCountAggregate = 0L;
		if (jobRoleVersion != 0) {
			JobRoleDAO previousVersionJobRole = this.getJobRole(jobRoleId, jobRoleVersion - 1).get();
			activeCandidateCountAggregate = previousVersionJobRole.getActiveCandidatesCountAggregate();
		}
		return activeCandidateCountAggregate;
	}
}
