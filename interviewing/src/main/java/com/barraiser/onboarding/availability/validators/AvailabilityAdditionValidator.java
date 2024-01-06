/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.availability.validators;

import com.barraiser.common.enums.DayOfTheWeek;
import com.barraiser.common.graphql.types.ValidationResult;
import com.barraiser.onboarding.availability.*;
import com.barraiser.onboarding.dal.AvailabilityDAO;
import com.barraiser.onboarding.dal.RecurringAvailabilityDAO;
import com.barraiser.onboarding.validation.exception.validator.DataValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class AvailabilityAdditionValidator implements DataValidator<AddAvailabilityInput> {

	private final AvailabilityConsolidator availabilityConsolidator;
	private final RecurringAvailabilityManager recurringAvailabilityManager;

	private static final String OVERALL_ERROR_1 = "StarTime greater than end time for slot.";
	private static final String OVERALL_ERROR_2 = "Availabilities provided overlap with those provided in the past. Kindy verify and submit.";

	@Override
	public ValidationResult validate(AddAvailabilityInput input) {
		final ValidationResult validationResult = new ValidationResult();

		this.performBasicInputSanity(input, validationResult);
		this.checkIfRecurringSlotsAndCustomSlotsOverlap(input, validationResult);

		return validationResult;
	}

	public void performBasicInputSanity(final AddAvailabilityInput input,
			final ValidationResult validationResult) {

		final List<String> overallErrors = new ArrayList<>();

		for (AvailabilityInput s : input.getAvailabilities()) {
			if (s.getStartDate() >= s.getEndDate()) {
				overallErrors.add(OVERALL_ERROR_1);
				break;
			}
		}

		validationResult.setOverallErrors(overallErrors);
	}

	public void checkIfRecurringSlotsAndCustomSlotsOverlap(final AddAvailabilityInput input,
			final ValidationResult validationResult) {

		final List<String> overallErrors = new ArrayList<>();
		final Map<DayOfTheWeek, List<RecurringAvailabilityDAO>> daywiseRecurringAvailability = this.recurringAvailabilityManager
				.getDaywiseRecurringAvailabilities(input.getUserId());

		// Considering a 1 year window to verify overlap with custom slots.
		final Long windowStart = Instant.now().getEpochSecond();
		final Long windowEnd = LocalDateTime.now().plusYears(1)
				.atZone(ZoneId.of(this.recurringAvailabilityManager
						.getTimezoneOfRecurringAvailability(daywiseRecurringAvailability)))
				.toEpochSecond();

		final List<AvailabilityDAO> customAvailabilities = input.getAvailabilities()
				.stream()
				.map(s -> AvailabilityDAO.builder()
						.userId(input.getUserId())
						.maximumNumberOfInterviews(s.getMaximumNumberOfInterviews())
						.startDate(s.getStartDate())
						.endDate(s.getEndDate())
						.build())
				.collect(Collectors.toList());

		Boolean areSlotsOverlapping = this.availabilityConsolidator.checkIfAvailabilitiesOverlapping(
				customAvailabilities, daywiseRecurringAvailability, windowStart, windowEnd);
		if (Boolean.TRUE.equals(areSlotsOverlapping)) {
			overallErrors.add(OVERALL_ERROR_2);
		}

		validationResult.getOverallErrors().addAll(overallErrors);
	}

	@Override
	public String type() {
		return null;
	}

}
