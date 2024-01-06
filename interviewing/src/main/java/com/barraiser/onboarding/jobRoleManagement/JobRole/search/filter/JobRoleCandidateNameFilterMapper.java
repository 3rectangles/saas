/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.jobRoleManagement.JobRole.search.filter;

import com.barraiser.common.graphql.input.SearchFilterInput;
import com.barraiser.commons.auth.FilterOperator;
import com.barraiser.commons.auth.SearchFilter;
import com.barraiser.onboarding.common.search.db.SearchFilterMapper;
import com.barraiser.onboarding.jobRoleManagement.JobRole.search.dal.JobRoleSearchDAO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class JobRoleCandidateNameFilterMapper implements SearchFilterMapper<JobRoleSearchDAO> {
	@Override
	public String field() {
		return "candidate_display_name";
	}

	@Override
	public List<SearchFilter> getSearchFilters(final SearchFilterInput searchFilterInput, final String partnerId) {
		return List.of(SearchFilter.builder()
				.name("candidate_display_name")
				.field("candidate_display_name")
				.operator(FilterOperator.LIKE)
				.value(searchFilterInput.getValue())
				.build());
	}
}
