/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.jobRoleManagement.JobRole.search.filter;

import com.barraiser.common.graphql.input.SearchFilterInput;
import com.barraiser.commons.auth.FilterOperator;
import com.barraiser.commons.auth.SearchFilter;
import com.barraiser.onboarding.common.search.db.SearchFilterMapper;
import com.barraiser.onboarding.dal.JobRoleDAO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class JobRoleActiveCandidatesPresentFilterMapper implements SearchFilterMapper<JobRoleDAO> {
	@Override
	public String field() {
		return "activeCandidatesCountAggregate";
	}

	@Override
	public List<SearchFilter> getSearchFilters(final SearchFilterInput searchFilterInput, final String partnerId) {
		List<SearchFilter> searchFilters = new ArrayList<>();

		for (String v : searchFilterInput.getValue()) {
			if (v.equals("1")) {
				searchFilters.add(
						SearchFilter.builder()
								.name("activeCandidatesCountAggregate")
								.field("activeCandidatesCountAggregate")
								.operator(FilterOperator.GREATER_THAN)
								.value(0)
								.build());
			} else if (v.equals("2")) {
				searchFilters.add(
						SearchFilter.builder()
								.name("activeCandidatesCountAggregate")
								.field("activeCandidatesCountAggregate")
								.operator(FilterOperator.EQUALS)
								.value(0)
								.build());
			}
		}

		return searchFilters;
	}
}
