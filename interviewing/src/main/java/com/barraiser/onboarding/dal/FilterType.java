/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

public enum FilterType {
	SORT("SORT"), SEARCH("SEARCH");

	private final String filterType;

	FilterType(final String filterType) {
		this.filterType = filterType;
	}

	public String getValue() {
		return this.filterType;
	}
}
