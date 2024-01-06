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
public class EvaluationHaveQueryForPartnerFilterMapper implements SearchFilterMapper<EvaluationDAO> {

	public static final String HAVE_QUERY_FOR_PARTNER = "haveQueryForPartner";

	@Override
	public String field() {
		return HAVE_QUERY_FOR_PARTNER;
	}

	@Override
	public List<SearchFilter> getSearchFilters(final SearchFilterInput searchFilterInput, final String partnerId) {
		return List.of(
				SearchFilter.builder()
						.name(HAVE_QUERY_FOR_PARTNER)
						.field(HAVE_QUERY_FOR_PARTNER)
						.operator(FilterOperator.EQUALS)
						.value(
								searchFilterInput.getValue().stream()
										.map(Boolean::parseBoolean)
										.collect(Collectors.toList()))
						.build());
	}
}
