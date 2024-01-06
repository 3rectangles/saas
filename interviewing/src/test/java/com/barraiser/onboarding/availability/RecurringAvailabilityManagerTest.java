/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.availability;

import com.barraiser.common.enums.DayOfTheWeek;
import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.dal.RecurringAvailabilityDAO;
import com.barraiser.onboarding.dal.RecurringAvailabilityRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RecurringAvailabilityManagerTest {

	@Mock
	private RecurringAvailabilityRepository recurringAvailabilityRepository;

	@InjectMocks
	private RecurringAvailabilityManager recurringAvailabilityManager;

	@Test
	public void shouldReturnNoDaywiseRecurringAvailability() {
		when(this.recurringAvailabilityRepository.findByUserId(any())).thenReturn(List.of());

		final Map<DayOfTheWeek, List<RecurringAvailabilityDAO>> daywiseRecurringAvailability = this.recurringAvailabilityManager
				.getDaywiseRecurringAvailabilities("test_user_id");
		Assert.assertEquals(0, daywiseRecurringAvailability.size());
	}

	@Test
	public void shouldReturnDaywiseRecurringAvailabilities() {

		when(this.recurringAvailabilityRepository.findByUserId(any()))
				.thenReturn(
						List.of(
								RecurringAvailabilityDAO.builder().dayOfTheWeek(DayOfTheWeek.SAT).slotStartTime(32400)
										.slotEndTime(61200).build(),
								RecurringAvailabilityDAO.builder().dayOfTheWeek(DayOfTheWeek.SAT).slotStartTime(61200)
										.slotEndTime(68400).build(),
								RecurringAvailabilityDAO.builder().dayOfTheWeek(DayOfTheWeek.SAT).slotStartTime(68400)
										.slotEndTime(72000).build(),
								RecurringAvailabilityDAO.builder().dayOfTheWeek(DayOfTheWeek.SUN).slotStartTime(32400)
										.slotEndTime(61200).build(),
								RecurringAvailabilityDAO.builder().dayOfTheWeek(DayOfTheWeek.MON).slotStartTime(32400)
										.slotEndTime(61200).build(),
								RecurringAvailabilityDAO.builder().dayOfTheWeek(DayOfTheWeek.TUE).slotStartTime(32400)
										.slotEndTime(61200).build(),
								RecurringAvailabilityDAO.builder().dayOfTheWeek(DayOfTheWeek.WED).slotStartTime(32400)
										.slotEndTime(61200).build(),
								RecurringAvailabilityDAO.builder().dayOfTheWeek(DayOfTheWeek.FRI).slotStartTime(32400)
										.slotEndTime(61200).build()));

		final Map<DayOfTheWeek, List<RecurringAvailabilityDAO>> daywiseRecurringAvailability = this.recurringAvailabilityManager
				.getDaywiseRecurringAvailabilities("test_user_id");
		Assert.assertEquals(6, daywiseRecurringAvailability.size());

		Assert.assertEquals(3, daywiseRecurringAvailability.get(DayOfTheWeek.SAT).size());
	}

	@Test
	public void shouldReturnSetTimezone() {
		final Map<DayOfTheWeek, List<RecurringAvailabilityDAO>> daywiseRecurringAvailabilities = Map.of(
				DayOfTheWeek.MON, new ArrayList<>(),
				DayOfTheWeek.THU, List.of(RecurringAvailabilityDAO.builder().timezone("Afria/Mapusa").build()));

		Assert.assertEquals("Afria/Mapusa",
				this.recurringAvailabilityManager.getTimezoneOfRecurringAvailability(daywiseRecurringAvailabilities));
	}

	@Test
	public void shouldReturnDefaultTimezone() {
		final Map<DayOfTheWeek, List<RecurringAvailabilityDAO>> daywiseRecurringAvailabilities = Map.of(
				DayOfTheWeek.MON, new ArrayList<>(),
				DayOfTheWeek.THU, new ArrayList<>());

		Assert.assertEquals(DateUtils.TIMEZONE_ASIA_KOLKATA,
				this.recurringAvailabilityManager.getTimezoneOfRecurringAvailability(daywiseRecurringAvailabilities));

	}
}
