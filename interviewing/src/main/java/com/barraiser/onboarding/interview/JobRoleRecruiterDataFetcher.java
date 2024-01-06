/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.input.GetHiringManagersInput;
import com.barraiser.common.graphql.types.PartnerRepDetails;
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

import java.util.List;
import java.util.stream.Collectors;

import static com.barraiser.onboarding.graphql.NamedDataFetcher.QUERY_TYPE;

@Component
@Log4j2
public class JobRoleRecruiterDataFetcher extends AuthorizedGraphQLQuery<List<PartnerRepDetails>> {
	private final GraphQLUtil graphQLUtil;
	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;
	private final PartnerRepsMapper partnerRepsMapper;
	private final PartnerRepsRepository partnerRepsRepository;

	public JobRoleRecruiterDataFetcher(AuthorizationServiceFeignClient authorizationServiceFeignClient,
			AllowAuthenticatedUserAuthorizationInputConstructor allowAuthenticatedUserAuthorizationInputConstructor,
			ObjectFieldsFilter objectFieldsFilter,
			GraphQLUtil graphQLUtil,
			PartnerRepsMapper partnerRepsMapper,
			PartnerRepsRepository partnerRepsRepository) {
		super(authorizationServiceFeignClient, allowAuthenticatedUserAuthorizationInputConstructor,
				objectFieldsFilter);
		this.graphQLUtil = graphQLUtil;
		this.authorizationServiceFeignClient = authorizationServiceFeignClient;
		this.partnerRepsMapper = partnerRepsMapper;
		this.partnerRepsRepository = partnerRepsRepository;
	}

	@Override
	protected List<PartnerRepDetails> fetch(DataFetchingEnvironment environment,
			AuthorizationResult authorizationResult) {
		final GetHiringManagersInput input = this.graphQLUtil.getInput(environment, GetHiringManagersInput.class);

		final List<PartnerRepsDAO> partnerRepsDAOS = this.partnerRepsRepository
				.findAllByPartnerId(input.getPartnerId());

		// TODO: Add Filter to filter out hiring managers based on role
		return partnerRepsDAOS.stream()
				.map(this.partnerRepsMapper::toPartnerRepDetails)
				.collect(Collectors.toList());
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of(QUERY_TYPE, "fetchRecruiters"));
	}
}
