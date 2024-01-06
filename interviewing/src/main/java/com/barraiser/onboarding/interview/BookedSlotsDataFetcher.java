/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.availability.AvailabilityManager;
import com.barraiser.onboarding.availability.AvailabilityServiceClient;
import com.barraiser.onboarding.availability.DTO.BookedSlotDTO;
import com.barraiser.onboarding.availability.DTO.GetBookedSlotsRequestDTO;
import com.barraiser.onboarding.dal.BookedSlotsDAO;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.common.graphql.input.GetBookedSlotsInput;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class BookedSlotsDataFetcher implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final AvailabilityServiceClient availabilityServiceClient;

	@Override
	public String name() {
		return "getBookedSlotsOfInterviewer";
	}

	@Override
	public String type() {
		return QUERY_TYPE;
	}

	@Override
	public Object get(DataFetchingEnvironment environment) throws Exception {
		final GetBookedSlotsInput input = this.graphQLUtil.getArgument(environment, "input", GetBookedSlotsInput.class);

		final List<BookedSlotDTO> slots = this.availabilityServiceClient.getBookedSlots(
				GetBookedSlotsRequestDTO.builder()
						.userIds(List.of(input.getUserId()))
						.startDate(input.getStartDate())
						.endDate(input.getEndDate())
						.excludeBufferForOverlappingCheck(true)
						.overlappingType(GetBookedSlotsRequestDTO.OverlappingType.PARTIAL)
						.build())
				.get(input.getUserId());

		final List<BookedSlotDTO> slotsWithoutBuffer = slots.stream()
				.map(s -> s.toBuilder()
						.startDate(s.getStartDate() + s.getBuffer())
						.endDate(s.getEndDate() - s.getBuffer())
						.buffer(0L)
						.build())
				.collect(Collectors.toList());

		return DataFetcherResult.newResult()
				.data(slotsWithoutBuffer)
				.build();
	}
}
