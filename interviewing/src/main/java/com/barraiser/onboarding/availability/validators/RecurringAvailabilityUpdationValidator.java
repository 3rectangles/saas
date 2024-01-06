/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.availability.validators;

import com.barraiser.common.enums.DayOfTheWeek;
import com.barraiser.common.graphql.input.availability.UpdateRecurringAvailabilityInput;
import com.barraiser.common.graphql.types.ValidationResult;
import com.barraiser.common.graphql.types.availability.RecurringAvailabilitySlot;
import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.availability.AvailabilityConsolidator;
import com.barraiser.onboarding.availability.AvailabilityManager;
import com.barraiser.onboarding.dal.AvailabilityDAO;
import com.barraiser.onboarding.dal.RecurringAvailabilityDAO;
import com.barraiser.onboarding.validation.exception.validator.DataValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class RecurringAvailabilityUpdationValidator implements DataValidator<UpdateRecurringAvailabilityInput> {

	private static final String OVERALL_ERROR_1 = "StarTime greater than end time";

	@Override
	public ValidationResult validate(UpdateRecurringAvailabilityInput input) {
		final ValidationResult validationResult = new ValidationResult();

		this.performBasicInputSanity(input, validationResult);
		this.checkIfRecurringSlotsAreOverlapping(input, validationResult);

		return validationResult;
	}

	public void performBasicInputSanity(final UpdateRecurringAvailabilityInput input,
			final ValidationResult validationResult) {

		final List<String> overallErrors = new ArrayList<>();

		for (RecurringAvailabilitySlot s : input.getSlots()) {
			if (s.getStartTime() >= s.getEndTime()) {
				overallErrors.add(OVERALL_ERROR_1);
				break;
			}
		}

		validationResult.setOverallErrors(overallErrors);
	}

	public void checkIfRecurringSlotsAreOverlapping(final UpdateRecurringAvailabilityInput input,
			final ValidationResult validationResult) {
		final List<String> overallErrors = new ArrayList<>();

		final Map<DayOfTheWeek, List<RecurringAvailabilitySlot>> slotsByDay = input.getSlots().stream()
				.collect(Collectors.groupingBy(RecurringAvailabilitySlot::getDayOfTheWeek));

		slotsByDay.keySet().forEach(x -> {
			final List<RecurringAvailabilitySlot> slots = slotsByDay.get(x);
			slots.sort(Comparator.comparingInt(RecurringAvailabilitySlot::getStartTime));

			for (int i = 0; i < slots.size() - 1; i++) {
				if (slots.get(i).getEndTime() > slots.get(i + 1).getStartTime()) {
					overallErrors.add(
							"Overlapping slots found. Please configure interviewing hours such they do not overlap");
				}
			}
		});

		validationResult.getOverallErrors().addAll(overallErrors);
	}

	@Override
	public String type() {
		return null;
	}

}
