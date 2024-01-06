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
public class JobRoleInternalNameFilterMapper implements SearchFilterMapper<JobRoleSearchDAO> {
	@Override
	public String field() {
		return "internal_display_name";
	}

	@Override
	public List<SearchFilter> getSearchFilters(final SearchFilterInput searchFilterInput, final String partnerId) {
		return List.of(SearchFilter.builder()
				.name("internal_display_name")
				.field("internal_display_name")
				.operator(FilterOperator.LIKE)
				.value(searchFilterInput.getValue())
				.build());
	}
}
