/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.jobRoleManagement.JobRole;

import com.barraiser.common.graphql.types.JobRole;
import com.barraiser.common.utilities.ObjectFieldsFilter;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.onboarding.auth.AllowAllAuthorizationInputConstructor;
import com.barraiser.onboarding.common.search.db.*;
import com.barraiser.onboarding.common.search.graphql.types.SearchResultType;
import com.barraiser.onboarding.dal.JobRoleDAO;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.jobRoleManagement.JobRole.graphql.input.SearchJobRoleInput;
import com.barraiser.onboarding.jobRoleManagement.JobRole.search.JobRoleSearchResultTypeMapper;
import com.barraiser.onboarding.jobRoleManagement.SearchQueryMapperForJobRole;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.barraiser.onboarding.graphql.NamedDataFetcher.QUERY_TYPE;

@Log4j2
@Component
public class SearchJobRole extends AuthorizedGraphQLQuery<SearchResultType> {

	private final GraphQLUtil graphQLUtil;
	private final JobRoleSearchService jobRoleSearchService;
	private final SearchQueryMapperForJobRole searchQueryMapperForJobRole;
	private final JobRoleSearchResultTypeMapper jobRoleSearchResultTypeMapper;

	public SearchJobRole(AuthorizationServiceFeignClient authorizationServiceFeignClient,
			AllowAllAuthorizationInputConstructor allowAllAuthorizationInputConstructor,
			ObjectFieldsFilter<SearchResultType> objectFieldsFilter,
			GraphQLUtil graphQLUtil,
			JobRoleSearchService jobRoleSearchService,
			JobRoleSearchResultTypeMapper jobRoleSearchResultTypeMapper,
			SearchQueryMapperForJobRole searchQueryMapperForJobRole) {

		super(authorizationServiceFeignClient, allowAllAuthorizationInputConstructor, objectFieldsFilter);
		this.graphQLUtil = graphQLUtil;
		this.jobRoleSearchService = jobRoleSearchService;
		this.jobRoleSearchResultTypeMapper = jobRoleSearchResultTypeMapper;
		this.searchQueryMapperForJobRole = searchQueryMapperForJobRole;
	}

	@Override
	protected SearchResultType fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult) {
		// TODO: Add Authorization and authorization filters
		SearchJobRoleInput searchJobRoleInput = this.graphQLUtil.getInput(environment, SearchJobRoleInput.class);

		// TODO:Remove these default values
		if (searchJobRoleInput.getSearchJobRoleQuery().getPageNumber() == null
				&& searchJobRoleInput.getSearchJobRoleQuery().getPageSize() == null) {
			searchJobRoleInput = searchJobRoleInput.toBuilder()
					.searchJobRoleQuery(
							searchJobRoleInput.getSearchJobRoleQuery().toBuilder()
									.pageNumber(0)
									.pageSize(300)
									.build())
					.build();
		}

		SearchResultType searchResult = SearchResultType.builder().build();
		try {
			searchResult = this.search(searchJobRoleInput);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return searchResult;
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(List.of(QUERY_TYPE, "searchJobRole"));
	}

	private SearchResultType search(SearchJobRoleInput searchJobRoleInput) {
		if ((searchJobRoleInput.getSearchJobRoleQuery().getFilters() == null ||
				searchJobRoleInput.getSearchJobRoleQuery().getFilters().isEmpty()) &&
				(searchJobRoleInput.getSearchJobRoleQuery().getSortBy() == null ||
						searchJobRoleInput.getSearchJobRoleQuery().getSortBy().isEmpty())) {
			searchJobRoleInput = searchJobRoleInput.toBuilder()
					.searchJobRoleQuery(
							this.jobRoleSearchService.getDefaultQueryForPartnershipModel(
									searchJobRoleInput.getSearchJobRoleQuery(), searchJobRoleInput.getPartnerId()))
					.build();
		}
		SearchQuery searchQuery = this.searchQueryMapperForJobRole.mapSearchQuery(
				searchJobRoleInput.getSearchJobRoleQuery(),
				JobRoleDAO.class,
				searchJobRoleInput.getPartnerId());
		if (searchJobRoleInput.getOnlyAllowLatestVersion()) {
			searchQuery = this.jobRoleSearchService.addDeprecatedOnFilter(searchQuery);
		}
		if (searchJobRoleInput.getPartnerId() != null) {
			searchQuery = this.jobRoleSearchService.addPartnerIdFilter(searchQuery, searchJobRoleInput.getPartnerId());
		}
		searchQuery = this.jobRoleSearchService.addSortOnCreatedOn(searchQuery);

		final SearchResult<JobRole> jobRoleResult = this.jobRoleSearchService.findAll(searchQuery);

		return this.jobRoleSearchResultTypeMapper.toSearchResultType(searchJobRoleInput, jobRoleResult,
				this.jobRoleSearchService.getModelForPartner(searchJobRoleInput.getPartnerId()));
	}

}
