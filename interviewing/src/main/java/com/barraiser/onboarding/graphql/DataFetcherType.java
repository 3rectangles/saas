/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.graphql;

import lombok.Getter;

public enum DataFetcherType {
	QUERY("Query"), MUTATION("Mutation"), TYPE("Type");

	@Getter
	private String value;

	DataFetcherType(final String str) {
		this.value = str;
	}
}
