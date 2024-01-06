/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jobrole;

import com.barraiser.common.graphql.input.GetJobRoleInput;
import com.barraiser.common.graphql.types.Evaluation;
import com.barraiser.common.graphql.types.Interview;
import com.barraiser.common.graphql.types.JobRole;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.MultiParentTypeDataFetcher;
import com.barraiser.onboarding.interview.InterViewRepository;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.antlr.v4.runtime.misc.Pair;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.barraiser.onboarding.interview.jobrole.JobRoleDataLoaderFactory.JOB_ROLE_DATA_LOADER;

@Log4j2
@Component
@AllArgsConstructor
public class JobRoleDataFetcher implements MultiParentTypeDataFetcher {
	private static final String INTERVIEW = "Interview";
	private static final String EVALUATION = "Evaluation";

	private final InterViewRepository interViewRepository;
	private final EvaluationRepository evaluationRepository;
	private final JobRoleManager jobRoleManager;
	private final PartnerCompanyRepository partnerCompanyRepository;
	private final GraphQLUtil graphQLUtil;
	private final JobRoleMapper jobRoleMapper;

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of(QUERY_TYPE, "getJobRoles"),
				List.of(INTERVIEW, "jobRole"),
				List.of(EVALUATION, "jobRole"));
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final GraphQLObjectType parentType = (GraphQLObjectType) environment.getParentType();

		final DataLoader<JobRoleCriteria, JobRole> jobRoleDataLoader = environment.getDataLoader(JOB_ROLE_DATA_LOADER);

		if (INTERVIEW.equals(parentType.getName())) {
			return this.handleInterviewType(environment, jobRoleDataLoader);

		} else if (EVALUATION.equals(parentType.getName())) {
			return this.handleEvaluationType(environment, jobRoleDataLoader);
		} else if (QUERY_TYPE.equals(parentType.getName())) {

			return this.handleQueryType(environment);
		} else {
			throw new UnsupportedOperationException(
					"Bad parent type while accessing job role type, please fix your query");
		}
	}

	private DataFetcherResult<Object> handleQueryType(final DataFetchingEnvironment environment) {
		final GetJobRoleInput input = this.graphQLUtil.getArgument(environment, "input", GetJobRoleInput.class);

		final List<JobRole> jobRoleList;
		if (input.getPartnerId() != null) {
			final PartnerCompanyDAO partnerCompanyDAO = this.partnerCompanyRepository
					.findById(input.getPartnerId())
					.orElseThrow(
							() -> new IllegalArgumentException(
									"Partner company does not exist"));

			List<JobRoleDAO> jobRoleDAOList;

			if (input.getOnlyAllowLatestVersion() != null && input.getOnlyAllowLatestVersion()) {
				jobRoleDAOList = this.jobRoleManager.getLatestJobRolesByCompanyId(partnerCompanyDAO.getCompanyId());
			} else {
				jobRoleDAOList = this.jobRoleManager.getJobRolesByCompanyId(partnerCompanyDAO.getCompanyId());
			}

			jobRoleList = new ArrayList<>(this.getJobRolesMap(jobRoleDAOList).values());
		} else {
			final String jobRoleId = input.getJobRoleId();
			final Integer jobRoleVersion = input.getJobRoleVersion() != null ? input.getJobRoleVersion()
					: this.jobRoleManager.getLatestVersionOfJobRole(jobRoleId).get().getEntityId().getVersion();
			final JobRoleDAO jobRoleDAO = this.jobRoleManager
					.getJobRole(jobRoleId, jobRoleVersion)
					.orElseThrow(() -> new RuntimeException("JobRole does not exist"));
			jobRoleList = List.of(this.jobRoleMapper.toJobRole(jobRoleDAO));
		}

		return DataFetcherResult.newResult().data(jobRoleList).build();
	}

	private CompletableFuture<JobRole> handleEvaluationType(
			final DataFetchingEnvironment environment,
			final DataLoader<JobRoleCriteria, JobRole> jobRoleDataLoader) {
		final Evaluation evaluation = environment.getSource();
		return jobRoleDataLoader.load(
				new JobRoleCriteria(evaluation.getJobRoleId(), evaluation.getJobRoleVersion()));
	}

	private CompletableFuture<JobRole> handleInterviewType(
			final DataFetchingEnvironment environment,
			final DataLoader<JobRoleCriteria, JobRole> jobRoleDataLoader) {
		final Interview interview = environment.getSource();

		final InterviewDAO interviewDAO = this.interViewRepository
				.findById(interview.getId())
				.orElseThrow(
						() -> new IllegalArgumentException(
								"Interview does not exist: " + interview.getId()));

		final EvaluationDAO evaluationDAO = this.evaluationRepository
				.findById(interviewDAO.getEvaluationId())
				.orElseThrow(
						() -> new RuntimeException(
								"Evaluation does not exist: "
										+ interviewDAO.getEvaluationId()));

		if (evaluationDAO.getJobRoleId() == null) {
			return null;
		}

		return jobRoleDataLoader.load(
				new JobRoleCriteria(
						evaluationDAO.getJobRoleId(), evaluationDAO.getJobRoleVersion()));
	}

	private Map<Pair<String, Integer>, JobRole> getJobRolesMap(final List<JobRoleDAO> jobRoleDAOs) {
		final Map<Pair<String, Integer>, JobRole> jobRoleMap = new HashMap<>();
		jobRoleDAOs.forEach(
				jobRoleDAO -> {
					final JobRole jobRole = this.jobRoleMapper.toJobRole(jobRoleDAO);
					jobRoleMap.put(new Pair<>(jobRole.getId(), jobRole.getVersion()), jobRole);
				});
		return jobRoleMap;
	}
}
