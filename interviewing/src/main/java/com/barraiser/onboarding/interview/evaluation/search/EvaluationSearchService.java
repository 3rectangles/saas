/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.search;

import com.barraiser.onboarding.common.search.db.AggregationResult;
import com.barraiser.onboarding.common.search.db.SearchAggregator;
import com.barraiser.onboarding.common.search.db.SearchDBHelper;
import com.barraiser.onboarding.common.search.db.SearchDBService;

import com.barraiser.commons.auth.FilterOperator;
import com.barraiser.commons.auth.SearchFilter;
import com.barraiser.onboarding.common.search.db.SearchQuery;
import com.barraiser.onboarding.common.search.db.SearchResult;
import com.barraiser.onboarding.common.search.db.SearchSpecificationBuilder;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.EvaluationRepository;
import com.barraiser.onboarding.interview.evaluation.search.dal.EvaluationSearchDAO;
import com.barraiser.onboarding.interview.evaluation.search.dal.EvaluationSearchRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class EvaluationSearchService implements SearchDBService<EvaluationDAO> {
	private final EvaluationRepository evaluationRepository;
	private final EvaluationSearchRepository evaluationSearchRepository;
	private final SearchAggregator searchAggregator;
	private final SearchDBHelper searchDBHelper;

	@Override
	public SearchResult<EvaluationDAO> findAll(final SearchQuery searchQuery) {
		final Page<EvaluationSearchDAO> searchedPageResult = this.getPageResult(searchQuery);

		SearchResult<EvaluationDAO> result = SearchResult.<EvaluationDAO>builder()
				.pageResult(this.hydrateSearchedEvaluations(searchedPageResult))
				.build();

		if (searchQuery.getAggregations() != null) {
			result = result.toBuilder()
					.aggregationResults(this.getAggregations(searchQuery))
					.build();
		}

		return result;
	}

	private Page<EvaluationSearchDAO> getPageResult(final SearchQuery searchQuery) {
		final Specification<EvaluationSearchDAO> specification = SearchSpecificationBuilder
				.getSpecificationForFilters(searchQuery.getFilters());
		final Pageable pageRequest = this.searchDBHelper.getPageRequest(searchQuery);
		return this.evaluationSearchRepository.findAll(specification, pageRequest);
	}

	private List<AggregationResult> getAggregations(final SearchQuery searchQuery) {
		final List<AggregationResult> result = new ArrayList<>();
		searchQuery.getAggregations().forEach(aggregation -> {
			final SearchQuery modifiedSearchQuery = searchQuery.toBuilder()
					.filters(
							this.removeSearchFiltersForAggregation(searchQuery.getFilters(), aggregation.getName(),
									0, searchQuery.isNewPortal()))
					.build();
			final Specification<EvaluationSearchDAO> specification = SearchSpecificationBuilder
					.getSpecificationForFilters(modifiedSearchQuery.getFilters());
			result.add(this.searchAggregator.aggregateCount(specification, aggregation, EvaluationSearchDAO.class));
		});

		return result;
	}

	private List<SearchFilter> removeSearchFiltersForAggregation(final List<SearchFilter> searchFilters,
			final String aggregateName,
			final Integer recursionDepth,
			final boolean isNewPortal) {

		if (recursionDepth == 100) {
			throw new RuntimeException("Please check operation. Might cause memory leak.");
		}

		final List<SearchFilter> modifiedSearchFilters = new ArrayList<>(searchFilters);
		for (int i = modifiedSearchFilters.size() - 1; i >= 0; --i) {
			final SearchFilter filter = modifiedSearchFilters.get(i);

			// This is a hack to keep backward compatibility with partner portal
			if (!isNewPortal) {
				if (("status".equals(aggregateName) && filter.getName() != null &&
						List.of("status", "candidateName", "eid").contains(filter.getName())) ||
						("pocEmail".equals(aggregateName) && filter.getName() != null &&
								!List.of("status").contains(filter.getName()))) {
					modifiedSearchFilters.remove(i);
				}
			} else {
				if (filter.getName() != null && filter.getName().equals(aggregateName)) {
					modifiedSearchFilters.remove(i);
				}
			}

			if (modifiedSearchFilters.get(i).getMatchAll() != null) {
				modifiedSearchFilters.get(i).setMatchAll(this.removeSearchFiltersForAggregation(
						modifiedSearchFilters.get(i).getMatchAll(), aggregateName, recursionDepth + 1, isNewPortal));
			}

			if (modifiedSearchFilters.get(i).getMatchAnyOf() != null) {
				modifiedSearchFilters.get(i).setMatchAnyOf(this.removeSearchFiltersForAggregation(
						modifiedSearchFilters.get(i).getMatchAnyOf(), aggregateName, recursionDepth + 1, isNewPortal));
			}
		}
		return modifiedSearchFilters;
	}

	public Page<EvaluationDAO> hydrateSearchedEvaluations(final Page<EvaluationSearchDAO> searchResultPage) {
		final List<String> evaluationIds = searchResultPage.getContent().stream()
				.map(EvaluationSearchDAO::getId).collect(Collectors.toList());
		final List<EvaluationDAO> evaluations = this.evaluationRepository.findAllByIdIn(evaluationIds);
		evaluations.sort(Comparator.comparingInt(e -> evaluationIds.indexOf(e.getId())));
		return searchResultPage.map(
				searchedEvaluation -> evaluations.stream()
						.filter(e -> e.getId().equals(searchedEvaluation.getId())).findFirst().get());
	}
}
