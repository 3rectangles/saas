/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.barraiser.common.utilities.ObjectFieldsFilter;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.onboarding.auth.AllowAuthenticatedUserAuthorizationInputConstructor;
import com.barraiser.onboarding.dal.RelaxedMeetingInterceptionConfigDAO;
import com.barraiser.onboarding.dal.RelaxedMeetingInterceptionConfigRepository;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery;

import static com.barraiser.onboarding.graphql.NamedDataFetcher.QUERY_TYPE;

import graphql.schema.DataFetchingEnvironment;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class FetchMeetingInterceptionConfiguration extends AuthorizedGraphQLQuery<MeetingInterceptionConfiguration> {

	final RelaxedMeetingInterceptionConfigRepository relaxedMeetingInterceptionConfigRepository;

	public FetchMeetingInterceptionConfiguration(AuthorizationServiceFeignClient authorizationServiceFeignClient,
			AllowAuthenticatedUserAuthorizationInputConstructor allowAuthenticatedUserAuthorizationInputConstructor,
			ObjectFieldsFilter objectFieldsFilter,
			RelaxedMeetingInterceptionConfigRepository relaxedMeetingInterceptionConfigRepository) {
		super(authorizationServiceFeignClient, allowAuthenticatedUserAuthorizationInputConstructor, objectFieldsFilter);
		this.relaxedMeetingInterceptionConfigRepository = relaxedMeetingInterceptionConfigRepository;

	}

	@Override
	protected MeetingInterceptionConfiguration fetch(final DataFetchingEnvironment environment,
			final AuthorizationResult authorizationResult)
			throws IOException {
		final String partnerId = environment.getArgument("partnerId");
		RelaxedMeetingInterceptionConfigDAO relaxedMeetingInterceptionConfigDAO = this.relaxedMeetingInterceptionConfigRepository
				.findByPartnerId(partnerId).orElse(null);
		/*
		 * If the given partnerId is not there in relaxed_meeting_interception_config
		 * table then an empty
		 * ArrayList is returned
		 */
		if (relaxedMeetingInterceptionConfigDAO == null) {

			return MeetingInterceptionConfiguration.builder().build();
		}

		return relaxedMeetingInterceptionConfigDAO.getKeyword() == null
				? MeetingInterceptionConfiguration.builder().build()
				: MeetingInterceptionConfiguration.builder().keyword(relaxedMeetingInterceptionConfigDAO.getKeyword())
						.build();
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(List.of(QUERY_TYPE, "fetchInterceptionConfiguration"));
	}

}
