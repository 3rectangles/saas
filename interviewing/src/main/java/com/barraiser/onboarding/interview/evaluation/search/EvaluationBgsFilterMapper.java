/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.search;

import com.barraiser.commons.auth.FilterOperator;
import com.barraiser.commons.auth.SearchFilter;
import com.barraiser.onboarding.common.search.db.SearchFilterMapper;
import com.barraiser.common.graphql.input.SearchFilterInput;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.interview.evaluation.scores.ScoreScaleConverter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class EvaluationBgsFilterMapper implements SearchFilterMapper<EvaluationDAO> {
	final ScoreScaleConverter scaleConverter;

	@Override
	public String field() {
		return "bgs";
	}

	@Override
	public List<SearchFilter> getSearchFilters(final SearchFilterInput searchFilterInput, String partnerId) {
		Float bgsFrom = Float.parseFloat(searchFilterInput.getValue().get(0));
		Float bgsTo = Float.parseFloat(searchFilterInput.getValue().get(1));
		int bgsFromInt = (int) scaleConverter.convertScoreTo800(bgsFrom, partnerId);
		int bgsToInt = (int) scaleConverter.convertScoreTo800(bgsTo, partnerId);
		return List.of(
				SearchFilter.builder()
						.name("bgs")
						.matchAll(List.of(
								SearchFilter.builder()
										.name("bgs")
										.field("bgs")
										.operator(FilterOperator.GREATER_THAN_OR_EQUAL_TO)
										.value(bgsFromInt)
										.build(),
								SearchFilter.builder()
										.name("bgs")
										.field("bgs")
										.operator(FilterOperator.LESS_THAN_OR_EQUAL_TO)
										.value(bgsToInt)
										.build()))
						.build());
	}
}
