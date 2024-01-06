/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.search;

import com.barraiser.commons.auth.FilterOperator;
import com.barraiser.commons.auth.SearchFilter;
import com.barraiser.onboarding.common.search.db.SearchFilterMapper;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.common.graphql.input.SearchFilterInput;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class EvaluationStatusFilterMapper implements SearchFilterMapper<EvaluationDAO> {
	@Override
	public String field() {
		return "status";
	}

	@Override
	public List<SearchFilter> getSearchFilters(final SearchFilterInput searchFilterInput, final String partnerId) {
		return List.of(
				SearchFilter.builder()
						.name("status")
						.field("displayStatus")
						.operator(FilterOperator.IN)
						.value(searchFilterInput.getValue())
						.build());
	}
}
