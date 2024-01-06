/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.availability;

import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.availabilitychangeevent.AvailabilityChangeEvent;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.availabilitychangeevent.SlotEvent;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.events.InterviewingEventProducer;
import com.barraiser.onboarding.graphql.GraphQLMutation;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.commons.auth.UserRole;
import com.barraiser.onboarding.validation.exception.validator.Validator;
import com.fasterxml.jackson.databind.ObjectMapper;

import graphql.schema.DataFetchingEnvironment;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class AddAvailabilityMutation implements GraphQLMutation<Boolean> {
	private final ObjectMapper objectMapper;
	private final AvailabilityManager availabilityManager;
	private final GraphQLUtil graphQLUtil;
	private final InterviewingEventProducer eventProducer;
	private final Validator validator;

	@Override
	public String name() {
		return "addAvailability";
	}

	@Override
	@Transactional
	public Boolean get(final DataFetchingEnvironment environment) throws Exception {
		final AddAvailabilityInput input = this.graphQLUtil.getInput(environment, AddAvailabilityInput.class);

		final AuthenticatedUser user = this.graphQLUtil.getLoggedInUser(environment);

		final String userId;
		if (input.getUserId() != null
				&& (user.getRoles().contains(UserRole.OPS)
						|| user.getRoles().contains(UserRole.ADMIN))) {
			userId = input.getUserId();
		} else {
			userId = user.getUserName();
		}

		this.validator.validate(input);

		input.getAvailabilities()
				.forEach(
						x -> this.availabilityManager.addASlot(
								userId,
								x.getStartDate(),
								x.getEndDate(),
								x.getMaximumNumberOfInterviews()));

		this.sendAddAvailabilityEvent(input.toBuilder().userId(userId).build());
		return true;
	}

	private void sendAddAvailabilityEvent(final AddAvailabilityInput input) throws Exception {
		final Event<AvailabilityChangeEvent> event = new Event<>();
		event.setPayload(
				new AvailabilityChangeEvent()
						.userId(input.getUserId())
						.operation("ADD")
						.slots(
								input.getAvailabilities().stream()
										.map(
												x -> this.objectMapper.convertValue(
														x, SlotEvent.class))
										.collect(Collectors.toList())));
		this.eventProducer.pushEvent(event);
	}
}
