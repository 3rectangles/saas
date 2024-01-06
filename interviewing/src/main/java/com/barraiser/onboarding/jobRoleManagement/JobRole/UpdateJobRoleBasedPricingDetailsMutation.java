/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.jobRoleManagement.JobRole;

import com.barraiser.common.DTO.pricing.JobRoleBasedPricingUpdationResult;
import com.barraiser.common.graphql.input.UpdateJobRoleBasedPricingInput;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.graphql.AuthorizationResult;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLMutation_deprecated;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.jobRoleManagement.JobRole.auth.UpdateJobRoleBasedPricingAuthorizer;
import com.barraiser.onboarding.jobRoleManagement.JobRolePricingDetailsUpdator;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class UpdateJobRoleBasedPricingDetailsMutation
		extends AuthorizedGraphQLMutation_deprecated<JobRoleBasedPricingUpdationResult> {
	private final GraphQLUtil graphQLUtil;
	private final JobRolePricingDetailsUpdator jobRolePricingDetailsUpdator;

	@Override
	protected JobRoleBasedPricingUpdationResult fetch(DataFetchingEnvironment environment,
			AuthorizationResult authorizationResult) {
		final AuthenticatedUser user = this.graphQLUtil.getLoggedInUser(environment);
		final UpdateJobRoleBasedPricingInput input = this.graphQLUtil.getInput(environment,
				UpdateJobRoleBasedPricingInput.class);
		return this.jobRolePricingDetailsUpdator.update(input.getJobRoleBasedPricings(), user.getUserName());
	}

	@Override
	public String name() {
		return "updateJobRoleBasedPricing";
	}

	public UpdateJobRoleBasedPricingDetailsMutation(final UpdateJobRoleBasedPricingAuthorizer abacAuthorizer,
			final GraphQLUtil graphQLUtil, final JobRolePricingDetailsUpdator jobRolePricingDetailsUpdator) {
		super(abacAuthorizer);
		this.graphQLUtil = graphQLUtil;
		this.jobRolePricingDetailsUpdator = jobRolePricingDetailsUpdator;
	}
}
