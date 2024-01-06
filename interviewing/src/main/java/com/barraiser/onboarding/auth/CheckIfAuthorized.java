/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth;

import com.barraiser.onboarding.auth.enums.Action;
import com.barraiser.onboarding.auth.graphql.input.AuthorizationInput;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.graphql.Constants;
import com.barraiser.onboarding.graphql.GraphQLQuery;
import com.barraiser.onboarding.graphql.GraphQLUtil;

import com.barraiser.common.graphql.input.ResourceAttribute;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class CheckIfAuthorized implements GraphQLQuery<Boolean> {

	private final GraphQLUtil graphQLUtil;
	private final Authorizer authorizer;

	@Override
	public Boolean get(final DataFetchingEnvironment environment) throws Exception {

		final AuthorizationInput input = this.graphQLUtil.getArgument(environment, Constants.CONTEXT_KEY_INPUT,
				AuthorizationInput.class);
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);
		final Action action = input.getAction();

		final AuthorizationResourceDTO authorizationResource = AuthorizationResourceDTO.builder()
				.type(input.getResourceType())
				.resource(this.getResourceAttributeMap(input.getResourceAttributes()))
				.build();

		try {
			this.authorizer.can(authenticatedUser, action.getValue(), authorizationResource);
		} catch (AuthorizationException authEx) {
			return false;
		}

		return true;
	}

	private Map<String, String> getResourceAttributeMap(final List<ResourceAttribute> resourceAttributes) {
		return resourceAttributes.stream()
				.collect(Collectors.toMap(ResourceAttribute::getKey, ResourceAttribute::getValue));
	}

	@Override
	public String name() {
		return "checkIfAuthorized";
	}
}
