/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation;

import com.barraiser.common.graphql.input.GetEvaluationInput;
import com.barraiser.common.graphql.types.Evaluation;
import com.barraiser.common.graphql.types.Interview;
import com.barraiser.common.monitoring.Profiled;
import com.barraiser.onboarding.auth.AuthorizationResourceDTO;
import com.barraiser.onboarding.auth.Authorizer;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.EvaluationRepository;
import com.barraiser.onboarding.dal.JiraUUIDRepository;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.MultiParentTypeDataFetcher;
import com.barraiser.onboarding.interview.EvaluationMapper;
import com.barraiser.onboarding.interview.evaluation.auth.EvaluationAuthorizer;
import com.barraiser.onboarding.partner.EvaluationManager;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
@AllArgsConstructor
public class EvaluationDataFetcher implements MultiParentTypeDataFetcher {
	public static final String SCORING_ALGO_VERSION = "scoringAlgo";
	public static final String INTERVIEW = "Interview";
	public static final String GET_EVALUATIONS = "getEvaluations";
	public static final String EVALUATION = "evaluation";

	private final GraphQLUtil graphQLUtil;
	private final EvaluationRepository evaluationRepository;
	private final JiraUUIDRepository jiraUUIDRepository;
	private final Authorizer authorizer;
	private final EvaluationMapper evaluationMapper;
	private final EvaluationManager evaluationManager;

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(List.of(QUERY_TYPE, GET_EVALUATIONS), List.of(INTERVIEW, EVALUATION));
	}

	@Profiled(name = "evaluations")
	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final GraphQLObjectType type = (GraphQLObjectType) environment.getParentType();
		if (type.getName().equals(INTERVIEW)) {
			return this.handleInterviewParentType(environment);
		} else if (type.getName().equals(QUERY_TYPE)) {
			return this.handleGetEvaluations(environment);
		}
		throw new UnsupportedOperationException("Unknown parent for Evaluation: " + type.getName());
	}

	private DataFetcherResult<Object> handleGetEvaluations(
			final DataFetchingEnvironment environment) {
		final GetEvaluationInput input = this.graphQLUtil.getInput(environment, GetEvaluationInput.class);

		final List<Evaluation> evaluations = new ArrayList<>();
		if (input.getId() != null) {
			evaluations.add(
					this.evaluationManager.getEvaluationById(input.getId(), input.getAgv()));
		} else if (input.getJira() != null) {
			// TODO: remove when we dont use Jira get details button
			evaluations.add(this.getEvaluationFromInputForJira(input.getJira(), input.getAgv()));
		} else {
			throw new IllegalArgumentException("At least one input key has to be non-null");
		}

		this.authorizeUser(environment, evaluations);
		return DataFetcherResult.newResult()
				.data(evaluations)
				.localContext(Map.of(SCORING_ALGO_VERSION, evaluations.get(0).getDefaultScoringAlgoVersion()))
				.build();
	}

	private Evaluation getEvaluationFromInputForJira(final String jira, final String agv) {
		final String id = this.jiraUUIDRepository
				.findById(jira)
				.orElseThrow(
						() -> new IllegalArgumentException(
								"Invalid JIRA provided for evaluation: "
										+ jira))
				.getUuid();
		return this.evaluationManager.getEvaluationById(id, agv);
	}

	private DataFetcherResult<Object> handleInterviewParentType(
			final DataFetchingEnvironment environment) {
		final Interview interview = environment.getSource();
		final String evaluationId = interview.getEvaluationId();

		final EvaluationDAO evaluationDAO = this.evaluationRepository
				.findByIdAndDeletedOnIsNull(evaluationId)
				.orElseThrow(
						() -> new IllegalArgumentException(
								"Evaluation does not exist for interview id: "
										+ interview.getId()));
		final Evaluation evaluation = this.evaluationMapper.toEvaluation(evaluationDAO);
		return DataFetcherResult.newResult().data(evaluation).build();
	}

	private void authorizeUser(
			final DataFetchingEnvironment environment,

			final List<Evaluation> evaluations) {
		final AuthenticatedUser user = this.graphQLUtil.getLoggedInUser(environment);
		evaluations.forEach(
				x -> {
					final AuthorizationResourceDTO authorizationResource = AuthorizationResourceDTO.builder()
							.type(EvaluationAuthorizer.RESOURCE_TYPE)
							.resource(x)
							.build();
					this.authorizer.can(
							user, EvaluationAuthorizer.ACTION_READ, authorizationResource);
				});
	}

}
