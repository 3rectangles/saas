/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.input.PartnerInterviewFeedbackInput;
import com.barraiser.commons.auth.FilterOperator;
import com.barraiser.commons.auth.SearchFilter;
import com.barraiser.onboarding.common.search.db.AggregationResult;
import com.barraiser.onboarding.common.search.db.SearchAggregator;
import com.barraiser.onboarding.common.search.db.SearchDBHelper;
import com.barraiser.onboarding.common.search.db.SearchQuery;
import com.barraiser.onboarding.common.search.db.SearchResult;
import com.barraiser.onboarding.common.search.db.SearchSpecificationBuilder;
import com.barraiser.onboarding.common.search.graphql.types.SearchResultType;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.onboarding.interview.evaluation.search.EvaluationSearchResultTypeMapper;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class GetPendingInterviews implements NamedDataFetcher<DataFetcherResult<Object>> {
	private static final String PARTNER_ID_IS_NOT_AVAILABLE = "Partner Id is not available";
	private static final String PARTNER_ID = "partnerId";
	private static final String IS_PENDING_SCHEDULING = "isPendingScheduling";
	private final InterViewRepository interViewRepository;
	private final GraphQLUtil graphQLUtil;
	private final SearchDBHelper searchDBHelper;
	private final SearchQueryMapperForInterview searchQueryMapperForInterview;
	private final EvaluationSearchResultTypeMapper searchResultTypeMapper;
	private final InterviewMapper interviewMapper;
	private final SearchAggregator searchAggregator;

	@Override
	public String name() {
		return "getPendingInterviews";
	}

	@Override
	public String type() {
		return QUERY_TYPE;
	}

	@Override
	public DataFetcherResult<Object> get(DataFetchingEnvironment environment) throws Exception {
		final PartnerInterviewFeedbackInput input = this.graphQLUtil.getInput(environment,
				PartnerInterviewFeedbackInput.class);
		final String partnerId = input.getPartnerId();
		SearchQuery searchQuery = this.searchQueryMapperForInterview.mapSearchQuery(input.getSearchQuery(), partnerId);
		searchQuery = this.additionalPendingInterviewFilter(searchQuery, partnerId);
		if (partnerId != null) {
			final Page<InterviewDAO> result = this.getPageResult(searchQuery);
			final SearchResult searchResult = SearchResult.builder()
					.aggregationResults(this.getAggregations(searchQuery))
					.pageResult(result.map(Function.identity()))
					.build();
			return DataFetcherResult.newResult().data(this.toSearchResultType(input, searchResult)).build();
		} else {
			throw new IllegalArgumentException(PARTNER_ID_IS_NOT_AVAILABLE);
		}
	}

	private List<AggregationResult> getAggregations(final SearchQuery searchQuery) {
		final List<AggregationResult> result = new ArrayList<>();
		searchQuery.getAggregations().forEach(aggregation -> {
			List<SearchFilter> modifiedSearchFilters = searchQuery.getFilters().stream()
					.filter(x -> !(aggregation.getName().equals(x.getName())))
					.collect(Collectors.toList());
			final SearchQuery modifiedSearchQuery = searchQuery.toBuilder()
					.filters(modifiedSearchFilters)
					.build();
			final Specification<InterviewDAO> specification = SearchSpecificationBuilder
					.getSpecificationForFilters(modifiedSearchQuery.getFilters());
			result.add(this.searchAggregator.aggregateCount(specification, aggregation, InterviewDAO.class));
		});
		return result;
	}

	private Page<InterviewDAO> getPageResult(SearchQuery searchQuery) {
		final Pageable pageRequest = this.searchDBHelper.getPageRequest(searchQuery);
		final Specification<InterviewDAO> specification = SearchSpecificationBuilder
				.getSpecificationForFilters(searchQuery.getFilters());
		return this.interViewRepository.findAll(specification, pageRequest);
	}

	private SearchQuery additionalPendingInterviewFilter(SearchQuery searchQuery, String partnerId) {
		final List<SearchFilter> searchFilters = new ArrayList<>(searchQuery.getFilters());

		searchFilters.add(
				SearchFilter.builder()
						.field(PARTNER_ID)
						.operator(FilterOperator.EQUALS)
						.value(partnerId)
						.build());
		searchFilters.add(
				SearchFilter.builder()
						.field(IS_PENDING_SCHEDULING)
						.operator(FilterOperator.EQUALS)
						.value(true)
						.build());

		return searchQuery.toBuilder().filters(searchFilters).build();
	}

	private SearchResultType toSearchResultType(
			final PartnerInterviewFeedbackInput partnerInterviewFeedbackInput,
			final SearchResult<InterviewDAO> searchFeedbackResult) {
		return SearchResultType.builder()
				.pageNumber(searchFeedbackResult.getPageResult().getNumber())
				.pageSize(searchFeedbackResult.getPageResult().getSize())
				.totalRecords(searchFeedbackResult.getPageResult().getTotalElements())
				.totalPages(searchFeedbackResult.getPageResult().getTotalPages())
				.records(
						searchFeedbackResult
								.getPageResult()
								.getContent().stream()
								.map(this.interviewMapper::toInterview)
								.collect(Collectors.toList()))
				.aggregations(this.searchResultTypeMapper
						.mapAggregateResultToReturn(searchFeedbackResult.getAggregationResults(),
								partnerInterviewFeedbackInput.getPartnerId()))
				.filtersApplied(partnerInterviewFeedbackInput.getSearchQuery().getFilters().stream()
						.map(this.searchResultTypeMapper::toFilterType)
						.collect(Collectors.toList()))
				.build();
	}
}
