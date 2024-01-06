/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.search;

import com.barraiser.commons.auth.FilterOperator;
import com.barraiser.commons.auth.SearchFilter;
import com.barraiser.onboarding.common.search.db.SearchFilterMapper;
import com.barraiser.common.graphql.input.SearchFilterInput;
import com.barraiser.onboarding.dal.EvaluationDAO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@AllArgsConstructor
public class EvaluationCreatedOnFilterMapper implements SearchFilterMapper<EvaluationDAO> {

	@Override
	public String field() {
		return "createdOn";
	}

	@Override
	public List<SearchFilter> getSearchFilters(final SearchFilterInput searchFilterInput, final String partnerId) {
		return List.of(
				SearchFilter.builder()
						.name("createdOn")
						.matchAll(List.of(
								SearchFilter.builder()
										.name("createdOn")
										.field("createdOn")
										.operator(FilterOperator.GREATER_THAN_OR_EQUAL_TO)
										.value(Instant
												.ofEpochSecond(Long.parseLong(searchFilterInput.getValue().get(0))))
										.build(),
								SearchFilter.builder()
										.name("createdOn")
										.field("createdOn")
										.operator(FilterOperator.LESS_THAN_OR_EQUAL_TO)
										.value(Instant
												.ofEpochSecond(Long.parseLong(searchFilterInput.getValue().get(1))))
										.build()))
						.build());
	}
}
