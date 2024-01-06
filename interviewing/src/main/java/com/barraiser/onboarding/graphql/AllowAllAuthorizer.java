/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.graphql;

import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AllowAllAuthorizer implements GraphQLAbacAuthorizer {
	@Override
	public AuthorizationResult authorize(DataFetchingEnvironment environment) {
		return AuthorizationResult.builder()
				.readableFields(List.of("*"))
				.build();
	}
}
