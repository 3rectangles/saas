/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.input.PartnerInterviewFeedbackInput;
import com.barraiser.common.graphql.types.CandidateInterviewFeedback;
import com.barraiser.onboarding.common.search.db.SearchDBHelper;
import com.barraiser.onboarding.common.search.db.SearchQuery;
import com.barraiser.onboarding.common.search.db.SearchQueryMapper;
import com.barraiser.onboarding.common.search.db.SearchResult;
import com.barraiser.onboarding.common.search.graphql.types.SearchResultType;
import com.barraiser.onboarding.dal.IntervieweeFeedbackDAO;
import com.barraiser.onboarding.dal.IntervieweeFeedbackRepository;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.onboarding.interview.evaluation.search.EvaluationSearchResultTypeMapper;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class GetPartnerInterviewFeedbacks implements NamedDataFetcher<DataFetcherResult<Object>> {
	public static final String PARTNER_ID_IS_NOT_AVAILABLE = "Partner Id is not available";
	private final IntervieweeFeedbackRepository intervieweeFeedbackRepository;
	private final GraphQLUtil graphQLUtil;
	private final SearchDBHelper searchDBHelper;
	private final SearchQueryMapper searchQueryMapper;
	private final EvaluationSearchResultTypeMapper searchResultTypeMapper;

	@Override
	public String name() {
		return "getPartnerInterviewFeedbacks";
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
		final SearchQuery searchQuery = this.searchQueryMapper.mapSearchQuery(input.getSearchQuery(), partnerId);

		if (partnerId != null) {
			final Page<IntervieweeFeedbackDAO> result = this.getPageResult(partnerId, searchQuery);
			final SearchResult searchResult = SearchResult.builder()
					.pageResult(result.map(Function.identity()))
					.build();
			return DataFetcherResult.newResult().data(toSearchResultType(input, searchResult)).build();
		} else {
			throw new IllegalArgumentException(PARTNER_ID_IS_NOT_AVAILABLE);
		}
	}

	private Page<IntervieweeFeedbackDAO> getPageResult(final String partnerId, final SearchQuery searchQuery) {
		final Pageable pageRequest = this.searchDBHelper.getPageRequest(searchQuery);
		return this.intervieweeFeedbackRepository
				.findAllByPartnerId(partnerId, pageRequest);
	}

	private SearchResultType toSearchResultType(
			final PartnerInterviewFeedbackInput partnerInterviewFeedbackInput,
			final SearchResult<IntervieweeFeedbackDAO> searchFeedbackResult) {
		return SearchResultType.builder()
				.pageNumber(searchFeedbackResult.getPageResult().getNumber())
				.pageSize(searchFeedbackResult.getPageResult().getSize())
				.totalRecords(searchFeedbackResult.getPageResult().getTotalElements())
				.totalPages(searchFeedbackResult.getPageResult().getTotalPages())
				.records(
						searchFeedbackResult
								.getPageResult()
								.getContent().stream()
								.map(this::mapIntervieweeFeedbackDAOtoCandidateInterviewFeedback)
								.collect(Collectors.toList()))
				.aggregations(searchResultTypeMapper
						.mapAggregateResultToReturn(searchFeedbackResult.getAggregationResults(),
								partnerInterviewFeedbackInput.getPartnerId()))
				.filtersApplied(partnerInterviewFeedbackInput.getSearchQuery().getFilters().stream()
						.map(this.searchResultTypeMapper::toFilterType)
						.collect(Collectors.toList()))
				.build();
	}

	private CandidateInterviewFeedback mapIntervieweeFeedbackDAOtoCandidateInterviewFeedback(
			IntervieweeFeedbackDAO intervieweeFeedbackDAO) {
		return CandidateInterviewFeedback.builder()
				.interviewId(intervieweeFeedbackDAO.getInterviewId())
				.overallRating(intervieweeFeedbackDAO.getAverageRating())
				.comment(intervieweeFeedbackDAO.getAnyOtherFeedback())
				.build();
	}
}
