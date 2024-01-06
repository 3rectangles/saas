/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.common.search.db;

import com.barraiser.common.graphql.input.SearchFilterInput;
import com.barraiser.commons.auth.SearchFilter;

import java.util.List;

public interface SearchFilterMapper<T> {
	String field();

	List<SearchFilter> getSearchFilters(final SearchFilterInput searchFilterInput, String partnerId);
}
