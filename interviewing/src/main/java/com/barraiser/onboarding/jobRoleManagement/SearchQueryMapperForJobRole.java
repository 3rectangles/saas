/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.jobRoleManagement;

import com.barraiser.common.graphql.input.SearchFilterInput;
import com.barraiser.common.graphql.input.SearchInput;
import com.barraiser.common.graphql.input.SearchOrderInput;
import com.barraiser.common.graphql.types.ApplicableFilterType;
import com.barraiser.common.graphql.types.FieldType;
import com.barraiser.commons.auth.FilterOperator;
import com.barraiser.commons.auth.SearchFilter;
import com.barraiser.onboarding.common.search.db.SearchFilterMapper;
import com.barraiser.onboarding.common.search.db.SearchOrder;
import com.barraiser.onboarding.common.search.db.SearchQuery;
import com.barraiser.onboarding.dal.FilterDAO;
import com.barraiser.onboarding.dal.FilterRepository;
import com.barraiser.onboarding.dal.JobRoleDAO;
import com.barraiser.onboarding.interview.FilterMapper;
import com.barraiser.onboarding.jobRoleManagement.JobRole.JobRoleSearchService;
import com.barraiser.onboarding.search.SearchQueryMapperUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Log4j2
public class SearchQueryMapperForJobRole {

	private SearchQueryMapperUtil searchQueryMapperUtil;

	private FilterRepository filterRepository;

	private FilterMapper filterMapper;

	private JobRoleSearchService jobRoleSearchService;

	private List<SearchFilterMapper<JobRoleDAO>> jobRoleSearchFilters;

	public SearchQuery mapSearchQuery(final SearchInput searchInput, Class<?> object, final String partnerId) {
		SearchQuery searchQuery = SearchQuery.builder()
				.pageNumber(searchInput.getPageNumber())
				.pageSize(searchInput.getPageSize())
				.build();
		if (searchInput.getSortBy() != null) {
			searchQuery = searchQuery.toBuilder()
					.sortBy(searchInput.getSortBy().stream().map(this::mapSearchOrder)
							.flatMap(Collection::stream).collect(Collectors.toList()))
					.build();
		}
		if (searchInput.getFilters() != null) {
			if (object == JobRoleDAO.class) {
				searchQuery = searchQuery.toBuilder()
						.filters(searchInput.getFilters().stream()
								.map(a -> mapSearchFilter(a, jobRoleSearchService.getJobRoleContext(partnerId)))
								.flatMap(Collection::stream)
								.collect(Collectors.toList()))
						.build();
			}
		}

		return searchQuery;
	}

	private List<SearchFilter> mapSearchFilter(final SearchFilterInput searchFilterInput, final String filterContext) {
		List<SearchFilter> searchFilterList = new ArrayList<>();
		List<String> values = searchFilterInput.getValue();

		FilterDAO filterDAO = this.filterRepository.findByFilterContextAndNameAndFilterType(
				this.filterMapper.getSearchFilterContext(filterContext),
				searchFilterInput.getField(), ApplicableFilterType.SEARCH).get();

		if (filterDAO.getFieldType().equals(FieldType.INT)) {
			searchFilterList.add(
					SearchFilter.builder()
							.name(searchFilterInput.getField())
							.matchAll(List.of(
									SearchFilter.builder()
											.name(searchFilterInput.getField())
											.field(filterDAO.getInternalName())
											.operator(FilterOperator.GREATER_THAN_OR_EQUAL_TO)
											.value(Integer.parseInt(values.get(0)))
											.build(),
									SearchFilter.builder()
											.name(searchFilterInput.getField())
											.field(filterDAO.getInternalName())
											.operator(FilterOperator.LESS_THAN_OR_EQUAL_TO)
											.value(Integer.parseInt(values.get(1)))
											.build()))
							.build());
			return searchFilterList;
		}

		else if (FilterOperator.valueOf(filterDAO.getOperationsPossible().get(0))
				.equals(FilterOperator.valueOf("IN"))) {
			return List.of(SearchFilter.builder()
					.name(searchFilterInput.getField())
					.field(filterDAO.getInternalName())
					.operator(FilterOperator.IN)
					.value(searchFilterInput.getValue())
					.build());
		} else if (filterDAO.getInternalName() == null) {
			for (SearchFilterMapper s : jobRoleSearchFilters) {
				if (s.field().equals(searchFilterInput.getField()))
					searchFilterList.addAll(s.getSearchFilters(searchFilterInput, null));
			}
		} else {
			List<SearchFilter> filterSearchList = new ArrayList<>();
			for (String v : values) {
				filterSearchList.add(SearchFilter.builder()
						.name(searchFilterInput.getField())
						.field(filterDAO.getInternalName())
						.operator(FilterOperator.valueOf(filterDAO.getOperationsPossible().get(0)))
						.value(v)
						.build());
			}
			searchFilterList.add(
					SearchFilter.builder()
							.matchAnyOf(filterSearchList)
							.build());
		}

		return searchFilterList;
	}

	public List<SearchOrder> mapSearchOrder(final SearchOrderInput searchOrderInput) {
		return List.of(
				SearchOrder.builder()
						.field(searchOrderInput.getField())
						.sortByAscending(searchOrderInput.getAscending())
						.build());
	}

}
