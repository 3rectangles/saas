/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.common.search.db;

import com.barraiser.commons.auth.SearchFilter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SearchAggregation {
	private String field;
	private String name;
	private List<String> path;
	private List<SearchFilter> filters;
}
