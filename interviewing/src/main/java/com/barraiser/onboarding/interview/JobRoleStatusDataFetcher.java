/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.types.StatusType;
import com.barraiser.common.utilities.ObjectFieldsFilter;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.onboarding.auth.AllowAuthenticatedUserAuthorizationInputConstructor;
import com.barraiser.onboarding.dal.StatusDAO;
import com.barraiser.onboarding.dal.StatusMapper;
import com.barraiser.onboarding.dal.StatusRepository;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static com.barraiser.onboarding.graphql.NamedDataFetcher.QUERY_TYPE;

@Component
@Log4j2
public class JobRoleStatusDataFetcher extends AuthorizedGraphQLQuery<List<StatusType>> {
	private final GraphQLUtil graphQLUtil;
	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;
	private final StatusRepository statusRepository;
	private final StatusMapper statusMapper;

	public JobRoleStatusDataFetcher(AuthorizationServiceFeignClient authorizationServiceFeignClient,
			AllowAuthenticatedUserAuthorizationInputConstructor allowAuthenticatedUserAuthorizationInputConstructor,
			ObjectFieldsFilter objectFieldsFilter,
			StatusRepository statusRepository,
			StatusMapper statusMapper,
			GraphQLUtil graphQLUtil) {
		super(authorizationServiceFeignClient, allowAuthenticatedUserAuthorizationInputConstructor, objectFieldsFilter);
		this.graphQLUtil = graphQLUtil;
		this.authorizationServiceFeignClient = authorizationServiceFeignClient;
		this.statusMapper = statusMapper;
		this.statusRepository = statusRepository;
	}

	@Override
	protected List<StatusType> fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult) {
		final List<StatusDAO> statusDAOS = this.statusRepository.findAllByEntityType("JOB_ROLE");

		return statusDAOS.stream().filter(s -> this.statusMapper.matchContext(s, "iaas", "brStatus"))
				.map(this.statusMapper::toStatusType).collect(Collectors.toList());
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of(QUERY_TYPE, "getJobRoleStatuses"));
	}
}
