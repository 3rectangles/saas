/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.types.Slot;
import com.barraiser.onboarding.availability.AvailabilityManager;
import com.barraiser.onboarding.availability.SlotMapper;
import com.barraiser.onboarding.availability.enums.SlotType;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.common.graphql.input.GetAvailabilityInput;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class GetAvailability implements NamedDataFetcher {
	private final AvailabilityManager availabilityManager;
	private final GraphQLUtil graphQLUtil;
	private final SlotMapper slotMapper;

	@Override
	public Object get(final DataFetchingEnvironment environment) {
		final GetAvailabilityInput input = this.graphQLUtil.getArgument(environment, "input",
				GetAvailabilityInput.class);

		final List<Slot> availabilities = new ArrayList<>();

		this.availabilityManager
				.getCustomAvailabilitiesForUser(input.getUserId(), input.getStartDate(), input.getEndDate(),
						input.isBreakIntoSlots() ? 60L : -1L)
				.stream()
				.map(s -> this.slotMapper.toSlot(s).toBuilder().type(SlotType.CUSTOM.toString()).build())
				.collect(Collectors.toCollection(() -> availabilities));

		this.availabilityManager
				.getExtrapolatedRecurringAvailabilitiesForUser(input.getUserId(), input.getStartDate(),
						input.getEndDate(), input.isBreakIntoSlots() ? 60L : -1L)
				.stream()
				.map(s -> this.slotMapper.toSlot(s).toBuilder().type(SlotType.RECURRING.toString()).build())
				.collect(Collectors.toCollection(() -> availabilities));

		return availabilities;
	}

	@Override
	public String name() {
		return "getAvailability";
	}

	@Override
	public String type() {
		return QUERY_TYPE;
	}
}
