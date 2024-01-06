/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.graphql;

@Deprecated(forRemoval = true)
public interface GraphQLMutation<T> extends NamedDataFetcher<T> {
	default String type() {
		return MUTATION_TYPE;
	}
}
