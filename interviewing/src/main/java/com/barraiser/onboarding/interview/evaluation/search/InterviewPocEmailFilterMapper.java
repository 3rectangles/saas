/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.search;

import com.barraiser.common.graphql.input.SearchFilterInput;
import com.barraiser.commons.auth.FilterOperator;
import com.barraiser.commons.auth.SearchFilter;
import com.barraiser.onboarding.common.search.db.SearchFilterMapper;
import com.barraiser.onboarding.dal.InterviewDAO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class InterviewPocEmailFilterMapper implements SearchFilterMapper<InterviewDAO> {

	private static final String POC_EMAIL = "pocEmail";

	@Override
	public String field() {
		return POC_EMAIL;
	}

	@Override
	public List<SearchFilter> getSearchFilters(final SearchFilterInput searchFilterInput, final String partnerId) {
		return List.of(
				SearchFilter.builder()
						.name(POC_EMAIL)
						.field(POC_EMAIL)
						.operator(FilterOperator.LIKE_IGNORE_CASE)
						.value(searchFilterInput.getValue().get(0))
						.build());
	}
}
