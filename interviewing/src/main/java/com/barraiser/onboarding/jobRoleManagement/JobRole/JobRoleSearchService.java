/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.jobRoleManagement.JobRole;

import com.barraiser.common.graphql.input.SearchFilterInput;
import com.barraiser.common.graphql.input.SearchInput;
import com.barraiser.common.graphql.input.SearchOrderInput;
import com.barraiser.common.graphql.types.ApplicableFilter;
import com.barraiser.common.graphql.types.ApplicableFilterType;
import com.barraiser.common.graphql.types.JobRole;
import com.barraiser.commons.auth.SearchFilter;
import com.barraiser.onboarding.common.search.db.*;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.FilterMapper;
import com.barraiser.onboarding.interview.jobrole.JobRoleMapper;
import com.barraiser.onboarding.jobRoleManagement.JobRole.graphql.input.SearchJobRoleInput;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.barraiser.commons.auth.FilterOperator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@AllArgsConstructor
@Component
public class JobRoleSearchService {
	private final SearchDBHelper searchDBHelper;
	private final JobRoleRepository jobRoleRepository;
	private final JobRoleMapper jobRoleMapper;
	private final PartnerCompanyRepository partnerCompanyRepository;
	private final FilterRepository filterRepository;
	private final FilterMapper filterMapper;

	public SearchResult<JobRole> findAll(final SearchQuery searchQuery) {
		final Page<JobRoleDAO> searchedPageResult = this.getPageResult(searchQuery);

		return SearchResult.<JobRole>builder()
				.pageResult(this.hydrateSearchedJobRoles(searchedPageResult))
				.build();
	}

	private Page<JobRoleDAO> getPageResult(final SearchQuery searchQuery) {
		final Specification<JobRoleDAO> specification = SearchSpecificationBuilder
				.getSpecificationForFilters(searchQuery.getFilters());
		final Pageable pageRequest = this.searchDBHelper.getPageRequest(searchQuery);

		return this.jobRoleRepository.findAll(specification, pageRequest);
	}

	private Page<JobRole> hydrateSearchedJobRoles(final Page<JobRoleDAO> searchResultPage) {
		return searchResultPage.map(jobRoleMapper::toJobRole);
	}

	public SearchQuery addPartnerIdFilter(final SearchQuery searchQuery, final String partnerId) {
		final List<SearchFilter> searchFilters = new ArrayList<>(searchQuery.getFilters());
		final String value = partnerId == null ? "NULL" : partnerId;

		searchFilters.add(SearchFilter.builder()
				.field("partnerId")
				.operator(FilterOperator.EQUALS)
				.value(value)
				.build());

		return searchQuery.toBuilder().filters(searchFilters).build();
	}

	public SearchQuery addDeprecatedOnFilter(final SearchQuery searchQuery) {
		final List<SearchFilter> searchFilters = new ArrayList<>(searchQuery.getFilters());

		searchFilters.add(SearchFilter.builder()
				.field("deprecatedOn")
				.operator(FilterOperator.IS_NULL)
				.build());

		return searchQuery.toBuilder().filters(searchFilters).build();
	}

	public SearchQuery addSortOnCreatedOn(final SearchQuery searchQuery) {

		List<SearchOrder> searchOrders = new ArrayList<>(searchQuery.getSortBy());
		searchOrders.add(SearchOrder.builder()
				.field("createdOn")
				.sortByAscending(false)
				.build());

		return searchQuery.toBuilder().sortBy(searchOrders).build();
	}

	public String getJobRoleContext(final String partnerId) {
		return "{\"context\":\"JobRolePage\",\"partnerId\":\"" + partnerId + "\",\"model\":\""
				+ this.getModelForPartner(partnerId) + "\"}";
	}

	public String getModelForPartner(final String partnerId) {
		final Optional<PartnerCompanyDAO> partnerCompanyDAO = this.partnerCompanyRepository.findById(partnerId);
		if (partnerCompanyDAO.isEmpty() ||
				partnerCompanyDAO.get().getPartnershipModelId() == null)
			return "iaas";

		return partnerCompanyDAO.get().getPartnershipModelId();
	}

	public SearchInput getDefaultQueryForPartnershipModel(SearchInput searchJobRoleQuery, final String partnerId) {
		List<FilterDAO> defaultFilters = this.filterRepository
				.findAllByFilterContextAndDefaultValueNotNull(
						this.filterMapper.getSearchFilterContext(getJobRoleContext(partnerId)));

		List<SearchFilterInput> searchFilterInputs = new ArrayList<>();
		List<SearchOrderInput> searchOrderInputs = new ArrayList<>();
		for (FilterDAO f : defaultFilters) {
			if (f.getFilterType().equals(ApplicableFilterType.SEARCH)) {
				List<String> values = new ArrayList<>();
				values.add(f.getDefaultValue());
				searchFilterInputs.add(
						SearchFilterInput.builder()
								.field(f.getName())
								.value(values)
								.build());
			}
			if (f.getFilterType().equals(ApplicableFilterType.SORT)) {
				searchOrderInputs.add(
						SearchOrderInput.builder()
								.field(f.getName())
								.ascending(f.getDefaultValue().equals("ASCENDING"))
								.build());
			}
		}

		return searchJobRoleQuery.toBuilder()
				.filters(searchFilterInputs)
				.sortBy(searchOrderInputs)
				.build();
	}
}
