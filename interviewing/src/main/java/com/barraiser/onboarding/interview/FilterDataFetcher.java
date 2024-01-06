/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.input.ApplicableFiltersInput;
import com.barraiser.common.graphql.types.ApplicableFilter;
import com.barraiser.common.graphql.types.ApplicableFilterType;
import com.barraiser.common.utilities.ObjectFieldsFilter;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.onboarding.auth.AllowAuthenticatedUserAuthorizationInputConstructor;
import com.barraiser.onboarding.dal.FilterDAO;
import com.barraiser.onboarding.dal.FilterRepository;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.barraiser.onboarding.graphql.NamedDataFetcher.QUERY_TYPE;

@Component
public class FilterDataFetcher extends AuthorizedGraphQLQuery<List<ApplicableFilter>> {

	private final GraphQLUtil graphQLUtil;
	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;
	private final FilterRepository filterRepository;
	private final FilterMapper filterMapper;

	public FilterDataFetcher(AuthorizationServiceFeignClient authorizationServiceFeignClient,
			AllowAuthenticatedUserAuthorizationInputConstructor allowAuthenticatedUserAuthorizationInputConstructor,
			ObjectFieldsFilter objectFieldsFilter,
			FilterRepository filterRepository,
			FilterMapper filterMapper,
			GraphQLUtil graphQLUtil) {
		super(authorizationServiceFeignClient, allowAuthenticatedUserAuthorizationInputConstructor, objectFieldsFilter);
		this.graphQLUtil = graphQLUtil;
		this.authorizationServiceFeignClient = authorizationServiceFeignClient;
		this.filterRepository = filterRepository;
		this.filterMapper = filterMapper;
	}

	@Override
	protected List<ApplicableFilter> fetch(DataFetchingEnvironment environment,
			AuthorizationResult authorizationResult) {
		/* TODO: Add Authorization */
		final ApplicableFiltersInput input = this.graphQLUtil.getInput(environment, ApplicableFiltersInput.class);

		return this.fetchFiltersFromContext(input);
	}

	private List<ApplicableFilter> fetchFiltersFromContext(final ApplicableFiltersInput input) {
		List<FilterDAO> filterDAOS = new ArrayList<>();

		if (input.getType() != null) {
			filterDAOS.addAll(this.filterRepository.findAllByFilterContextAndFilterTypeOrderBySequenceNumber(
					this.filterMapper.getSearchFilterContext(input.getFilterContext()),
					input.getType()));
		} else {
			filterDAOS.addAll(this.filterRepository.findAllByFilterContextAndFilterTypeOrderBySequenceNumber(
					this.filterMapper.getSearchFilterContext(input.getFilterContext()),
					ApplicableFilterType.SEARCH));
			filterDAOS.addAll(this.filterRepository.findAllByFilterContextAndFilterTypeOrderBySequenceNumber(
					this.filterMapper.getSearchFilterContext(input.getFilterContext()),
					ApplicableFilterType.SORT));
		}

		return filterDAOS.stream().map(this.filterMapper::toApplicableFilter).collect(Collectors.toList());
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of(QUERY_TYPE, "fetchApplicableFilters"));
	}
}
