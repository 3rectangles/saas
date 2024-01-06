/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.expert;

import com.barraiser.common.graphql.types.expertProfile.ExpertInterviewingConfiguration;
import com.barraiser.common.graphql.types.expertProfile.ExpertProfile;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.ExpertDAO;
import com.barraiser.onboarding.expert.auth.UpdateExpertProfileAuthorizer;
import com.barraiser.onboarding.graphql.AuthorizationResult;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLMutation_deprecated;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.IOException;

@Log4j2
@Component
public class UpdateExpertProfileMutation extends AuthorizedGraphQLMutation_deprecated<Boolean> {
	private final GraphQLUtil graphQLUtil;
	private final ExpertService expertService;

	@Override
	public String name() {
		return "updateExpertProfile";
	}

	public UpdateExpertProfileMutation(final UpdateExpertProfileAuthorizer updateExpertProfileAuthorizer,
			final GraphQLUtil graphQLUtil, final ExpertService expertService) {
		super(updateExpertProfileAuthorizer);
		this.graphQLUtil = graphQLUtil;
		this.expertService = expertService;
	}

	@Override
	@Transactional
	protected Boolean fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult)
			throws IOException {
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);
		final ExpertProfile input = this.graphQLUtil.getInput(environment, ExpertProfile.class);
		ExpertDAO expert = this.expertService.findById(input.getExpertId()).get();
		expert = this.updateInterviewingConfiguration(expert, input.getInterviewingConfiguration());
		expert = this.updateMinPriceOfExpert(expert, input.getMinPrice());
		this.expertService.save(expert, authenticatedUser.getUserName());
		return Boolean.TRUE;
	}

	private ExpertDAO updateInterviewingConfiguration(ExpertDAO expertDAO,
			final ExpertInterviewingConfiguration interviewingConfiguration) {
		if (interviewingConfiguration != null) {
			expertDAO = expertDAO
					.toBuilder()
					.gapBetweenInterviews(interviewingConfiguration.getTimeGapBetweenInterviews())
					.build();
		}
		return expertDAO;
	}

	private ExpertDAO updateMinPriceOfExpert(ExpertDAO expertDAO, final Double minPrice) {
		if (minPrice != null) {
			if (expertDAO.getBaseCost() * expertDAO.getMultiplier() < minPrice) {
				throw new IllegalArgumentException("minimum price cannot be greater than the price set");
			}
			expertDAO = expertDAO.toBuilder().minPrice(minPrice).build();
		}
		return expertDAO;
	}
}
