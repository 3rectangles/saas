/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.input.UpdateInterviewingConfigurationInput;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.onboarding.auth.AllowAuthenticatedUserAuthorizationInputConstructor;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.EvaluationRepository;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.errorhandling.exception.IllegalOperationException;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLMutation;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class UpdateInterviewingConfigurationMutation extends AuthorizedGraphQLMutation<Boolean> {

	private final GraphQLUtil graphQLUtil;
	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;
	private final InterViewRepository interViewRepository;
	private final EvaluationRepository evaluationRepository;

	private static final String ERROR_MESSAGE_INTERVIEW_UPDATION_NOT_ALLOWED = "This operation is not allowed as Interview is already completed.";
	private static final Integer ERROR_CODE_INTERVIEW_UPDATION_NOT_ALLOWED = 1006;

	public UpdateInterviewingConfigurationMutation(AuthorizationServiceFeignClient authorizationServiceFeignClient,
			AllowAuthenticatedUserAuthorizationInputConstructor allowAuthenticatedUserAuthorizationInputConstructor,
			InterViewRepository interViewRepository,
			GraphQLUtil graphQLUtil, EvaluationRepository evaluationRepostiory) {
		super(authorizationServiceFeignClient, allowAuthenticatedUserAuthorizationInputConstructor);
		this.graphQLUtil = graphQLUtil;
		this.authorizationServiceFeignClient = authorizationServiceFeignClient;
		this.interViewRepository = interViewRepository;
		this.evaluationRepository = evaluationRepostiory;
	}

	@Override
	protected Boolean fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult)
			throws IllegalOperationException {
		/* TODO: Add Authorization */

		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);
		final UpdateInterviewingConfigurationInput input = this.graphQLUtil.getInput(environment,
				UpdateInterviewingConfigurationInput.class);

		this.checkIfInterviewUpdateAllowed(input.getInterviewId());

		this.updateInterview(input);

		return Boolean.TRUE;
	}

	private void checkIfInterviewUpdateAllowed(final String interviewId) throws IllegalOperationException {
		final InterviewDAO interviewDAO = this.interViewRepository.findById(interviewId)
				.orElseThrow(() -> new IllegalArgumentException("Interview does not exist"));

		if (InterviewStatus.fromString(interviewDAO.getStatus()).isFeedbackSubmissionCompleted()) {
			throw new IllegalOperationException(ERROR_MESSAGE_INTERVIEW_UPDATION_NOT_ALLOWED,
					ERROR_MESSAGE_INTERVIEW_UPDATION_NOT_ALLOWED, ERROR_CODE_INTERVIEW_UPDATION_NOT_ALLOWED);
		}
	}

	private void updateInterview(final UpdateInterviewingConfigurationInput updateInterviewingConfigurationInput) {
		final InterviewDAO interviewDAO = this.interViewRepository
				.findById(updateInterviewingConfigurationInput.getInterviewId()).get();

		this.interViewRepository.save(
				interviewDAO.toBuilder()
						.interviewStructureId(updateInterviewingConfigurationInput.getInterviewStructureId())
						.build());

		if (updateInterviewingConfigurationInput.getJobRoleId() != null
				&& updateInterviewingConfigurationInput.getJobRoleVersion() != null) {
			this.updateEvaluation(interviewDAO.getEvaluationId(), updateInterviewingConfigurationInput);
		}
	}

	private void updateEvaluation(final String evaluationId,
			final UpdateInterviewingConfigurationInput updateInterviewingConfigurationInput) {

		final EvaluationDAO evaluationDAO = this.evaluationRepository.findById(evaluationId)
				.get();

		this.evaluationRepository.save(evaluationDAO.toBuilder()
				.jobRoleId(updateInterviewingConfigurationInput.getJobRoleId())
				.jobRoleVersion(updateInterviewingConfigurationInput.getJobRoleVersion())
				.build());
	}

	@Override
	public String name() {
		return "updateInterviewingConfiguration";
	}

}
