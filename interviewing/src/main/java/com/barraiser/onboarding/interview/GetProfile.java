/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import com.barraiser.onboarding.graphql.Constants;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.common.graphql.input.ProfileInput;
import com.barraiser.common.graphql.types.Profile;
import graphql.GraphQLContext;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Log4j2
@Component
@AllArgsConstructor
public class GetProfile implements NamedDataFetcher<DataFetcherResult<Object>> {
	private final UserDetailsRepository userDetailsRepository;
	private final GraphQLUtil graphQLUtil;

	@Override
	public DataFetcherResult<Object> get(final DataFetchingEnvironment environment) throws Exception {
		final ProfileInput input = this.graphQLUtil.getArgument(environment, "input", ProfileInput.class);
		final String expertId = input.getExpertId();
		log.info("getExpertInterviews for expert id : {}", expertId);
		// authenticate user
		final GraphQLContext context = environment.getContext();
		final AuthenticatedUser authenticatedUser = context.get(Constants.CONTEXT_KEY_USER);
		final String responseError = "Either this page does not exists or you do not have the permissions to view this page";
		if (authenticatedUser == null) {
			throw new IllegalArgumentException(responseError);
		}
		// check if the expert id given is actually an expert and is active and is the
		// currently logged in user
		final Optional<UserDetailsDAO> userDetails = this.userDetailsRepository.findById(expertId);
		if (userDetails.isPresent()) {
			if (!userDetails.get().getId().equals(authenticatedUser.getUserName())) {
				throw new IllegalArgumentException(responseError);
			}
			if (!userDetails.get().getIsExpertPartner()) {
				throw new IllegalArgumentException(responseError);
			}
		} else {
			throw new IllegalArgumentException(responseError);
		}
		final Profile profile = Profile.builder()
				.expertId(expertId)
				.build();
		return DataFetcherResult.newResult().data(profile).build();
	}

	@Override
	public String name() {
		return "getProfile";
	}

	@Override
	public String type() {
		return QUERY_TYPE;
	}
}
