/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.graphql;

import graphql.schema.DataFetcher;

public interface NamedDataFetcher<T> extends DataFetcher<T> {
	String QUERY_TYPE = "Query";
	String MUTATION_TYPE = "Mutation";

	String name();

	String type();
}
