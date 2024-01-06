/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.types.UpdateUserCommentInput;
import com.barraiser.common.graphql.types.UserComment;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.onboarding.auth.AllowAuthenticatedUserAuthorizationInputConstructor;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLMutation;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.interview.evaluation.dal.UserCommentDAO;
import com.barraiser.onboarding.interview.evaluation.dal.UserCommentRepository;
import com.barraiser.onboarding.interview.evaluation.dal.UserCommentsMapper;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Log4j2
@Component
public class UpdateUserCommentMutation extends AuthorizedGraphQLMutation<UserComment> {

	private final GraphQLUtil graphQLUtil;
	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;
	private final UserCommentRepository userCommentRepository;
	private final UserCommentsMapper userCommentsMapper;

	public UpdateUserCommentMutation(AuthorizationServiceFeignClient authorizationServiceFeignClient,
			AllowAuthenticatedUserAuthorizationInputConstructor allowAuthenticatedUserAuthorizationInputConstructor,
			UserCommentRepository userCommentRepository,
			UserCommentsMapper userCommentsMapper,
			GraphQLUtil graphQLUtil) {
		super(authorizationServiceFeignClient, allowAuthenticatedUserAuthorizationInputConstructor);
		this.graphQLUtil = graphQLUtil;
		this.authorizationServiceFeignClient = authorizationServiceFeignClient;
		this.userCommentRepository = userCommentRepository;
		this.userCommentsMapper = userCommentsMapper;
	}

	@Override
	protected UserComment fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult) {
		/* TODO: Add Authorization */

		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);
		final UpdateUserCommentInput input = this.graphQLUtil.getInput(environment, UpdateUserCommentInput.class);

		return this.handleUserCommentUpdate(input, authenticatedUser);
	}

	private UserComment handleUserCommentUpdate(final UpdateUserCommentInput updateUserCommentInput,
			AuthenticatedUser user) {

		log.info(String.format("UpdateUserCommentMutation id:%s by:%s", updateUserCommentInput.getUserCommentId(),
				user.getUserName()));

		final UserCommentDAO existingUserComment = this.userCommentRepository
				.findById(updateUserCommentInput.getUserCommentId()).get();

		final UserCommentDAO userCommentDAO = existingUserComment.toBuilder()
				.id(UUID.randomUUID().toString())
				.commentValue(updateUserCommentInput.getCommentValue())
				.reactionValue(updateUserCommentInput.getReactionValue())
				.build();

		this.userCommentRepository.save(existingUserComment.toBuilder()
				.deletedOn(Instant.now())
				.build());

		return this.userCommentsMapper.getUserComment(
				this.userCommentRepository.save(userCommentDAO), null);

	}

	@Override
	public String name() {
		return "updateUserComment";
	}
}
