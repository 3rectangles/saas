/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.search;

import com.barraiser.commons.auth.FilterOperator;
import com.barraiser.commons.auth.SearchFilter;
import com.barraiser.onboarding.common.search.db.SearchFilterMapper;
import com.barraiser.common.graphql.input.SearchFilterInput;
import com.barraiser.onboarding.dal.EvaluationDAO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class EvaluationPocEmailFilterMapper implements SearchFilterMapper<EvaluationDAO> {

	@Override
	public String field() {
		return "pocEmail";
	}

	@Override
	public List<SearchFilter> getSearchFilters(final SearchFilterInput searchFilterInput, final String partnerId) {
		return List.of(
				SearchFilter.builder()
						.name("pocEmail")
						.field("pocEmail")
						.operator(FilterOperator.LIKE_IGNORE_CASE)
						.value(searchFilterInput.getValue().get(0))
						.build());
	}
}
