/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types;

public enum ApplicableFilterType {
	SEARCH("SEARCH"), SORT("SORT");

	private final String applicableFilterType;

	ApplicableFilterType(final String applicableFilterType) {
		this.applicableFilterType = applicableFilterType;
	}

	public String getValue() {
		return this.applicableFilterType;
	}
}
