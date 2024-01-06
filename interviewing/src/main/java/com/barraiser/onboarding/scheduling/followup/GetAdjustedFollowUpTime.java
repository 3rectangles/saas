/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.followup;

import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.TimeZone;

@AllArgsConstructor
@Component
public class GetAdjustedFollowUpTime {
	public DynamicAppConfigProperties appConfigProperties;

	public long findTimeXMinsAfterExcludingNonOpHrs(final int xMinutes, final long followUpTime) {
		final long adjustedTimeInEpoch = followUpTime
				+ ((long) xMinutes * FollowUpConstants.SECONDS_IN_A_MINUTE);

		final int adjustedTimeInHour = this.findTimeInHourFromEpoch(adjustedTimeInEpoch);
		final int followUpTimeInHour = this.findTimeInHourFromEpoch(followUpTime);
		final int adjustedTimeInMinutes = this.findTimeInMinutesFromEpoch(adjustedTimeInEpoch);
		final int followUpTimeInMinutes = this.findTimeInMinutesFromEpoch(followUpTime);

		final int nonOperationalHourStart = this.appConfigProperties
				.getInt(FollowUpConstants.DYNAMO_NON_OPERATIONAL_TIME_START);
		final int nonOperationalHourEnd = this.appConfigProperties
				.getInt(FollowUpConstants.DYNAMO_NON_OPERATIONAL_TIME_END);

		final Boolean doesFollowUpTimeInHourLieInNonOpHrs = this.doesFollowUpTimeInHourLieInNonOpHrs(
				adjustedTimeInHour, followUpTimeInHour, nonOperationalHourStart, nonOperationalHourEnd);
		final Boolean doesAdjustedTimeInHourAndFollowUpTimeInHourLieInNonOpHrs = this
				.doesAdjustedTimeInHourAndFollowUpTimeInHourLieInNonOpHrs(adjustedTimeInHour,
						followUpTimeInHour, nonOperationalHourStart, nonOperationalHourEnd);
		final Boolean doNonOperationalHrsLieBetweenFollowUpTimeAndAdjustedTime = this
				.doNonOperationalHrsLieBetweenFollowUpTimeAndAdjustedTime(adjustedTimeInHour,
						followUpTimeInHour, nonOperationalHourStart, nonOperationalHourEnd);
		final Boolean doesAdjustedTimeLieBetweenNonOperationalHrs = this
				.doesAdjustedTimeLieBetweenNonOperationalHrs(adjustedTimeInHour, followUpTimeInHour,
						nonOperationalHourStart, nonOperationalHourEnd);

		if (doesAdjustedTimeLieBetweenNonOperationalHrs || doNonOperationalHrsLieBetweenFollowUpTimeAndAdjustedTime)
			return this.adjustedTimeLiesInNonOpHrsOrNonOpHrsLieBetweenfollowUpTimeAndAdjustedTime(
					adjustedTimeInEpoch, nonOperationalHourStart, nonOperationalHourEnd);
		else if (doesAdjustedTimeInHourAndFollowUpTimeInHourLieInNonOpHrs)
			return this.adjustedTimeAndfollowUpTimeLieInNonOpHrs(adjustedTimeInEpoch,
					adjustedTimeInHour, adjustedTimeInMinutes, nonOperationalHourEnd, xMinutes);
		else if (doesFollowUpTimeInHourLieInNonOpHrs)
			return this.followUpTimeLiesBetweenNonOperationalHrs(adjustedTimeInEpoch,
					followUpTimeInHour, followUpTimeInMinutes, nonOperationalHourEnd);
		else
			return adjustedTimeInEpoch;
	}

	// followUp time: 11 am, adj time: 11pm OR followUp time: 9 am, adj time: 9 pm
	private long adjustedTimeLiesInNonOpHrsOrNonOpHrsLieBetweenfollowUpTimeAndAdjustedTime(
			final long adjustedTimeInEpoch, final int nonOperationalHourStart, final int nonOperationalHourEnd) {
		long timeGapBetweenNonOperationHourStartAndEnd = (long) ((24 - nonOperationalHourStart)
				+ nonOperationalHourEnd) * FollowUpConstants.SECONDS_IN_A_MINUTE
				* FollowUpConstants.MINUTES_IN_AN_HOUR;
		return adjustedTimeInEpoch + timeGapBetweenNonOperationHourStartAndEnd;
	}

	// followUp time: 11 pm, adj time: 11 am

	private long followUpTimeLiesBetweenNonOperationalHrs(final long adjustedTimeInEpoch,
			final int followUpTimeInHour, final int followUpTimeInMinutes, final int nonOperationalHourEnd) {
		final int nonOperationalHrEndMinusFollowUpTimeStart;
		if (followUpTimeInHour <= nonOperationalHourEnd)
			nonOperationalHrEndMinusFollowUpTimeStart = ((-followUpTimeInHour + nonOperationalHourEnd)
					* FollowUpConstants.SECONDS_IN_A_MINUTE * FollowUpConstants.MINUTES_IN_AN_HOUR)
					+ (followUpTimeInMinutes * FollowUpConstants.SECONDS_IN_A_MINUTE);
		else
			nonOperationalHrEndMinusFollowUpTimeStart = ((nonOperationalHourEnd + (24 - followUpTimeInHour))
					* FollowUpConstants.SECONDS_IN_A_MINUTE * FollowUpConstants.MINUTES_IN_AN_HOUR)
					+ (followUpTimeInMinutes * FollowUpConstants.SECONDS_IN_A_MINUTE
							* FollowUpConstants.MINUTES_IN_AN_HOUR);
		return adjustedTimeInEpoch + nonOperationalHrEndMinusFollowUpTimeStart;
	}

	// followUp time: 12 am, adj time: 10 pm
	private long adjustedTimeAndfollowUpTimeLieInNonOpHrs(final long adjustedTimeInEpoch,
			final int adjustedTimeInHour, final int adjustedTimeInMinutes, final int nonOperationalHourEnd,
			final int xMinutes) {
		if (adjustedTimeInHour <= nonOperationalHourEnd)
			return adjustedTimeInEpoch
					+ ((long) (nonOperationalHourEnd - adjustedTimeInHour)
							* FollowUpConstants.SECONDS_IN_A_MINUTE
							* FollowUpConstants.MINUTES_IN_AN_HOUR
							- ((long) adjustedTimeInMinutes * FollowUpConstants.SECONDS_IN_A_MINUTE))
					+ ((long) xMinutes * FollowUpConstants.SECONDS_IN_A_MINUTE);
		return adjustedTimeInEpoch + ((nonOperationalHourEnd + (long) (24 - adjustedTimeInHour)
				* FollowUpConstants.SECONDS_IN_A_MINUTE * FollowUpConstants.MINUTES_IN_AN_HOUR)
				- ((long) adjustedTimeInMinutes * FollowUpConstants.SECONDS_IN_A_MINUTE))
				+ ((long) xMinutes * FollowUpConstants.SECONDS_IN_A_MINUTE);
	}

	private Boolean doesAdjustedTimeLieBetweenNonOperationalHrs(final int adjustedTimeInHour,
			final int followUpTimeInHour, final int nonOperationalHourStart, final int nonOperationalHourEnd) {
		return (followUpTimeInHour >= nonOperationalHourEnd
				&& followUpTimeInHour < nonOperationalHourStart
				&& (adjustedTimeInHour >= nonOperationalHourStart || adjustedTimeInHour < nonOperationalHourEnd));
	}

	private Boolean doNonOperationalHrsLieBetweenFollowUpTimeAndAdjustedTime(final int adjustedTimeInHour,
			final int followUpTimeInHour, final int nonOperationalHourStart, final int nonOperationalHourEnd) {
		return (followUpTimeInHour < nonOperationalHourStart
				&& adjustedTimeInHour >= nonOperationalHourEnd && followUpTimeInHour > adjustedTimeInHour);
	}

	private Boolean doesAdjustedTimeInHourAndFollowUpTimeInHourLieInNonOpHrs(final int adjustedTimeInHour,
			final int followUpTimeInHour, final int nonOperationalHourStart, final int nonOperationalHourEnd) {
		return ((followUpTimeInHour >= nonOperationalHourStart
				|| followUpTimeInHour < nonOperationalHourEnd)
				&& (adjustedTimeInHour >= nonOperationalHourStart || adjustedTimeInHour < nonOperationalHourEnd));
	}

	private Boolean doesFollowUpTimeInHourLieInNonOpHrs(final int adjustedTimeInHour, final int followUpTimeInHour,
			final int nonOperationalHourStart, final int nonOperationalHourEnd) {
		return ((followUpTimeInHour >= nonOperationalHourStart
				|| followUpTimeInHour < nonOperationalHourEnd)
				&& (adjustedTimeInHour >= nonOperationalHourEnd && adjustedTimeInHour < nonOperationalHourStart));
	}

	private int findTimeInMinutesFromEpoch(final long timeInEpoch) {
		final Calendar instanceFromEpoch = this.getCalendarInstanceFromEpoch(timeInEpoch);
		return instanceFromEpoch.get(Calendar.MINUTE);
	}

	private int findTimeInHourFromEpoch(final long timeInEpoch) {
		final Calendar instanceFromEpoch = this.getCalendarInstanceFromEpoch(timeInEpoch);
		return instanceFromEpoch.get(Calendar.HOUR_OF_DAY);
	}

	// TODO: make follow-up for scheduling dynamic as per candidate timezone
	private Calendar getCalendarInstanceFromEpoch(long epochTime) {
		Calendar calendarInstance = Calendar.getInstance();
		calendarInstance.setTimeInMillis(epochTime * 1000L);
		calendarInstance.setTimeZone(TimeZone.getTimeZone(FollowUpConstants.TIMEZONE_ASIA_KOLKATA));
		return calendarInstance;
	}
}
