/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.graphql;

@Deprecated(forRemoval = true)
public interface GraphQLQuery<T> extends NamedDataFetcher<T> {
	default String type() {
		return QUERY_TYPE;
	}
}
