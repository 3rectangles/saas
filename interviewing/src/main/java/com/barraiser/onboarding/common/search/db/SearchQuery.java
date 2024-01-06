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
public class SearchQuery {
	private Integer pageNumber;
	private Integer pageSize;

	@Builder.Default
	private List<SearchOrder> sortBy = List.of();

	@Builder.Default
	private List<SearchFilter> filters = List.of();

	private List<SearchAggregation> aggregations;

	private boolean newPortal;
	private String partnerID;
}
