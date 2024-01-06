/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.availability;

import com.barraiser.common.enums.DayOfTheWeek;
import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.dal.RecurringAvailabilityDAO;
import com.barraiser.onboarding.dal.RecurringAvailabilityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Component
public class RecurringAvailabilityManager {
	// TBD : Timezone support to be added
	private final RecurringAvailabilityRepository recurringAvailabilityRepository;

	public List<RecurringAvailabilityDAO> getRecurringAvailabilitiesForUser(final String userId) {
		return this.recurringAvailabilityRepository.findByUserId(userId);
	}

	/**
	 * @param userId
	 * @return
	 */
	public Map<DayOfTheWeek, List<RecurringAvailabilityDAO>> getDaywiseRecurringAvailabilities(final String userId) {
		final Map<DayOfTheWeek, List<RecurringAvailabilityDAO>> daywiseUnmergedAvailabilitiesMap = new HashMap<>();

		List<RecurringAvailabilityDAO> recurringAvailabilities = this.getRecurringAvailabilitiesForUser(userId);

		for (RecurringAvailabilityDAO recurringAvailability : recurringAvailabilities) {
			final List<RecurringAvailabilityDAO> recurringAvailabilitiesForADay = daywiseUnmergedAvailabilitiesMap
					.computeIfAbsent(recurringAvailability.getDayOfTheWeek(), x -> new ArrayList<>());
			recurringAvailabilitiesForADay.add(recurringAvailability);
		}

		return daywiseUnmergedAvailabilitiesMap;
	}

	public String getTimezoneOfRecurringAvailabilityForUser(final String userId) {
		final List<RecurringAvailabilityDAO> recurringAvailabilities = this.recurringAvailabilityRepository
				.findByUserId(userId);
		return recurringAvailabilities.size() != 0 ? recurringAvailabilities.get(0).getTimezone()
				: DateUtils.TIMEZONE_ASIA_KOLKATA;
	}

	public String getTimezoneOfRecurringAvailability(
			final Map<DayOfTheWeek, List<RecurringAvailabilityDAO>> daywiseRecurringAvailabilities) {
		String timezone = DateUtils.TIMEZONE_ASIA_KOLKATA;

		if (daywiseRecurringAvailabilities.size() != 0) {
			for (Map.Entry<DayOfTheWeek, List<RecurringAvailabilityDAO>> recurringAvailabilityForADay : daywiseRecurringAvailabilities
					.entrySet()) {
				if (recurringAvailabilityForADay.getValue().size() != 0) {
					timezone = recurringAvailabilityForADay.getValue().get(0).getTimezone();
					break;
				}
			}
		}

		return timezone;
	}

}
