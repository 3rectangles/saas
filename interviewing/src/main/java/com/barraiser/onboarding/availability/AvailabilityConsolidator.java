/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.availability;

import com.barraiser.common.enums.DayOfTheWeek;
import com.barraiser.onboarding.availability.DTO.InterviewingTimeSlot;
import com.barraiser.onboarding.dal.AvailabilityDAO;
import com.barraiser.onboarding.dal.RecurringAvailabilityDAO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class is used to consolidate/merge
 * availabilities at our disposal from
 * various sources and in various formats.
 */
@AllArgsConstructor
@Component
public class AvailabilityConsolidator {

	private final InterviewingTimeslotUtilityService interviewingTimeslotUtilityService;
	private final RecurringAvailabilityManager recurringAvailabilityManager;

	/**
	 * Here we merge recurring availabilities with the custom availabilities given
	 * by the user.
	 * <p>
	 * Example 1: ADJACENT AVAILABILITIES
	 * Lets say recurring availability : Monday : 2 - 7 pm
	 * Custom Availability : in epoch but lets assume it is a 24th jan 2022 1 - 2 pm
	 * . Which turns out to be
	 * a Monday
	 * <p>
	 * CONSOLIDATED AVAILABILITY : 24th Jan 1 - 7 pm. (since they are adjacent)
	 *
	 * <p>
	 * <p>
	 * Example 2: NON ADJACENT AVAILABILITIES
	 * Lets say recurring availability : Monday : 2 - 7 pm
	 * Custom Availability : in epoch but lets assume it is a 24th jan 2022 12 - 1
	 * pm . Which turns out to be
	 * a Monday
	 * <p>
	 * CONSOLIDATED AVAILABILITY : 24th Jan 12- 1 pm , 24th Jan 2 - 7 pm.
	 * <p>
	 * NOTE: Overlapping availabilities are prevented at validation (ie becauset
	 * their max interviews cannot be combined right now)
	 *
	 * @param customAvailabilities
	 * @param daywiseRecurringAvailabilities
	 * @param startDatetime
	 *            time from which availability is needed
	 * @param endDatetime
	 *            time until which availability is needed
	 * @return
	 */

	public List<AvailabilityDAO> consolidateAvailabilites(final List<AvailabilityDAO> customAvailabilities,
			final Map<DayOfTheWeek, List<RecurringAvailabilityDAO>> daywiseRecurringAvailabilities,
			final Long startDatetime, final Long endDatetime) {

		// Step1 : Extrapolate recurring availabilities to datewise availabilities.
		final List<AvailabilityDAO> extrapolatedRecurringAvailabilitiesList = this
				.extrapolateRecurringAvailabilitiesOverTimerange(daywiseRecurringAvailabilities, startDatetime,
						endDatetime);

		// Step2: Merge extrapolated recurring availabilities with custom
		// availabilities.
		final List<AvailabilityDAO> consolidatedAvailabilities = this.mergeAvailabilities(customAvailabilities,
				extrapolatedRecurringAvailabilitiesList);

		return consolidatedAvailabilities;
	}

	/**
	 * @param customAvailabilities
	 * @param extrapolatedRecurringAvailabilities
	 * @return
	 */
	private List<AvailabilityDAO> mergeAvailabilities(final List<AvailabilityDAO> customAvailabilities,
			final List<AvailabilityDAO> extrapolatedRecurringAvailabilities) {

		final List<InterviewingTimeSlot> timeslotsToBeMerged = customAvailabilities.stream()
				.map(x -> this.interviewingTimeslotUtilityService.toInterviewingTimeslot(x))
				.collect(Collectors.toList());

		timeslotsToBeMerged.addAll(
				extrapolatedRecurringAvailabilities.stream()
						.map(x -> this.interviewingTimeslotUtilityService.toInterviewingTimeslot(x))
						.collect(Collectors.toList()));

		final List<InterviewingTimeSlot> mergedTimeslots = this.interviewingTimeslotUtilityService
				.mergeSlots(timeslotsToBeMerged);

		return mergedTimeslots.stream()
				.map(y -> this.interviewingTimeslotUtilityService.toAvailabilityDAO(y))
				.collect(Collectors.toList());
	}

	public List<AvailabilityDAO> mergeAvailabilities(final List<AvailabilityDAO> availabilties) {
		final List<InterviewingTimeSlot> timeslotsToBeMerged = availabilties.stream()
				.map(x -> this.interviewingTimeslotUtilityService.toInterviewingTimeslot(x))
				.collect(Collectors.toList());

		final List<InterviewingTimeSlot> mergedTimeslots = this.interviewingTimeslotUtilityService
				.mergeSlots(timeslotsToBeMerged);

		return mergedTimeslots.stream()
				.map(y -> this.interviewingTimeslotUtilityService.toAvailabilityDAO(y))
				.collect(Collectors.toList());
	}

	/**
	 * Recurring availability : Monday 2 - 3 pm
	 * Then Extrapollated availability : Actual availability for all mondays between
	 * sstart and end date.
	 *
	 * @param daywiseRecurringAvailabilities
	 * @param startDatetime
	 * @param endDatetime
	 * @return
	 */
	public List<AvailabilityDAO> extrapolateRecurringAvailabilitiesOverTimerange(
			final Map<DayOfTheWeek, List<RecurringAvailabilityDAO>> daywiseRecurringAvailabilities,
			final Long startDatetime, final Long endDatetime) {

		final Map<LocalDate, List<AvailabilityDAO>> explodedDatewiseAvailability = new HashMap<>();
		final String timezoneOfRecurringAvailability = this.recurringAvailabilityManager
				.getTimezoneOfRecurringAvailability(daywiseRecurringAvailabilities);

		final ZoneId zoneId = ZoneId.of(timezoneOfRecurringAvailability);

		final OffsetDateTime startDatetimeWithTimezone = OffsetDateTime.ofInstant(Instant.ofEpochSecond(startDatetime),
				zoneId);
		final OffsetDateTime endDatetimeWithTimezone = OffsetDateTime.ofInstant(Instant.ofEpochSecond(endDatetime),
				zoneId);

		final LocalDateTime startOfDayForStartDate = startDatetimeWithTimezone.toLocalDate().atTime(0, 0, 0);
		final LocalDateTime endOfDayForEndDate = endDatetimeWithTimezone.toLocalDate().atTime(23, 59, 59);

		long numOfDaysBetween = ChronoUnit.DAYS.between(startOfDayForStartDate, endOfDayForEndDate) + 1;

		IntStream.iterate(0, i -> i + 1)
				.limit(numOfDaysBetween)
				.forEach(i -> {
					final LocalDateTime dateTime = startOfDayForStartDate.plusDays(i);
					final DayOfTheWeek dayOfTheWeek = DayOfTheWeek.fromString(dateTime.getDayOfWeek().toString());
					final LocalDate date = dateTime.toLocalDate();

					final List<RecurringAvailabilityDAO> recurringAvailabilitiesForTheDay = daywiseRecurringAvailabilities
							.getOrDefault(dayOfTheWeek, new ArrayList<>());

					final List<AvailabilityDAO> explodedRecurringAvailabilities = new ArrayList<>();

					for (RecurringAvailabilityDAO recurringAvailabilitySlot : recurringAvailabilitiesForTheDay) {

						explodedRecurringAvailabilities
								.add(this.toAvailabilityDAO(dateTime, timezoneOfRecurringAvailability,
										recurringAvailabilitySlot));

						explodedDatewiseAvailability.put(date, explodedRecurringAvailabilities);
					}
				});

		// Filtering date wise Availability between date range specified.
		return explodedDatewiseAvailability.values()
				.stream()
				.flatMap(List::stream)
				.filter(availability -> availability.getStartDate() < endDatetime
						&& availability.getEndDate() > startDatetime)
				.collect(Collectors.toList());
	}

	/**
	 * @param customAvailabilities
	 * @param daywiseRecurringAvailabilities
	 * @param availabilityStart
	 * @param availabilityEnd
	 * @return
	 */
	public Boolean checkIfAvailabilitiesOverlapping(final List<AvailabilityDAO> customAvailabilities,
			final Map<DayOfTheWeek, List<RecurringAvailabilityDAO>> daywiseRecurringAvailabilities,
			final Long availabilityStart, final Long availabilityEnd) {

		final List<AvailabilityDAO> extrapolateRecurringAvailabilitiesOverTimerange = this
				.extrapolateRecurringAvailabilitiesOverTimerange(daywiseRecurringAvailabilities, availabilityStart,
						availabilityEnd);

		final List<InterviewingTimeSlot> timeslotsToBeMerged = customAvailabilities.stream()
				.map(x -> this.interviewingTimeslotUtilityService.toInterviewingTimeslot(x))
				.collect(Collectors.toList());

		timeslotsToBeMerged.addAll(
				extrapolateRecurringAvailabilitiesOverTimerange.stream()
						.map(x -> this.interviewingTimeslotUtilityService.toInterviewingTimeslot(x))
						.collect(Collectors.toList()));

		return this.interviewingTimeslotUtilityService.getOverlappingSlots(timeslotsToBeMerged).size() != 0;
	}

	private AvailabilityDAO toAvailabilityDAO(final LocalDateTime startOfDayDatetime, final String timezone,
			final RecurringAvailabilityDAO recurringAvailabilityDAO) {

		final ZoneId zoneId = ZoneId.of(timezone);

		final LocalDateTime modifiedStartTime = startOfDayDatetime
				.plusSeconds(recurringAvailabilityDAO.getSlotStartTime());
		final LocalDateTime modifiedEndTime = startOfDayDatetime
				.plusSeconds(recurringAvailabilityDAO.getSlotEndTime());

		final AvailabilityDAO availabilityDAO = AvailabilityDAO.builder()
				.userId(recurringAvailabilityDAO.getUserId())
				.startDate(modifiedStartTime.atZone(zoneId).toEpochSecond())
				.endDate(modifiedEndTime.atZone(zoneId).toEpochSecond())
				.maximumNumberOfInterviews(recurringAvailabilityDAO.getMaximumNumberOfInterviewsInSlot())
				.build();

		return availabilityDAO;
	}

}
