/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation;

import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.jobrole.JobRoleManager;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class DemoEvaluationChecker {
	public static final String NUMBER_OF_EVALUATIONS_FOR_DEMO = "number_of_evaluations_for_demo";
	public static final String NUMBER_OF_EVALUATIONS_PER_JOB_ROLE_FOR_DEMO = "number_of_evaluations_per_job_role_for_demo";
	private final DynamicAppConfigProperties appConfigProperties;
	private final EvaluationRepository evaluationRepository;
	private final CompanyRepository companyRepository;
	private final JobRoleManager jobRoleManager;

	public boolean isDemoEvaluation(final String evaluationId, final String jobRoleId, final Integer jobRoleVersion) {
		final Optional<EvaluationDAO> evaluationDAO = this.evaluationRepository.findById(evaluationId);
		if (evaluationDAO.isEmpty()) {
			return this.isNewEvaluationDemoEligible(jobRoleId, jobRoleVersion);
		} else {
			return this.isExistingEvaluationDemo(evaluationDAO.get());
		}

	}

	private boolean isNewEvaluationDemoEligible(final String jobRoleId, final Integer jobRoleVersion) {
		final CompanyDAO companyDAO = this.companyRepository
				.findById(this.jobRoleManager.getJobRole(jobRoleId, jobRoleVersion).get().getCompanyId()).get();
		final List<JobRoleDAO> jobRoles = this.jobRoleManager.getJobRolesByCompanyId(companyDAO.getId());
		final int numberOfDemosAllowedPerCompany = this.appConfigProperties.getInt(NUMBER_OF_EVALUATIONS_FOR_DEMO);
		final int numberOfDemosAllowedPerJobRole = this.appConfigProperties
				.getInt(NUMBER_OF_EVALUATIONS_PER_JOB_ROLE_FOR_DEMO);
		final List<EvaluationDAO> evaluationDAOsPerCompany = this.evaluationRepository
				.findAllByJobRoleIdIn(
						jobRoles.stream().map(jr -> jr.getEntityId().getId()).collect(Collectors.toList()),
						PageRequest.of(0, numberOfDemosAllowedPerCompany));
		final List<EvaluationDAO> evaluationDAOSPerJobRole = this.evaluationRepository
				.findAllByJobRoleIdIn(
						List.of(jobRoleId),
						PageRequest.of(0, numberOfDemosAllowedPerJobRole));

		if (evaluationDAOsPerCompany.size() < numberOfDemosAllowedPerCompany ||
				evaluationDAOSPerJobRole.size() < numberOfDemosAllowedPerJobRole) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isExistingEvaluationDemo(final EvaluationDAO evaluationDAO) {
		return Boolean.TRUE.equals(evaluationDAO.getIsDemo());
	}

}
