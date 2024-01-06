/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.utilities;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Component
@AllArgsConstructor
@Log4j2
public class DateUtils {
	public static final String DATE_IN_YYYY_MM_DD_FORMAT = "yyyy-MM-dd";
	public static final Integer MINUTES_IN_ONE_HOUR = 60;
	public static final String TIMEZONE_ASIA_KOLKATA = "Asia/Kolkata";
	public static final String TIME_IN_12_HOUR_FORMAT = "dd MMM uuuu, hh:mm a z";
	public static final String DATEFORMAT_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final String ZOOM_TRANSCRIPT_TIME_FORMAT = "HH:mm:ss.SSS";
	public static final String TIMEZONE_UTC = "UTC";
	public static final String IST_TIMEZONE_OFFSET = "+05:30";
	public static final String TIME_IN_12_HOUR_FORMAT_WITH_SLASH = "dd/MMM/uuuu hh:mm a z";

	public Long getTimeStringInMillis(final String timeString, final String format) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(String.format("%s %s", DATE_IN_YYYY_MM_DD_FORMAT, format));
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

			return sdf.parse("1970-01-01 " + timeString).getTime();
		} catch (final Exception e) {
			log.error("could not parse time : " + timeString);
			throw new IllegalArgumentException(e);
		}
	}

	public Boolean isTimeMoreThanNminutesFromScheduledTime(final Long time, final Integer n, final Long scheduledTime) {
		return scheduledTime - time >= n * 60;
	}

	public Long convertDateTimeToEpoch(final Instant dateTime) {
		return ChronoUnit.SECONDS.between(Instant.EPOCH, dateTime);
	}

	public String getFormattedDateString(
			final Long epoch, final String timezone, final String formatString) {
		final String timezone1 = timezone != null ? timezone : TIMEZONE_ASIA_KOLKATA;
		final ZoneId zoneId = ZoneId.of(timezone1);
		final OffsetDateTime interviewStartTime = OffsetDateTime.ofInstant(Instant.ofEpochSecond(epoch), zoneId);
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatString).withZone(zoneId);

		return formatter.format(interviewStartTime);
	}

	public Long convertDateToEpoch(final String dateToFormat, final String dateFormat, final String timezone) {
		try {
			final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
			simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
			final Date date = simpleDateFormat.parse(dateToFormat);
			return date.getTime() / 1000;
		} catch (ParseException e) {
			throw new IllegalArgumentException("Badly formatted date");
		}
	}

	public static String getISO8601DateStringWithOffset(final Long epoch, final String timezone) {
		final String timezoneTemp = timezone != null ? timezone : TIMEZONE_ASIA_KOLKATA;
		final ZoneId zoneId = ZoneId.of(timezoneTemp);
		final OffsetDateTime offsetDateTime = OffsetDateTime.ofInstant(Instant.ofEpochSecond(epoch), zoneId);
		return offsetDateTime.toString();
	}

	public OffsetDateTime getDateTime(final Long epochInSeconds, final String timezone) {
		final ZoneId zoneId = ZoneId.of(timezone);
		return OffsetDateTime.ofInstant(Instant.ofEpochSecond(epochInSeconds), zoneId);
	}

	public Long getEpochTo15ThMinuteCeil(final Long dateTimeEpoch) {
		return dateTimeEpoch % 900 != 0
				? dateTimeEpoch + (900 - (dateTimeEpoch % 900))
				: dateTimeEpoch;
	}

	public Long convertHHMMSSToSeconds(String timeString) {
		String[] units = timeString.split(":");
		Integer hours = Integer.parseInt(units[0]);
		Integer minutes = Integer.parseInt(units[1]);
		Integer seconds = Integer.parseInt(units[2]);
		return Long.valueOf(3600 * hours + 60 * minutes + seconds);
	}

	public String getHumanReadableDuration(final Long milliseconds) {
		return DurationFormatUtils.formatDurationWords(milliseconds, true, true);
	}

	public Long getStartOfDayEpochSecond(final Long timeInSeconds, final String timeZone) {
		final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
		calendar.setTimeInMillis(timeInSeconds * 1000);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis() / 1000;
	}

	public boolean isDateInBetween(Instant startDate, Instant endDate, final Instant date) {
		startDate = startDate == null ? Instant.EPOCH : startDate;
		endDate = endDate == null ? Instant.MAX : endDate;
		return date.isAfter(startDate) && date.isBefore(endDate);
	}

	public Long getDateStringInEpoch(final String dateString, final String format) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(String.format("%s %s", format, "HH:mm:ss.SSS"));
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

			return sdf.parse(dateString + " 00:00:00.000").getTime() / 1000;
		} catch (final Exception e) {
			log.error("could not parse time : " + dateString);
			throw new IllegalArgumentException(e);
		}
	}

	public static Long getDateTimeStringInEpoch(final String dateString, final String format) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			sdf.setTimeZone(TimeZone.getTimeZone(TIMEZONE_UTC));

			return sdf.parse(dateString).getTime() / 1000;
		} catch (final Exception e) {
			log.error("could not parse time : " + dateString);
			throw new IllegalArgumentException(e);
		}
	}

	// finds if the time passed is between the given from time and to time
	public boolean isBetweenTimeOfDay(final Long time, Long fromTime, Long toTime, final String timezone) {
		final String actualTimezone = timezone == null ? DateUtils.TIMEZONE_ASIA_KOLKATA : timezone;
		final Long startOfDay = this.getStartOfDayEpochSecond(time, actualTimezone);
		fromTime += startOfDay;
		toTime += startOfDay;
		return time >= fromTime || time < toTime;
	}
}
