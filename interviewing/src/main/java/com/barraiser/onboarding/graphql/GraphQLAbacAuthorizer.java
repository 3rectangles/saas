/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.graphql;

import graphql.schema.DataFetchingEnvironment;

public interface GraphQLAbacAuthorizer {
	AuthorizationResult authorize(final DataFetchingEnvironment environment);
}
