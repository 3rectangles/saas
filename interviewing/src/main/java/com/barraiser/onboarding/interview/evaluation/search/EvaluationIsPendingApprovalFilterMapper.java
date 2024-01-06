/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.search;

import com.barraiser.common.graphql.input.SearchFilterInput;
import com.barraiser.commons.auth.FilterOperator;
import com.barraiser.commons.auth.SearchFilter;
import com.barraiser.onboarding.common.search.db.SearchFilterMapper;
import com.barraiser.onboarding.dal.EvaluationDAO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class EvaluationIsPendingApprovalFilterMapper implements SearchFilterMapper<EvaluationDAO> {

	public static final String IS_PENDING_APPROVAL = "isPendingApproval";

	@Override
	public String field() {
		return IS_PENDING_APPROVAL;
	}

	@Override
	public List<SearchFilter> getSearchFilters(final SearchFilterInput searchFilterInput, final String partnerId) {
		return List.of(
				SearchFilter.builder()
						.name(IS_PENDING_APPROVAL)
						.field(IS_PENDING_APPROVAL)
						.operator(FilterOperator.EQUALS)
						.value(
								searchFilterInput.getValue().stream()
										.map(Boolean::parseBoolean)
										.collect(Collectors.toList()))
						.build());
	}
}
