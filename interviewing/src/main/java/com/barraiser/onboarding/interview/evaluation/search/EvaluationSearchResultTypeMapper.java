/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.search;

import com.barraiser.common.graphql.input.FilterType;
import com.barraiser.common.graphql.input.SearchFilterInput;
import com.barraiser.common.graphql.types.Partner;
import com.barraiser.onboarding.common.search.db.AggregationResult;
import com.barraiser.onboarding.common.search.db.SearchResult;
import com.barraiser.onboarding.common.search.graphql.types.AggregateCountResult;
import com.barraiser.onboarding.common.search.graphql.types.AggregateResultType;
import com.barraiser.onboarding.common.search.graphql.types.SearchResultType;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.interview.EvaluationMapper;
import com.barraiser.onboarding.interview.status.EvaluationStatusManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class EvaluationSearchResultTypeMapper {
	private final EvaluationMapper evaluationMapper;
	private final EvaluationStatusManager evaluationStatusManager;

	public SearchResultType toSearchResultType(
			final Partner partner, final SearchResult<EvaluationDAO> evaluationsResult) {
		return SearchResultType.builder()
				.pageNumber(evaluationsResult.getPageResult().getNumber())
				.pageSize(evaluationsResult.getPageResult().getSize())
				.totalRecords(evaluationsResult.getPageResult().getTotalElements())
				.totalPages(evaluationsResult.getPageResult().getTotalPages())
				.records(
						evaluationsResult
								.getPageResult()
								.get()
								.map(this.evaluationMapper::toEvaluation)
								.collect(Collectors.toList()))
				.aggregations(
						this.mapAggregateResultToReturn(
								evaluationsResult.getAggregationResults(), partner.getId()))
				.filtersApplied(
						partner.getEvaluationsSearchQuery().getFilters().stream()
								.map(this::toFilterType)
								.collect(Collectors.toList()))
				.build();
	}

	public FilterType toFilterType(final SearchFilterInput searchFilterInput) {
		return FilterType.builder()
				.field(searchFilterInput.getField())
				.value(searchFilterInput.getValue())
				.build();
	}

	public List<AggregateResultType> mapAggregateResultToReturn(
			final List<AggregationResult> aggregationResults, final String partnerId) {
		if (aggregationResults == null) {
			return Collections.emptyList();
		}
		final List<AggregateResultType> aggregateResultsToReturn = new ArrayList<>();
		aggregationResults.forEach(
				aggregationResult -> {
					if ("status".equals(aggregationResult.getName())) {
						addStatusAggregationResultType(
								partnerId, aggregateResultsToReturn, aggregationResult);
					} else {
						addAggregationResultType(aggregateResultsToReturn, aggregationResult);
					}
				});
		return aggregateResultsToReturn;
	}

	private void addAggregationResultType(
			final List<AggregateResultType> aggregateResultsToReturn,
			final AggregationResult aggregationResult) {
		aggregateResultsToReturn.add(
				AggregateResultType.builder()
						.aggregatedCount(
								aggregationResult.getAggregatedCount().stream()
										.map(
												c -> AggregateCountResult.builder()
														.fieldValue(
																c.getFieldValue() == null
																		? null
																		: c.getFieldValue()
																				.toString())
														.count(c.getCount())
														.build())
										.collect(Collectors.toList()))
						.build());
	}

	private void addStatusAggregationResultType(
			final String partnerId,
			final List<AggregateResultType> aggregateResultsToReturn,
			final AggregationResult aggregationResult) {
		final List<String> displayStatus = this.evaluationStatusManager.getAllDisplayStatusForPartner(partnerId);
		final List<AggregationResult.AggregationCountResult> counts = aggregationResult.getAggregatedCount();
		final List<AggregateCountResult> countsToReturn = new ArrayList<>();
		displayStatus.forEach(
				s -> countsToReturn.add(
						AggregateCountResult.builder()
								.fieldValue(s)
								.count(
										counts.stream()
												.filter(c -> c.getFieldValue().equals(s))
												.map(
														AggregationResult.AggregationCountResult::getCount)
												.reduce(0L, Long::sum))
								.build()));
		aggregateResultsToReturn.add(
				AggregateResultType.builder().aggregatedCount(countsToReturn).build());
	}
}
