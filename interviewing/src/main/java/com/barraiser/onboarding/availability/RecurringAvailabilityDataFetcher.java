/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.availability;

import com.barraiser.common.graphql.input.GetRecurringAvailabilityInput;
import com.barraiser.common.graphql.types.availability.RecurringAvailability;
import com.barraiser.common.graphql.types.availability.RecurringAvailabilitySlot;
import com.barraiser.common.utilities.ObjectFieldsFilter;
import com.barraiser.onboarding.availability.auth.GetRecurringAvailabilityAuthorizer;
import com.barraiser.onboarding.dal.RecurringAvailabilityDAO;
import com.barraiser.onboarding.dal.RecurringAvailabilityRepository;
import com.barraiser.onboarding.graphql.AuthorizationResult;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery_deprecated;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RecurringAvailabilityDataFetcher extends AuthorizedGraphQLQuery_deprecated<RecurringAvailability> {

	private final GraphQLUtil graphQLUtil;
	private final RecurringAvailabilityRepository recurringAvailabilityRepository;

	public RecurringAvailabilityDataFetcher(GetRecurringAvailabilityAuthorizer getRecurringAvailabilityAuthorizer,
			ObjectFieldsFilter<RecurringAvailability> objectFieldsFilter,
			RecurringAvailabilityRepository recurringAvailabilityRepository,
			GraphQLUtil graphQLUtil) {
		super(getRecurringAvailabilityAuthorizer, objectFieldsFilter);
		this.graphQLUtil = graphQLUtil;
		this.recurringAvailabilityRepository = recurringAvailabilityRepository;
	}

	@Override
	protected RecurringAvailability fetch(DataFetchingEnvironment environment,
			AuthorizationResult authorizationResult) {
		final GetRecurringAvailabilityInput input = this.graphQLUtil.getInput(environment,
				GetRecurringAvailabilityInput.class);
		return this.getRecurringAvailabilitiesOfUser(input.getUserId());
	}

	private RecurringAvailability getRecurringAvailabilitiesOfUser(final String userId) {
		final List<RecurringAvailabilityDAO> recurringAvailabilityDAOS = this.recurringAvailabilityRepository
				.findByUserId(userId);
		final List<RecurringAvailabilitySlot> recurringAvailabilitySlots = recurringAvailabilityDAOS.stream()
				.map(x -> this.toRecurringAvailabilitySlot(x)).collect(Collectors.toList());

		return RecurringAvailability.builder()
				.userId(userId)
				.timezone(recurringAvailabilityDAOS.size() != 0 ? recurringAvailabilityDAOS.get(0).getTimezone() : null)
				.slots(recurringAvailabilitySlots)
				.build();
	}

	private RecurringAvailabilitySlot toRecurringAvailabilitySlot(
			final RecurringAvailabilityDAO recurringAvailabilityDAO) {
		return RecurringAvailabilitySlot
				.builder()
				.dayOfTheWeek(recurringAvailabilityDAO.getDayOfTheWeek())
				.startTime(recurringAvailabilityDAO.getSlotStartTime())
				.endTime(recurringAvailabilityDAO.getSlotEndTime())
				.maxInterviewsInSlot(recurringAvailabilityDAO.getMaximumNumberOfInterviewsInSlot())
				.build();
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of("Query", "getRecurringAvailability"));
	}
}
