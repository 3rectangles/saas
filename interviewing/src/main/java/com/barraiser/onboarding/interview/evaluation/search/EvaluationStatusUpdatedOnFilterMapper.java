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
public class EvaluationStatusUpdatedOnFilterMapper implements SearchFilterMapper<EvaluationDAO> {

	@Override
	public String field() {
		return "statusUpdatedOn";
	}

	@Override
	public List<SearchFilter> getSearchFilters(final SearchFilterInput searchFilterInput, final String partnerId) {
		return List.of(
				SearchFilter.builder()
						.name("statusUpdatedOn")
						.matchAll(List.of(
								SearchFilter.builder()
										.name("statusUpdatedOn")
										.field("statusUpdatedOn")
										.operator(FilterOperator.GREATER_THAN_OR_EQUAL_TO)
										.value(Instant
												.ofEpochSecond(Long.parseLong(searchFilterInput.getValue().get(0))))
										.build(),
								SearchFilter.builder()
										.name("statusUpdatedOn")
										.field("statusUpdatedOn")
										.operator(FilterOperator.LESS_THAN_OR_EQUAL_TO)
										.value(Instant
												.ofEpochSecond(Long.parseLong(searchFilterInput.getValue().get(1))))
										.build()))
						.build());
	}
}
