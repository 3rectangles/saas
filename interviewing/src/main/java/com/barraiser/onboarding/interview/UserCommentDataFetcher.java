/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.types.GetUserCommentInput;
import com.barraiser.common.graphql.types.UserComment;
import com.barraiser.common.utilities.ObjectFieldsFilter;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.onboarding.auth.AllowAuthenticatedUserAuthorizationInputConstructor;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.interview.evaluation.dal.*;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.barraiser.onboarding.graphql.NamedDataFetcher.QUERY_TYPE;

@Component
public class UserCommentDataFetcher extends AuthorizedGraphQLQuery<List<UserComment>> {

	private final GraphQLUtil graphQLUtil;
	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;
	private final UserCommentRepository userCommentRepository;
	private final UserCommentsMapper userCommentsMapper;

	public UserCommentDataFetcher(AuthorizationServiceFeignClient authorizationServiceFeignClient,
			AllowAuthenticatedUserAuthorizationInputConstructor allowAuthenticatedUserAuthorizationInputConstructor,
			ObjectFieldsFilter objectFieldsFilter,
			UserCommentRepository userCommentRepository,
			UserCommentsMapper userCommentsMapper,
			GraphQLUtil graphQLUtil) {

		super(authorizationServiceFeignClient, allowAuthenticatedUserAuthorizationInputConstructor, objectFieldsFilter);
		this.graphQLUtil = graphQLUtil;
		this.authorizationServiceFeignClient = authorizationServiceFeignClient;
		this.userCommentRepository = userCommentRepository;
		this.userCommentsMapper = userCommentsMapper;
	}

	@Override
	protected List<UserComment> fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult) {

		/* TODO: Add Authorization */
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);
		final GetUserCommentInput input = this.graphQLUtil.getInput(environment, GetUserCommentInput.class);

		return this.getUserCommentsList(input);
	}

	private List<UserComment> getUserCommentsList(final GetUserCommentInput getUserCommentInput) {

		final List<UserCommentDAO> userCommentDAOS = this.userCommentRepository
				.findByEntityId(getUserCommentInput.getEntityId());
		final List<UserComment> userCommentList = new ArrayList<>();

		for (UserCommentDAO userCommentDAO : userCommentDAOS) {
			if (userCommentDAO.getDeletedOn() == null) {
				final List<UserComment> justifications = new ArrayList<>();

				final List<UserCommentDAO> savedJustifications = this.userCommentRepository
						.findByEntityId(userCommentDAO.getId());
				for (UserCommentDAO savedJustification : savedJustifications)
					justifications.add(this.userCommentsMapper.getUserComment(savedJustification, null));

				userCommentList.add(this.userCommentsMapper.getUserComment(userCommentDAO, justifications));
			}
		}
		return userCommentList;
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of(QUERY_TYPE, "getUserComments"));
	}
}
