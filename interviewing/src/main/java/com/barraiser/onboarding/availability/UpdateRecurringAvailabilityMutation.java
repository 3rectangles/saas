/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.availability;

import com.barraiser.common.enums.DayOfTheWeek;
import com.barraiser.common.graphql.input.availability.UpdateRecurringAvailabilityInput;
import com.barraiser.common.graphql.types.availability.RecurringAvailabilitySlot;
import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.dal.RecurringAvailabilityDAO;
import com.barraiser.onboarding.dal.RecurringAvailabilityRepository;
import com.barraiser.onboarding.graphql.AuthorizationResult;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLMutation_deprecated;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.validation.exception.validator.Validator;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@Component
public class UpdateRecurringAvailabilityMutation extends AuthorizedGraphQLMutation_deprecated<Boolean> {

	private final AvailabilityManager availabilityManager;
	private final RecurringAvailabilityRepository recurringAvailabilityRepository;
	private final GraphQLUtil graphQLUtil;
	private final Validator validator;

	@Override
	public String name() {
		return "updateRecurringAvailability";
	}

	public UpdateRecurringAvailabilityMutation(final UpdateRecurringAvailabilityAuthorizer authorizer,
			RecurringAvailabilityRepository recurringAvailabilityRepository,
			AvailabilityManager availabilityManager,
			AvailabilityConsolidator availabilityConsolidator,
			Validator validator,
			GraphQLUtil graphQLUtil) {
		super(authorizer);
		this.recurringAvailabilityRepository = recurringAvailabilityRepository;
		this.graphQLUtil = graphQLUtil;
		this.availabilityManager = availabilityManager;
		this.validator = validator;
	}

	@Transactional
	@Override
	protected Boolean fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult) {
		final UpdateRecurringAvailabilityInput input = this.graphQLUtil.getInput(environment,
				UpdateRecurringAvailabilityInput.class);

		this.validator.validate(input);

		this.deleteOverlappingCustomAvailability(input);

		this.updateRecurringAvailability(input);

		return Boolean.TRUE;
	}

	private void deleteOverlappingCustomAvailability(final UpdateRecurringAvailabilityInput input) {

		final Map<DayOfTheWeek, List<RecurringAvailabilityDAO>> daywiseRecurringAvailability = this
				.getDaywiseRecurringSlots(input);

		final Long windowStart = Instant.now().getEpochSecond();
		final Long windowEnd = LocalDateTime.now().plusYears(1).atZone(ZoneId.of(DateUtils.TIMEZONE_ASIA_KOLKATA))
				.toEpochSecond();

		this.availabilityManager.deleteOverlappingCustomAvailabilities(
				input.getUserId(),
				daywiseRecurringAvailability, windowStart, windowEnd);
	}

	private void updateRecurringAvailability(final UpdateRecurringAvailabilityInput input) {

		if (input.getSlots() != null) {
			this.deleteExistingRecurringAvailability(input.getUserId());
			this.saveNewRecurringAvailability(input);
		}
	}

	private void deleteExistingRecurringAvailability(final String userId) {
		this.recurringAvailabilityRepository.deleteByUserId(userId);
	}

	private void saveNewRecurringAvailability(final UpdateRecurringAvailabilityInput input) {
		final List<RecurringAvailabilityDAO> availabilityDAOList = input.getSlots().stream().map(
				x -> this.toRecurringAvailabilityDAO(input.getUserId(), input.getTimezone(),
						x))
				.collect(Collectors.toList());

		this.recurringAvailabilityRepository.saveAll(availabilityDAOList);
	}

	private RecurringAvailabilityDAO toRecurringAvailabilityDAO(final String userId,
			final String timezone, final RecurringAvailabilitySlot recurringAvailabilitySlot) {
		return RecurringAvailabilityDAO.builder()
				.id(UUID.randomUUID().toString())
				.userId(userId)
				.dayOfTheWeek(recurringAvailabilitySlot.getDayOfTheWeek())
				.slotStartTime(recurringAvailabilitySlot.getStartTime())
				.slotEndTime(recurringAvailabilitySlot.getEndTime())
				.timezone(timezone)
				.maximumNumberOfInterviewsInSlot(recurringAvailabilitySlot.getMaxInterviewsInSlot())
				.isAvailable(recurringAvailabilitySlot.getStartTime() == null
						&& recurringAvailabilitySlot.getEndTime() == null ? Boolean.FALSE : Boolean.TRUE)
				.build();
	}

	private Map<DayOfTheWeek, List<RecurringAvailabilityDAO>> getDaywiseRecurringSlots(
			UpdateRecurringAvailabilityInput input) {
		return input.getSlots()
				.stream()
				.map(s -> RecurringAvailabilityDAO.builder()
						.userId(input.getUserId())
						.timezone(input.getTimezone())
						.dayOfTheWeek(s.getDayOfTheWeek())
						.slotStartTime(s.getStartTime())
						.slotEndTime(s.getEndTime())
						.maximumNumberOfInterviewsInSlot(s.getMaxInterviewsInSlot())
						.build())
				.collect(Collectors.groupingBy(RecurringAvailabilityDAO::getDayOfTheWeek));
	}

}
