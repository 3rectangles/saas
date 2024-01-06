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
public class EvaluationUserNameFilterMapper implements SearchFilterMapper<EvaluationDAO> {

	@Override
	public String field() {
		return "candidateName";
	}

	@Override
	public List<SearchFilter> getSearchFilters(final SearchFilterInput searchFilterInput, final String partnerId) {
		return List.of(
				SearchFilter.builder()
						.name("candidateName")
						.field("candidateName")
						.operator(FilterOperator.LIKE_IGNORE_CASE)
						.value(searchFilterInput.getValue().get(0))
						.build());
	}
}
