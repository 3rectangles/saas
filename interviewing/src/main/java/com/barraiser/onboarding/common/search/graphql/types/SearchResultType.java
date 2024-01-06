/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.common.search.graphql.types;

import com.barraiser.common.graphql.input.FilterType;
import com.barraiser.common.graphql.types.SearchCard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SearchResultType {
	private Integer pageNumber;
	private Integer pageSize;
	private Integer totalPages;
	private Long totalRecords;
	private List<Object> records;
	private List<AggregateResultType> aggregations;
	private List<FilterType> filtersApplied;
	private List<SearchCard> cards;
	private String filterContext;
}
