/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.reject_candidate;

import com.barraiser.common.graphql.RejectCandidateInput;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.EvaluationRepository;
import com.barraiser.onboarding.dal.EvaluationStatus;
import com.barraiser.onboarding.errorhandling.exception.IllegalOperationException;
import com.barraiser.onboarding.graphql.AuthorizationResult;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLMutation_deprecated;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.interview.evaluation.EvaluationPartnerStatus;
import com.barraiser.onboarding.interview.status.EvaluationStatusManager;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@Log4j2
public class RejectCandidateMutation extends AuthorizedGraphQLMutation_deprecated<Boolean> {
	public static final String EVALUATION_DOES_NOT_EXIST_ERROR = "No such evaluation exists in our system. Please contact BarRaiser Team.";
	public static final String REJECT_CANDIDATE_ACTION_INVALID_ERROR = "Candidate cannot be rejected as candidature is not yet completed";
	public static final Integer REJECT_CANDIDATE_ACTION_INVALID_ERROR_CODE = 1001;

	private final GraphQLUtil graphQLUtil;
	private final EvaluationStatusManager evaluationStatusManager;
	private final EvaluationRepository evaluationRepository;

	public RejectCandidateMutation(final RejectCandidateAuthorizer rejectCandidateAuthorizer,
			final GraphQLUtil graphQLUtil,
			final EvaluationStatusManager evaluationStatusManager, final EvaluationRepository evaluationRepository) {
		super(rejectCandidateAuthorizer);
		this.graphQLUtil = graphQLUtil;
		this.evaluationStatusManager = evaluationStatusManager;
		this.evaluationRepository = evaluationRepository;
	}

	@Transactional
	@Override
	protected Boolean fetch(final DataFetchingEnvironment environment, final AuthorizationResult authorizationResult)
			throws Exception {
		final RejectCandidateInput input = this.graphQLUtil.getInput(environment, RejectCandidateInput.class);
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);

		final EvaluationDAO evaluationDAO = this.evaluationRepository.findById(input.getEvaluationId())
				.orElseThrow(() -> new IllegalArgumentException(EVALUATION_DOES_NOT_EXIST_ERROR));
		if (this.isRejectingCandidateAllowed(evaluationDAO)) {
			this.rejectCandidate(evaluationDAO, input.getRejectionReasonId(), authenticatedUser.getUserName());
		} else {
			throw new IllegalOperationException(REJECT_CANDIDATE_ACTION_INVALID_ERROR,
					REJECT_CANDIDATE_ACTION_INVALID_ERROR, REJECT_CANDIDATE_ACTION_INVALID_ERROR_CODE);
		}

		return true;
	}

	private Boolean isRejectingCandidateAllowed(final EvaluationDAO evaluationDAO) {
		if (EvaluationStatus.fromString(evaluationDAO.getStatus()).isInProcess()) {
			return false;
		}
		return evaluationDAO.getCandidateRejectionReason() == null;
	}

	private void rejectCandidate(final EvaluationDAO evaluationDAO, final String rejectionReason,
			final String rejectedBy) {
		this.evaluationStatusManager.transitionPartnerStatus(evaluationDAO,
				EvaluationPartnerStatus.REJECTED,
				rejectedBy);
		this.evaluationRepository
				.save(evaluationDAO.toBuilder().candidateRejectionReason(rejectionReason).build());
	}

	@Override
	public String name() {
		return "rejectCandidate";
	}
}
