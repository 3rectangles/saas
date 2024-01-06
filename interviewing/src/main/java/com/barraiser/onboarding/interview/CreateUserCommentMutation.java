/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.types.CreateUserCommentInput;
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

import java.util.UUID;

@Log4j2
@Component
public class CreateUserCommentMutation extends AuthorizedGraphQLMutation<UserComment> {

	private final GraphQLUtil graphQLUtil;
	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;
	private final UserCommentRepository userCommentRepository;
	private final UserCommentsMapper userCommentsMapper;

	public CreateUserCommentMutation(AuthorizationServiceFeignClient authorizationServiceFeignClient,
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
		final CreateUserCommentInput input = this.graphQLUtil.getInput(environment, CreateUserCommentInput.class);

		return this.handleUserCommentAddition(authenticatedUser, input);
	}

	private UserComment handleUserCommentAddition(final AuthenticatedUser authenticatedUser,
			final CreateUserCommentInput createUserCommentInput) {

		UserCommentDAO userCommentDAO = UserCommentDAO.builder()
				.id(UUID.randomUUID().toString())
				.entityId(createUserCommentInput.getEntityId())
				.entityType(createUserCommentInput.getEntityType())
				.commentValue(createUserCommentInput.getCommentValue())
				.reactionValue(createUserCommentInput.getReactionValue())
				.type(createUserCommentInput.getType().toString())
				.createdBy(authenticatedUser.getUserName())
				.offsetTime(createUserCommentInput.getOffsetTime())
				.build();
		return this.userCommentsMapper.getUserComment(
				this.userCommentRepository.save(userCommentDAO),
				null);
	}

	@Override
	public String name() {
		return "createUserComment";
	}

}
