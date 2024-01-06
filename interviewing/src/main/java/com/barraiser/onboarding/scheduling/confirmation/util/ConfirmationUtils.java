/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.confirmation.util;

import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.scheduling.confirmation.ConfirmationConstants;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.TimeZone;

@AllArgsConstructor
@Component
public class ConfirmationUtils {
	private DynamicAppConfigProperties appConfigProperties;

	public long findTimeXMinsBeforeExcludingNonOpHrs(final int xMinutes, final long interviewScheduleTime,
			final String timezone) {
		final long adjustedTimeInEpoch = interviewScheduleTime
				- ((long) xMinutes * ConfirmationConstants.SECONDS_IN_A_MINUTE);

		final int adjustedTimeInHour = this.findTimeInHourFromEpoch(adjustedTimeInEpoch, timezone);
		final int interviewTimeInHour = this.findTimeInHourFromEpoch(interviewScheduleTime, timezone);
		final int interviewTimeInMinutes = this.findTimeInMinutesFromEpoch(interviewScheduleTime, timezone);

		final int nonOperationalHourStart = this.appConfigProperties
				.getInt(ConfirmationConstants.DYNAMO_NON_OPERATIONAL_TIME_START);
		final int nonOperationalHourEnd = this.appConfigProperties
				.getInt(ConfirmationConstants.DYNAMO_NON_OPERATIONAL_TIME_END);

		final Boolean doesAdjustedTimeInHourLieInNonOpHrs = this.doesAdjustedTimeInHourLieInNonOpHrs(
				adjustedTimeInHour, interviewTimeInHour, nonOperationalHourStart, nonOperationalHourEnd);
		final Boolean doesAdjustedTimeInHourAndInterviewTimeInHourLieInNonOpHrs = this
				.doesAdjustedTimeInHourAndInterviewTimeInHourLieInNonOpHrs(adjustedTimeInHour,
						interviewTimeInHour, nonOperationalHourStart, nonOperationalHourEnd);
		final Boolean doNonOperationalHrsLieBetweenInterviewTimeAndAdjustedTime = this
				.doNonOperationalHrsLieBetweenInterviewTimeAndAdjustedTime(adjustedTimeInHour,
						interviewTimeInHour, nonOperationalHourStart, nonOperationalHourEnd);
		final Boolean doesInterviewTimeLieBetweenNonOperationalHrs = this
				.doesInterviewTimeLieBetweenNonOperationalHrs(adjustedTimeInHour, interviewTimeInHour,
						nonOperationalHourStart, nonOperationalHourEnd);

		if (doesAdjustedTimeInHourLieInNonOpHrs || doNonOperationalHrsLieBetweenInterviewTimeAndAdjustedTime)
			return this.adjustedTimeLiesInNonOpHrsOrNonOpHrsLieBetweenInterviewTimeAndAdjustedTime(
					adjustedTimeInEpoch, nonOperationalHourStart, nonOperationalHourEnd);
		else if (doesAdjustedTimeInHourAndInterviewTimeInHourLieInNonOpHrs)
			return this.adjustedTimeAndInterviewTimeLieInNonOpHrs(adjustedTimeInEpoch,
					interviewTimeInHour, interviewTimeInMinutes, nonOperationalHourStart);
		else if (doesInterviewTimeLieBetweenNonOperationalHrs)
			return this.interviewTimeLiesBetweenNonOperationalHrs(adjustedTimeInEpoch,
					interviewTimeInHour, interviewTimeInMinutes, nonOperationalHourStart);
		else
			return adjustedTimeInEpoch;
	}

	// interview time: 11 am, adj time: 11pm OR interview time: 9 am, adj time: 9 pm
	private long adjustedTimeLiesInNonOpHrsOrNonOpHrsLieBetweenInterviewTimeAndAdjustedTime(
			final long adjustedTimeInEpoch, final int nonOperationalHourStart, final int nonOperationalHourEnd) {
		long timeGapBetweenNonOperationHourStartAndEnd = (long) ((24 - nonOperationalHourStart)
				+ nonOperationalHourEnd) * ConfirmationConstants.SECONDS_IN_A_MINUTE
				* ConfirmationConstants.MINUTES_IN_AN_HOUR;
		return adjustedTimeInEpoch - timeGapBetweenNonOperationHourStartAndEnd;
	}

	// interview time: 11 pm, adj time: 11 am
	private long interviewTimeLiesBetweenNonOperationalHrs(final long adjustedTimeInEpoch,
			final int interviewTimeInHour, final int interviewTimeInMinutes, final int nonOperationalHourStart) {
		final int interviewTimeMinusNonOperationalHrStart;
		if (interviewTimeInHour >= nonOperationalHourStart)
			interviewTimeMinusNonOperationalHrStart = ((interviewTimeInHour - nonOperationalHourStart)
					* ConfirmationConstants.SECONDS_IN_A_MINUTE * ConfirmationConstants.MINUTES_IN_AN_HOUR)
					+ (interviewTimeInMinutes * ConfirmationConstants.SECONDS_IN_A_MINUTE);
		else
			interviewTimeMinusNonOperationalHrStart = ((interviewTimeInHour + (24 - nonOperationalHourStart))
					* ConfirmationConstants.SECONDS_IN_A_MINUTE * ConfirmationConstants.MINUTES_IN_AN_HOUR)
					+ (interviewTimeInMinutes * ConfirmationConstants.SECONDS_IN_A_MINUTE
							* ConfirmationConstants.MINUTES_IN_AN_HOUR);
		return adjustedTimeInEpoch - interviewTimeMinusNonOperationalHrStart;
	}

	// interview time: 12 am, adj time: 10 pm
	private long adjustedTimeAndInterviewTimeLieInNonOpHrs(final long adjustedTimeInEpoch,
			final int interviewTimeInHour, final int interviewTimeInMinutes, final int nonOperationalHourStart) {
		return interviewTimeInHour >= nonOperationalHourStart
				? adjustedTimeInEpoch
						- ((long) (interviewTimeInHour - nonOperationalHourStart)
								* ConfirmationConstants.SECONDS_IN_A_MINUTE
								* ConfirmationConstants.MINUTES_IN_AN_HOUR
								+ ((long) interviewTimeInMinutes * ConfirmationConstants.SECONDS_IN_A_MINUTE))
				: adjustedTimeInEpoch - (((interviewTimeInHour + (long) (24 - nonOperationalHourStart))
						* ConfirmationConstants.SECONDS_IN_A_MINUTE * ConfirmationConstants.MINUTES_IN_AN_HOUR)
						+ ((long) interviewTimeInMinutes * ConfirmationConstants.SECONDS_IN_A_MINUTE));
	}

	private Boolean doesInterviewTimeLieBetweenNonOperationalHrs(final int adjustedTimeInHour,
			final int interviewTimeInHour, final int nonOperationalHourStart, final int nonOperationalHourEnd) {
		return (adjustedTimeInHour >= nonOperationalHourEnd
				&& adjustedTimeInHour < nonOperationalHourStart
				&& (interviewTimeInHour >= nonOperationalHourStart || interviewTimeInHour < nonOperationalHourEnd));
	}

	private Boolean doNonOperationalHrsLieBetweenInterviewTimeAndAdjustedTime(final int adjustedTimeInHour,
			final int interviewTimeInHour, final int nonOperationalHourStart, final int nonOperationalHourEnd) {
		return (adjustedTimeInHour < nonOperationalHourStart
				&& interviewTimeInHour >= nonOperationalHourEnd && adjustedTimeInHour > interviewTimeInHour);
	}

	private Boolean doesAdjustedTimeInHourAndInterviewTimeInHourLieInNonOpHrs(final int adjustedTimeInHour,
			final int interviewTimeInHour, final int nonOperationalHourStart, final int nonOperationalHourEnd) {
		return ((adjustedTimeInHour >= nonOperationalHourStart
				|| adjustedTimeInHour < nonOperationalHourEnd)
				&& (interviewTimeInHour >= nonOperationalHourStart || interviewTimeInHour < nonOperationalHourEnd));
	}

	private Boolean doesAdjustedTimeInHourLieInNonOpHrs(final int adjustedTimeInHour, final int interviewTimeInHour,
			final int nonOperationalHourStart, final int nonOperationalHourEnd) {
		return ((adjustedTimeInHour >= nonOperationalHourStart
				|| adjustedTimeInHour < nonOperationalHourEnd)
				&& (interviewTimeInHour >= nonOperationalHourEnd && interviewTimeInHour < nonOperationalHourStart));
	}

	private int findTimeInMinutesFromEpoch(final long timeInEpoch, final String timezone) {
		final Calendar instanceFromEpoch = this.getCalendarInstanceFromEpoch(timeInEpoch, timezone);
		return instanceFromEpoch.get(Calendar.MINUTE);
	}

	private int findTimeInHourFromEpoch(final long timeInEpoch, final String timezone) {
		final Calendar instanceFromEpoch = this.getCalendarInstanceFromEpoch(timeInEpoch, timezone);
		return instanceFromEpoch.get(Calendar.HOUR_OF_DAY);
	}

	private Calendar getCalendarInstanceFromEpoch(long epochTime, final String timezone) {
		Calendar calendarInstance = Calendar.getInstance();
		calendarInstance.setTimeInMillis(epochTime * 1000L);
		calendarInstance.setTimeZone(TimeZone.getTimeZone(timezone));
		return calendarInstance;
	}
}
