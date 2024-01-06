/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.template;

import com.github.jknack.handlebars.Options;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

public class HandlebarsHelperSource {
	public String formatEpochInSeconds(final Integer epoch, final String timezone) {
		if (epoch == null) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy hh:mm a z");
		sdf.setTimeZone(TimeZone.getTimeZone(ZoneId.of(timezone == null ? "Asia/Kolkata" : timezone)));
		return sdf.format(new Date(Long.parseLong(epoch.toString()) * 1000));
	}

	public String stringEquals(final String source, final String target) {
		return source.equals(target) ? "true" : null;
	}

	public String in(final Object source, final Options values) {
		for (final Object value : values.params) {
			if (value.equals(source)) {
				return "true";
			}
		}
		return null;
	}

	public long randomNumberToPreventEmailTrimming() {
		return Instant.now().toEpochMilli();
	}

	public void setVariable(String variableName, String variableValue, Options options) {
		options.data(variableName, variableValue);
	}

}
