/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.drop_candidature;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.errorhandling.exception.IllegalOperationException;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.graphql.AuthorizationResult;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLMutation_deprecated;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.interview.InterViewRepository;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
public class DropCandidatureMutation extends AuthorizedGraphQLMutation_deprecated<Boolean> {

	private final GraphQLUtil graphQLUtil;

	private final InterViewRepository interViewRepository;
	private final EvaluationRepository evaluationRepository;
	private final DropCandidatureInterviewManager dropCandidatureInterviewManager;
	private final DropCandidatureEvaluationManager dropCandidatureEvaluationManager;

	public static final String EVALUATION_DOES_NOT_EXIST_ERROR = "No such evaluation exists in our system. Please contact Barraiser Team.";
	public static final String DROP_CANDIDATURE_ACTION_INVALID_ERROR = "Candidature cannot be dropped as it is completed.";
	public static final Integer DROP_CANDIDATURE_ACTION_INVALID_ERROR_CODE = 1001;

	public DropCandidatureMutation(DropCandidatureAuthorizer dropCandidatureAuthorizer,
			GraphQLUtil graphQLUtil,
			InterViewRepository interViewRepository,
			EvaluationRepository evaluationRepository,
			DropCandidatureEvaluationManager dropCandidatureEvaluationManager,
			DropCandidatureInterviewManager dropCandidatureInterviewManager) {
		super(dropCandidatureAuthorizer);
		this.graphQLUtil = graphQLUtil;
		this.interViewRepository = interViewRepository;
		this.evaluationRepository = evaluationRepository;
		this.dropCandidatureEvaluationManager = dropCandidatureEvaluationManager;
		this.dropCandidatureInterviewManager = dropCandidatureInterviewManager;
	}

	@Override
	protected Boolean fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult)
			throws Exception {
		final AuthenticatedUser user = this.graphQLUtil.getLoggedInUser(environment);
		final String evaluationId = environment.getArgument("evaluationId");
		final String cancellationReasonId = environment.getArgument("cancellationReasonId");

		if (this.isDropCandidatureAllowed(evaluationId)) {
			final List<InterviewDAO> interviews = this.interViewRepository.findAllByEvaluationId(evaluationId);
			this.interViewRepository.findAllByEvaluationId(evaluationId);
			this.dropCandidatureInterviewManager.updateInterviews(user, interviews);
			this.dropCandidatureEvaluationManager.updateEvaluation(user, evaluationId,
					cancellationReasonId);
		} else {
			throw new IllegalOperationException(DROP_CANDIDATURE_ACTION_INVALID_ERROR,
					DROP_CANDIDATURE_ACTION_INVALID_ERROR, DROP_CANDIDATURE_ACTION_INVALID_ERROR_CODE);
		}

		return Boolean.TRUE;
	}

	private Boolean isDropCandidatureAllowed(final String evaluationId) {
		final EvaluationDAO evaluationDAO = this.evaluationRepository.findById(evaluationId)
				.orElseThrow(() -> new IllegalArgumentException(EVALUATION_DOES_NOT_EXIST_ERROR));

		return EvaluationStatus.fromString(evaluationDAO.getStatus()).isInProcess();
	}

	@Override
	public String name() {
		return "dropCandidature";
	}
}
