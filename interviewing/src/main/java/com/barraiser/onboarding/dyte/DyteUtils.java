/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dyte;

import com.barraiser.common.utilities.DateUtils;

public class DyteUtils {
	private final static String DATE_FORMAT = "yyyy-MM-dd' 'HH:mm:ss";

	public final static String RECORDING_STATUS_UPLOADING = "UPLOADING";

	public final static String RECORDING_STATUS_UPLOADED = "UPLOADED";

	public static final String DYTE_EXPERT_PRESET = "expert_preset";

	public static final String DYTE_EXPERT_PARTICIPANT_NAME = "Interviewer";

	public static Long getEpochFromDateTimeString(final String dateTime) {
		return DateUtils.getDateTimeStringInEpoch(dateTime.substring(0, 19), DATE_FORMAT);
	}
}
