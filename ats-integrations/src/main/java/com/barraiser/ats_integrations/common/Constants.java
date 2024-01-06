/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.common;

import java.util.List;

public class Constants {
	// TODO: Capture a list of supported ATS, block user from selecting others by
	// informing they are not supported
	public static final List<String> ATS_SCHEDULING_LINK_REGEX_LIST = List.of(
			".*(https://.*/interviews/.*/feedback).*",
			".*(https://.*/interviews/).*",
			".*(https://.*\\/guides\\/([_a-zA-Z0-9]*)\\/people\\/([_a-zA-Z0-9]*)\\/interview\\?application_id=([_a-zA-Z0-9]*)).*");

	public static final String SAAS_TRIAL_PARTNERSHIP_MODEL_ID = "saas_trial";
}
