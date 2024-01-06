/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.graphql;

import graphql.schema.DataFetcher;

import java.util.List;

@Deprecated(forRemoval = true)
public interface MultiParentTypeDataFetcher<T> extends DataFetcher<T> {
	String QUERY_TYPE = "Query";
	String MUTATION_TYPE = "Mutation";

	/**
	 * A List of lists of size 2 for the property and its parent type.
	 * e.g.
	 * <code>
	 * List.of(
	 *  List.of("AuthorizedGraphQLQuery", "getInterview"),
	 *  List.of("Evaluation", "interviews")
	 * )
	 * </code>
	 */
	List<List<String>> typeNameMap();
}
