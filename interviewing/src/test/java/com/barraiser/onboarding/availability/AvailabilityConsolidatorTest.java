/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.availability;

import com.barraiser.common.enums.DayOfTheWeek;
import com.barraiser.onboarding.dal.AvailabilityDAO;
import com.barraiser.onboarding.dal.RecurringAvailabilityDAO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AvailabilityConsolidatorTest {

	@InjectMocks
	private AvailabilityConsolidator availabilityConsolidator;

	@Spy
	private InterviewingTimeslotUtilityService interviewingTimeslotUtilityService;

	@Mock
	private RecurringAvailabilityManager recurringAvailabilityManager;

	/**
	 * Scenario: Custom availabilities supplied.
	 * Recurring Availabilities NOT supplied.
	 */
	@Test
	public void testAvailabilityConsolidationScenario1() {

		when(this.recurringAvailabilityManager.getTimezoneOfRecurringAvailability(any()))
				.thenReturn("Asia/Kolkata");

		final List<AvailabilityDAO> customAvailabilities = Arrays.asList(
				AvailabilityDAO.builder()
						.userId("test_expert")
						.startDate(1658723000l)
						.endDate(1658723100l)
						.build(),
				AvailabilityDAO.builder()
						.userId("test_expert")
						.startDate(1658723200l)
						.endDate(1658723300l)
						.build());

		final Map<DayOfTheWeek, List<RecurringAvailabilityDAO>> daywiseRecurringAvailabilities = new HashMap<>();

		final List<AvailabilityDAO> consolidatedAvailabilities = this.availabilityConsolidator
				.consolidateAvailabilites(customAvailabilities, daywiseRecurringAvailabilities, 0l, 0l);

		Assert.assertEquals(2, consolidatedAvailabilities.size());
	}

	/**
	 * Scenario: Custom availabilities NOT supplied.
	 * Recurring Availabilities supplied.
	 * <p>
	 * No valid timerange to extrapolate recurring availabilities
	 */
	@Test
	public void testAvailabilityConsolidationScenario2() {

		when(this.recurringAvailabilityManager.getTimezoneOfRecurringAvailability(any()))
				.thenReturn("Asia/Kolkata");

		final List<AvailabilityDAO> customAvailabilities = new ArrayList<>();

		final Map<DayOfTheWeek, List<RecurringAvailabilityDAO>> daywiseRecurringAvailabilities = Map.of(
				DayOfTheWeek.MON, List.of(
						RecurringAvailabilityDAO.builder()
								.userId("test_expert")
								.slotStartTime(3600)
								.slotEndTime(7200)
								.build()));

		final List<AvailabilityDAO> consolidatedAvailabilities = this.availabilityConsolidator
				.consolidateAvailabilites(customAvailabilities, daywiseRecurringAvailabilities, 0l, 0l);

		Assert.assertEquals(0, consolidatedAvailabilities.size());
	}

	/**
	 * Scenario: Custom availabilities NOT supplied.
	 * Recurring Availabilities supplied.
	 * <p>
	 * Start and end date window = 1 week.
	 * Hence the same recurrance slot will just be convertedd
	 * to epoch.
	 */
	@Test
	public void testAvailabilityConsolidationScenario3() {

		when(this.recurringAvailabilityManager.getTimezoneOfRecurringAvailability(any()))
				.thenReturn("Asia/Kolkata");

		final List<AvailabilityDAO> customAvailabilities = new ArrayList<>();

		final Map<DayOfTheWeek, List<RecurringAvailabilityDAO>> daywiseRecurringAvailabilities = Map.of(
				DayOfTheWeek.MON, List.of(
						RecurringAvailabilityDAO.builder()
								.userId("test_expert")
								.slotStartTime(21600)
								.slotEndTime(28800)
								.timezone("Asia/Kolkata")
								.build()));

		final List<AvailabilityDAO> consolidatedAvailabilities = this.availabilityConsolidator.consolidateAvailabilites(
				customAvailabilities, daywiseRecurringAvailabilities, 1658082600l, 1658687400l);

		Assert.assertEquals(2, consolidatedAvailabilities.size());
		Assert.assertEquals(1658104200l, consolidatedAvailabilities.get(0).getStartDate().longValue());
		Assert.assertEquals(1658111400l, consolidatedAvailabilities.get(0).getEndDate().longValue());
	}

	/**
	 * Scenario: Custom availabilities NOT supplied.
	 * Recurring Availabilities supplied but in another timezone (not Asia/Kolkata)
	 * <p>
	 * Start and end date window = 1 week.
	 * Hence the same recurrance slot will just be convertedd
	 * to epoch.
	 */
	@Test
	public void testAvailabilityConsolidationScenario3_1() {

		/**
		 * CASE 1 : UTC
		 */
		when(this.recurringAvailabilityManager.getTimezoneOfRecurringAvailability(any()))
				.thenReturn("Universal");

		List<AvailabilityDAO> customAvailabilities = new ArrayList<>();

		Map<DayOfTheWeek, List<RecurringAvailabilityDAO>> daywiseRecurringAvailabilities = Map.of(
				DayOfTheWeek.MON, List.of(
						RecurringAvailabilityDAO.builder()
								.userId("test_expert")
								.slotStartTime(21600)
								.slotEndTime(28800)
								.timezone("Universal")
								.build()));

		List<AvailabilityDAO> consolidatedAvailabilities = this.availabilityConsolidator.consolidateAvailabilites(
				customAvailabilities, daywiseRecurringAvailabilities, 1658082600l, 1658687400l);

		Assert.assertEquals(1, consolidatedAvailabilities.size());
		Assert.assertEquals(1658124000l, consolidatedAvailabilities.get(0).getStartDate().longValue());
		Assert.assertEquals(1658131200l, consolidatedAvailabilities.get(0).getEndDate().longValue());

		/**
		 * CASE 2 : America/Chicago
		 */
		when(this.recurringAvailabilityManager.getTimezoneOfRecurringAvailability(any()))
				.thenReturn("America/Chicago");

		customAvailabilities = new ArrayList<>();

		daywiseRecurringAvailabilities = Map.of(
				DayOfTheWeek.MON, List.of(
						RecurringAvailabilityDAO.builder()
								.userId("test_expert")
								.slotStartTime(21600)
								.slotEndTime(28800)
								.timezone("America/Chicago")
								.build()));

		consolidatedAvailabilities = this.availabilityConsolidator.consolidateAvailabilites(
				customAvailabilities, daywiseRecurringAvailabilities, 1658082600l, 1658687400l);

		Assert.assertEquals(1, consolidatedAvailabilities.size());
		Assert.assertEquals(1658142000l, consolidatedAvailabilities.get(0).getStartDate().longValue());
		Assert.assertEquals(1658149200l, consolidatedAvailabilities.get(0).getEndDate().longValue());
	}

	/**
	 * Scenario: Custom availabilities NOT supplied.
	 * Recurring Availabilities supplied.
	 * <p>
	 * Start and end date window = 2 week.
	 * Should generate 2 extrapollated slots.
	 */
	@Test
	public void testAvailabilityConsolidationScenario4() {

		when(this.recurringAvailabilityManager.getTimezoneOfRecurringAvailability(any()))
				.thenReturn("Asia/Kolkata");

		final List<AvailabilityDAO> customAvailabilities = new ArrayList<>();

		final Map<DayOfTheWeek, List<RecurringAvailabilityDAO>> daywiseRecurringAvailabilities = Map.of(
				DayOfTheWeek.MON, List.of(
						RecurringAvailabilityDAO.builder()
								.userId("test_expert")
								.slotStartTime(21600)
								.slotEndTime(28800)
								.timezone("Asia/Kolkata")
								.build()));

		final List<AvailabilityDAO> consolidatedAvailabilities = this.availabilityConsolidator.consolidateAvailabilites(
				customAvailabilities, daywiseRecurringAvailabilities, 1658082600l, 1658687400l);

		Assert.assertEquals(2, consolidatedAvailabilities.size());
	}

	/**
	 * Scenario: Custom availabilities NOT supplied.
	 * Recurring Availabilities supplied.
	 * <p>
	 * <p>
	 * Start and end window not including day for which
	 * recurring slot is given
	 */
	@Test
	public void testAvailabilityConsolidationScenario5() {

		when(this.recurringAvailabilityManager.getTimezoneOfRecurringAvailability(any()))
				.thenReturn("Asia/Kolkata");

		final List<AvailabilityDAO> customAvailabilities = new ArrayList<>();

		final Map<DayOfTheWeek, List<RecurringAvailabilityDAO>> daywiseRecurringAvailabilities = Map.of(
				DayOfTheWeek.MON, List.of(
						RecurringAvailabilityDAO.builder()
								.userId("test_expert")
								.slotStartTime(21600)
								.slotEndTime(28800)
								.timezone("Asia/Kolkata")
								.build()));

		final List<AvailabilityDAO> consolidatedAvailabilities = this.availabilityConsolidator.consolidateAvailabilites(
				customAvailabilities, daywiseRecurringAvailabilities, 1658169000l, 1658687400l);

		Assert.assertEquals(1, consolidatedAvailabilities.size());
	}

	/**
	 * Scenario: Custom availabilities NOT supplied.
	 * Recurring Availabilities supplied.
	 * <p>
	 * Recurring availabilities of adjacent slots combined.
	 */
	@Test
	public void testAvailabilityConsolidationScenario6() {
		when(this.recurringAvailabilityManager.getTimezoneOfRecurringAvailability(any()))
				.thenReturn("Asia/Kolkata");

		final List<AvailabilityDAO> customAvailabilities = new ArrayList<>();

		final Map<DayOfTheWeek, List<RecurringAvailabilityDAO>> daywiseRecurringAvailabilities = Map.of(
				DayOfTheWeek.MON, List.of(
						RecurringAvailabilityDAO.builder()
								.userId("test_expert")
								.slotStartTime(21600)
								.slotEndTime(28800)
								.maximumNumberOfInterviewsInSlot(1)
								.timezone("Asia/Kolkata")
								.build(),
						RecurringAvailabilityDAO.builder()
								.userId("test_expert")
								.slotStartTime(28800)
								.slotEndTime(36000)
								.maximumNumberOfInterviewsInSlot(1)
								.timezone("Asia/Kolkata")
								.build(),
						RecurringAvailabilityDAO.builder()
								.userId("test_expert")
								.slotStartTime(36000)
								.slotEndTime(43200)
								.maximumNumberOfInterviewsInSlot(1)
								.timezone("Asia/Kolkata")
								.build()));

		final List<AvailabilityDAO> consolidatedAvailabilities = this.availabilityConsolidator.consolidateAvailabilites(
				customAvailabilities, daywiseRecurringAvailabilities, 1658082600l, 1658687400l);
		Assert.assertEquals(2, consolidatedAvailabilities.size());
		Assert.assertEquals(1658104200l, consolidatedAvailabilities.get(0).getStartDate().longValue());
		Assert.assertEquals(1658125800l, consolidatedAvailabilities.get(0).getEndDate().longValue());
	}

	/**
	 * Scenario: Custom availabilities supplied.
	 * Recurring Availabilities NOT supplied.
	 * <p>
	 * Adjacent Custom availability slots merged.
	 */
	@Test
	public void testAvailabilityConsolidationScenario7() {

		when(this.recurringAvailabilityManager.getTimezoneOfRecurringAvailability(any()))
				.thenReturn("Asia/Kolkata");

		final List<AvailabilityDAO> customAvailabilities = Arrays.asList(
				AvailabilityDAO.builder()
						.userId("test_expert")
						.startDate(1658125200l)
						.endDate(1658125300l)
						.maximumNumberOfInterviews(1)
						.build(),
				AvailabilityDAO.builder()
						.userId("test_expert")
						.startDate(1658125300l)
						.endDate(1658125800l)
						.maximumNumberOfInterviews(1)
						.build());

		final Map<DayOfTheWeek, List<RecurringAvailabilityDAO>> daywiseRecurringAvailabilities = new HashMap<>();

		final List<AvailabilityDAO> consolidatedAvailabilities = this.availabilityConsolidator
				.consolidateAvailabilites(customAvailabilities, daywiseRecurringAvailabilities, 0l, 0l);

		Assert.assertEquals(1, consolidatedAvailabilities.size());
		Assert.assertEquals(1658125200l, consolidatedAvailabilities.get(0).getStartDate().longValue());
		Assert.assertEquals(1658125800l, consolidatedAvailabilities.get(0).getEndDate().longValue());
	}

	/**
	 * Scenario: Custom availabilities supplied.
	 * Recurring Availabilities supplied.
	 * <p>
	 * Recurring availability slots adjacent to custom
	 * availability slots merged.
	 */
	@Test
	public void testAvailabilityConsolidationScenario8() {

		when(this.recurringAvailabilityManager.getTimezoneOfRecurringAvailability(any()))
				.thenReturn("Asia/Kolkata");

		final List<AvailabilityDAO> customAvailabilities = Arrays.asList(
				AvailabilityDAO.builder() // Wednesday, 20 July 10am - 12pm Asia/Kolkata
						.userId("test_expert")
						.startDate(1658291400l)
						.endDate(1658298600l)
						.maximumNumberOfInterviews(2)
						.build(),
				AvailabilityDAO.builder()
						.userId("test_expert")
						.startDate(1658298600l) // Wednesday, 20 July 12pm - 13pm Asia/Kolkata
						.endDate(1658302200l)
						.maximumNumberOfInterviews(1)
						.build(),
				AvailabilityDAO.builder()
						.userId("test_expert")
						.startDate(1658896200l)// Wednesday, 27 July 10am - 12pm Asia/Kolkata
						.endDate(1658903400l)
						.maximumNumberOfInterviews(2)
						.build());

		final Map<DayOfTheWeek, List<RecurringAvailabilityDAO>> daywiseRecurringAvailabilities = Map.of(
				DayOfTheWeek.MON, List.of(
						RecurringAvailabilityDAO.builder()
								.userId("test_expert")
								.slotStartTime(21600)
								.slotEndTime(28800)
								.maximumNumberOfInterviewsInSlot(1)
								.timezone("Asia/Kolkata")
								.build()),
				DayOfTheWeek.WED, List.of(
						RecurringAvailabilityDAO.builder()
								.userId("test_expert")
								.slotStartTime(32400)
								.slotEndTime(36000)
								.maximumNumberOfInterviewsInSlot(1)
								.timezone("Asia/Kolkata")
								.build()));

		// 19th july to 28th July
		final List<AvailabilityDAO> consolidatedAvailabilities = this.availabilityConsolidator
				.consolidateAvailabilites(customAvailabilities, daywiseRecurringAvailabilities, 1658169000l,
						1659032940l);

		Assert.assertEquals(3, consolidatedAvailabilities.size());

		// first slot should be the combined wednesday slot made by merging recurring
		// and custom slots. (Wednesday 20th July , 9am to 1pm)
		Assert.assertEquals(1658287800l, consolidatedAvailabilities.get(0).getStartDate().longValue());
		Assert.assertEquals(1658302200l, consolidatedAvailabilities.get(0).getEndDate().longValue());
		Assert.assertEquals(4, consolidatedAvailabilities.get(0).getMaximumNumberOfInterviews().longValue());

		// second slot should be the extrapolated monday slot
		Assert.assertEquals(1658709000l, consolidatedAvailabilities.get(1).getStartDate().longValue());
		Assert.assertEquals(1, consolidatedAvailabilities.get(1).getMaximumNumberOfInterviews().longValue());

		// third slot should be wednesday slot made by merging recurring and custom
		// slots. (Wednesday 27th July , 9am to 12pm)
		Assert.assertEquals(1658892600l, consolidatedAvailabilities.get(2).getStartDate().longValue());
		Assert.assertEquals(1658903400l, consolidatedAvailabilities.get(2).getEndDate().longValue());
		Assert.assertEquals(3, consolidatedAvailabilities.get(2).getMaximumNumberOfInterviews().longValue());
	}

}
