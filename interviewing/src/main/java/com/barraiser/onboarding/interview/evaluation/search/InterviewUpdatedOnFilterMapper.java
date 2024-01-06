/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.search;

import com.barraiser.common.graphql.input.SearchFilterInput;
import com.barraiser.commons.auth.FilterOperator;
import com.barraiser.commons.auth.SearchFilter;
import com.barraiser.onboarding.common.search.db.SearchFilterMapper;
import com.barraiser.onboarding.dal.InterviewDAO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@AllArgsConstructor
public class InterviewUpdatedOnFilterMapper implements SearchFilterMapper<InterviewDAO> {

	private static final String UPDATED_ON = "updatedOn";

	@Override
	public String field() {
		return UPDATED_ON;
	}

	@Override
	public List<SearchFilter> getSearchFilters(final SearchFilterInput searchFilterInput, final String partnerId) {
		return List.of(
				SearchFilter.builder()
						.name(UPDATED_ON)
						.matchAll(List.of(
								SearchFilter.builder()
										.name(UPDATED_ON)
										.field(UPDATED_ON)
										.operator(FilterOperator.GREATER_THAN_OR_EQUAL_TO)
										.value(Instant
												.ofEpochSecond(Long.parseLong(searchFilterInput.getValue().get(0))))
										.build(),
								SearchFilter.builder()
										.name(UPDATED_ON)
										.field(UPDATED_ON)
										.operator(FilterOperator.LESS_THAN_OR_EQUAL_TO)
										.value(Instant
												.ofEpochSecond(Long.parseLong(searchFilterInput.getValue().get(1))))
										.build()))
						.build());
	}
}
