/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.availability;

import com.barraiser.common.graphql.types.Calendar;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.availability.DTO.GetCalendarDTO;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class GetConnectedCalendarsDataFetcher implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final AvailabilityServiceClient availabilityServiceClient;

	@Override
	public String name() {
		return "getConnectedCalendars";
	}

	@Override
	public String type() {
		return QUERY_TYPE;
	}

	@Override
	public Object get(DataFetchingEnvironment environment) throws Exception {
		final AuthenticatedUser user = this.graphQLUtil.getLoggedInUser(environment);
		final List<GetCalendarDTO> calendars = this.availabilityServiceClient.getCalendars(user.getUserName());
		return DataFetcherResult.newResult().data(
				calendars.stream().map(
						c -> Calendar.builder()
								.email(c.getEmail())
								.isExpired(c.getIsExpired())
								.provider(c.getOauthProvider())
								.build())
						.collect(Collectors.toList()))
				.build();
	}
}
