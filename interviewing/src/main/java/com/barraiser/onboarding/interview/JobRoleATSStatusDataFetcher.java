/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.input.GetAtsStatusInput;
import com.barraiser.common.graphql.types.StatusType;
import com.barraiser.common.utilities.ObjectFieldsFilter;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.onboarding.auth.AllowAuthenticatedUserAuthorizationInputConstructor;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.barraiser.onboarding.graphql.NamedDataFetcher.QUERY_TYPE;

@Component
@Log4j2
public class JobRoleATSStatusDataFetcher extends AuthorizedGraphQLQuery<List<StatusType>> {

	private final GraphQLUtil graphQLUtil;
	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;
	private final StatusRepository statusRepository;
	private final StatusMapper statusMapper;
	private final JobRoleRepository jobRoleRepository;

	public JobRoleATSStatusDataFetcher(AuthorizationServiceFeignClient authorizationServiceFeignClient,
			AllowAuthenticatedUserAuthorizationInputConstructor allowAuthenticatedUserAuthorizationInputConstructor,
			ObjectFieldsFilter objectFieldsFilter,
			StatusRepository statusRepository,
			StatusMapper statusMapper,
			JobRoleRepository jobRoleRepository,
			GraphQLUtil graphQLUtil) {
		super(authorizationServiceFeignClient, allowAuthenticatedUserAuthorizationInputConstructor, objectFieldsFilter);
		this.graphQLUtil = graphQLUtil;
		this.statusRepository = statusRepository;
		this.statusMapper = statusMapper;
		this.authorizationServiceFeignClient = authorizationServiceFeignClient;
		this.jobRoleRepository = jobRoleRepository;
	}

	@Override
	protected List<StatusType> fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult) {

		final GetAtsStatusInput input = this.graphQLUtil.getInput(environment, GetAtsStatusInput.class);

		final List<JobRoleDAO> jobRoleDAOS = this.jobRoleRepository.findAllByPartnerId(input.getPartnerId());

		final List<String> statusIds = jobRoleDAOS.stream()
				.map(JobRoleDAO::getAtsStatus)
				.filter(Objects::nonNull)
				.distinct()
				.collect(Collectors.toList());

		return statusIds.stream()
				.map(id -> this.statusMapper.toStatusTypeFromId(id))
				.collect(Collectors.toList());
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of(QUERY_TYPE, "fetchJobRoleATSStatus"));
	}
}
