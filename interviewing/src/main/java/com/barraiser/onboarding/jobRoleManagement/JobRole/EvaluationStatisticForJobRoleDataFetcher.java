/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.jobRoleManagement.JobRole;

import com.barraiser.common.graphql.types.EvaluationStatistics;
import com.barraiser.common.graphql.types.JobRole;
import com.barraiser.onboarding.dal.JobRoleDAO;
import com.barraiser.onboarding.dal.JobRoleRepository;
import com.barraiser.onboarding.graphql.Constants;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.onboarding.interview.evaluation.search.dal.EvaluationSearchRepository;
import com.barraiser.onboarding.interview.jobrole.JobRoleManager;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
@Log4j2
public class EvaluationStatisticForJobRoleDataFetcher implements NamedDataFetcher {

	private final EvaluationSearchRepository evaluationSearchRepository;
	private final JobRoleManager jobRoleManager;

	@Override
	public String name() {
		return "evaluationStatistics";
	}

	@Override
	public String type() {
		return Constants.TYPE_JOB_ROLE;
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final JobRole jobRole = environment.getSource();
		String jobRoleId = jobRole.getId();

		EvaluationStatistics evaluationStatistics = this.getEvaluationStatistics(jobRoleId);

		return DataFetcherResult.newResult()
				.data(evaluationStatistics)
				.build();
	}

	private EvaluationStatistics getEvaluationStatistics(final String jobRoleId) {

		return EvaluationStatistics.builder()
				.total(this.evaluationSearchRepository.getCountOfTotalEvaluationsForJobRole(jobRoleId))
				.requiresAction(0l) // TODO : Remove. Keeping for backward compatability
				.active(this.jobRoleManager.getLatestVersionOfJobRole(jobRoleId).get()
						.getActiveCandidatesCountAggregate())
				.build();
	}

}
