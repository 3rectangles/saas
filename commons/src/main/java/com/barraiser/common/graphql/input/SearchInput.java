/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.input;

//
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SearchInput {
	private Integer pageNumber;
	private Integer pageSize;

	private List<SearchOrderInput> sortBy;

	private List<SearchFilterInput> filters;

	private List<AggregateInput> aggregates;

	private boolean newPortal;
	private String partnerID;
}
