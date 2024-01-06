/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.search;

import com.barraiser.common.graphql.types.Partner;
import com.barraiser.common.utilities.ObjectFieldsFilter;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.FilterOperator;
import com.barraiser.commons.auth.SearchFilter;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.onboarding.common.search.db.SearchQuery;
import com.barraiser.onboarding.common.search.db.SearchQueryMapper;
import com.barraiser.onboarding.common.search.db.SearchResult;
import com.barraiser.onboarding.common.search.graphql.types.SearchResultType;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.interview.PartnerConfigManager;
import com.barraiser.onboarding.interview.evaluation.search.auth.SearchEvaluationAuthorizationInputConstructor;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SearchEvaluationDataFetcher extends AuthorizedGraphQLQuery<SearchResultType> {
	private static final String TYPE_PARTNER = "Partner";

	private final GraphQLUtil graphQLUtil;
	private final EvaluationSearchService evaluationSearchService;
	private final SearchQueryMapper searchQueryMapper;
	private final EvaluationSearchResultTypeMapper searchResultTypeMapper;
	private final PartnerConfigManager partnerConfigManager;

	public SearchEvaluationDataFetcher(AuthorizationServiceFeignClient authorizationServiceFeignClient,
			SearchEvaluationAuthorizationInputConstructor searchEvaluationAuthorizationInputConstructor,
			ObjectFieldsFilter<SearchResultType> objectFieldsFilter,
			GraphQLUtil graphQLUtil,
			EvaluationSearchService evaluationSearchService,
			SearchQueryMapper searchQueryMapper,
			EvaluationSearchResultTypeMapper searchResultTypeMapper, PartnerConfigManager partnerConfigManager) {

		super(authorizationServiceFeignClient, searchEvaluationAuthorizationInputConstructor, objectFieldsFilter);
		this.graphQLUtil = graphQLUtil;
		this.evaluationSearchService = evaluationSearchService;
		this.searchQueryMapper = searchQueryMapper;
		this.searchResultTypeMapper = searchResultTypeMapper;
		this.partnerConfigManager = partnerConfigManager;
	}

	@Override
	protected SearchResultType fetch(DataFetchingEnvironment environment,
			com.barraiser.commons.auth.AuthorizationResult authorizationResult) {
		final Partner partner = environment.getSource();

		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);
		final SearchQuery searchQuery = this.createSearchQuery(partner, authorizationResult);
		final SearchResult<EvaluationDAO> evaluationsResult = this.evaluationSearchService.findAll(searchQuery);

		final SearchResultType searchResult = this.searchResultTypeMapper.toSearchResultType(partner,
				evaluationsResult);
		return searchResult;
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(List.of(TYPE_PARTNER, "searchedEvaluations"));
	}

	private SearchQuery createSearchQuery(
			final Partner partner,
			final com.barraiser.commons.auth.AuthorizationResult authorizationResult) {
		SearchQuery searchQuery = this.searchQueryMapper.mapSearchQuery(partner.getEvaluationsSearchQuery(),
				partner.getId());
		searchQuery = this.addFilterForPartnerIdAndDeletedOnIsNull(searchQuery, partner.getCompanyId());

		if (authorizationResult.getAuthorizationFilter() != null) {
			searchQuery = this.addAuthorizationFilters(searchQuery, authorizationResult.getAuthorizationFilter());
		}
		return searchQuery;
	}

	private SearchQuery addFilterForPartnerIdAndDeletedOnIsNull(
			final SearchQuery searchQuery, final String companyId) {
		final List<com.barraiser.commons.auth.SearchFilter> searchFilters = new ArrayList<>(searchQuery.getFilters());
		final String partnerId = this.partnerConfigManager.getPartnerIdFromCompanyId(companyId);

		searchFilters.add(
				SearchFilter.builder()
						.field("partnerId")
						.operator(FilterOperator.EQUALS)
						.value(partnerId)
						.build());
		searchFilters.add(
				SearchFilter.builder().field("deletedOn").operator(com.barraiser.commons.auth.FilterOperator.IS_NULL)
						.build());

		return searchQuery.toBuilder().filters(searchFilters).build();
	}

	private SearchQuery addAuthorizationFilters(final SearchQuery searchQuery,
			final SearchFilter authorizationFilter) {

		final List<SearchFilter> combinedSearchFilters = new ArrayList<>();
		combinedSearchFilters.addAll(searchQuery.getFilters());
		combinedSearchFilters.add(authorizationFilter);

		final SearchFilter combinedSearchFilter = SearchFilter.builder()
				.matchAll(combinedSearchFilters)
				.build();

		return searchQuery.toBuilder().filters(List.of(combinedSearchFilter)).build();
	}
}
