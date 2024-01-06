/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.onboarding.auth.AllowAuthenticatedUserAuthorizationInputConstructor;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLMutation;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.interview.evaluation.dal.UserCommentDAO;
import com.barraiser.onboarding.interview.evaluation.dal.UserCommentRepository;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Log4j2
@Component
public class DeleteUserCommentMutation extends AuthorizedGraphQLMutation<Boolean> {
	private final GraphQLUtil graphQLUtil;
	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;
	private final UserCommentRepository userCommentRepository;

	public DeleteUserCommentMutation(AuthorizationServiceFeignClient authorizationServiceFeignClient,
			AllowAuthenticatedUserAuthorizationInputConstructor allowAuthenticatedUserAuthorizationInputConstructor,
			UserCommentRepository userCommentRepository,
			GraphQLUtil graphQLUtil) {
		super(authorizationServiceFeignClient, allowAuthenticatedUserAuthorizationInputConstructor);
		this.graphQLUtil = graphQLUtil;
		this.authorizationServiceFeignClient = authorizationServiceFeignClient;
		this.userCommentRepository = userCommentRepository;
	}

	@Override
	protected Boolean fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult)
			throws Exception {
		/* TODO: Add Authorization */

		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);
		final String userCommentId = this.graphQLUtil.getInput(environment, String.class);

		final Optional<UserCommentDAO> existingUserComment = this.userCommentRepository.findById(userCommentId);

		if (existingUserComment.isPresent() && existingUserComment.get().getDeletedOn() == null) {
			final UserCommentDAO userCommentDAO = existingUserComment.get();
			this.deleteLinkedComments(userCommentDAO.getId(), authenticatedUser);

			this.userCommentRepository.save(userCommentDAO.toBuilder()
					.deletedOn(Instant.now())
					.build());

			log.info(String.format("DeleteUserCommentMutation ID:%s UserID:%s", userCommentDAO.getId(),
					authenticatedUser));
			return Boolean.TRUE;

		}
		return Boolean.FALSE;
	}

	private void deleteLinkedComments(final String parentCommentId, final AuthenticatedUser authenticatedUser) {
		final List<UserCommentDAO> userCommentDAOList = this.userCommentRepository.findByEntityId(parentCommentId);

		for (UserCommentDAO userCommentDAO : userCommentDAOList) {
			this.userCommentRepository.save(userCommentDAO.toBuilder()
					.deletedOn(Instant.now())
					.build());

			log.info(String.format("DeleteUserCommentMutationJustification ID:%s UserID:%s", userCommentDAO.getId(),
					authenticatedUser));
		}
	}

	@Override
	public String name() {
		return "deleteUserComment";
	}
}
