/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user.graphql;

import com.barraiser.common.graphql.types.Interviewer;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.UserRole;
import com.barraiser.onboarding.auth.AuthorizationException;
import com.barraiser.onboarding.graphql.AuthorizationResult;
import com.barraiser.onboarding.graphql.GraphQLAbacAuthorizer;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class InterviewerUserDetailsAuthorizer implements GraphQLAbacAuthorizer {
	private final GraphQLUtil graphQLUtil;

	private static final List<String> ALL_FIELDS = List.of(
			"id",
			"email",
			"firstName",
			"isdCode",
			"phone",
			"roles",
			"role",
			"lastName",
			"almaMater",
			"currentCompanyName",
			"workExperienceInMonths",
			"lastCompanies",
			"category", "whatsappNumber");
	private static final List<String> NON_PI_FIELDS = List.of(
			"roles",
			"role",
			"almaMater",
			"currentCompanyName",
			"workExperienceInMonths",
			"lastCompanies",
			"category", "whatsappNumber");

	@Override
	public AuthorizationResult authorize(DataFetchingEnvironment environment) {
		final AuthenticatedUser authenticatedUser = graphQLUtil.getLoggedInUser(environment);
		final Interviewer interviewer = environment.getSource();

		final List<String> authorizedFields;

		// Who should have the rights to look at the PI details of an interviewer?
		// In our application context, it should just be the partner who does not have
		// other means to access the details
		// of the expert. Also, it should not depend on the interview type, but it
		// should depend on who the interviewer is.
		// Hence, we should either compare the partnerId or email address.
		if (authenticatedUser.getRoles().contains(UserRole.ADMIN) ||
				authenticatedUser.getRoles().contains(UserRole.OPS) ||
				((authenticatedUser.getRoles().contains(UserRole.PARTNER) ||
						authenticatedUser.getRoles().contains(UserRole.PARTNER_EMPLOYEE))
						&& interviewer.getTenantId() != null
						&& authenticatedUser.getPartnerId().contains(interviewer.getTenantId()))) {
			authorizedFields = ALL_FIELDS;
		} else if (authenticatedUser.getUserName().equals(interviewer.getId())) {
			authorizedFields = ALL_FIELDS;
		} else {
			throw new AuthorizationException("User does not have permission to view the expert details");
		}

		return AuthorizationResult.builder()
				.readableFields(authorizedFields)
				.build();
	}
}
