/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.input.AggregateInput;
import com.barraiser.common.graphql.input.SearchFilterInput;
import com.barraiser.common.graphql.input.SearchInput;
import com.barraiser.common.graphql.input.SearchOrderInput;
import com.barraiser.onboarding.common.search.db.SearchAggregation;
import com.barraiser.onboarding.common.search.db.SearchAggregationMapper;

import com.barraiser.commons.auth.FilterOperator;
import com.barraiser.commons.auth.SearchFilter;
import com.barraiser.onboarding.common.search.db.SearchFilterMapper;
import com.barraiser.onboarding.common.search.db.SearchOrder;
import com.barraiser.onboarding.common.search.db.SearchOrderMapper;
import com.barraiser.onboarding.common.search.db.SearchQuery;
import com.barraiser.onboarding.dal.InterviewDAO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Log4j2
public class SearchQueryMapperForInterview {

	private List<SearchFilterMapper<InterviewDAO>> searchFilterMappers;

	private List<SearchAggregationMapper> searchAggregationMappers;

	private List<SearchOrderMapper> searchOrderMappers;

	public SearchQuery mapSearchQuery(final SearchInput searchInput, final String partnerId) {
		SearchQuery searchQuery = SearchQuery.builder()
				.pageNumber(searchInput.getPageNumber())
				.pageSize(searchInput.getPageSize())
				.build();
		if (searchInput.getSortBy() != null) {
			searchQuery = searchQuery.toBuilder()
					.sortBy(searchInput.getSortBy().stream().map(this::mapSearchOrder).flatMap(Collection::stream)
							.collect(Collectors.toList()))
					.build();
		}
		if (searchInput.getFilters() != null) {
			searchQuery = searchQuery.toBuilder()
					.filters(searchInput.getFilters().stream()
							.map(filter -> mapSearchFilter(partnerId, filter))
							.filter(Objects::nonNull)
							.flatMap(Collection::stream)
							.collect(Collectors.toList()))
					.build();
		}
		if (searchInput.getAggregates() != null) {
			searchQuery = searchQuery.toBuilder()
					.aggregations(this.mapAggregation(searchInput.getAggregates()))
					.build();
		}
		return searchQuery;
	}

	private List<SearchOrder> mapSearchOrder(final SearchOrderInput searchOrderInput) {
		for (final SearchOrderMapper mapper : this.searchOrderMappers) {
			if (mapper.field().equals(searchOrderInput.getField())) {
				return mapper.getSearchOrders(searchOrderInput);
			}
		}
		throw new IllegalArgumentException("sort for " + searchOrderInput.getField() + " is not defined");
	}

	private List<SearchFilter> mapSearchFilter(final SearchFilterInput searchFilterInput) {
		return mapSearchFilter(null, searchFilterInput);
	}

	private List<SearchFilter> mapSearchFilter(String partnerId, final SearchFilterInput searchFilterInput) {
		for (final SearchFilterMapper mapper : this.searchFilterMappers) {
			if (mapper.field().equals(searchFilterInput.getField())) {
				if (searchFilterInput.getValue() != null && !searchFilterInput.getValue().isEmpty()) {
					return mapper.getSearchFilters(searchFilterInput, partnerId);
				} else {
					return null;
				}
			}
		}
		throw new IllegalArgumentException("Filter for " + searchFilterInput.getField() + " not defined");
	}

	private List<SearchAggregation> mapAggregation(final List<AggregateInput> aggregateInputs) {
		final List<SearchAggregation> searchAggregations = new ArrayList<>();
		for (final AggregateInput aggregateInput : aggregateInputs) {
			boolean mapperFound = false;
			for (final SearchAggregationMapper mapper : this.searchAggregationMappers) {
				if (mapper.field().equals(aggregateInput.getField())) {
					searchAggregations.add(mapper.getSearchAggregation(aggregateInput));
					mapperFound = true;
					break;
				}
			}
			if (!mapperFound) {
				throw new IllegalArgumentException("Aggregation for " + aggregateInput.getField() + " not defined");
			}
		}
		return searchAggregations;
	}

}
