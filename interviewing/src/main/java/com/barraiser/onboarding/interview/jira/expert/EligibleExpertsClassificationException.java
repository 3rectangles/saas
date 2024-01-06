/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jira.expert;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@AllArgsConstructor
@Component
public class EligibleExpertsClassificationException {
	public static final String PARTNER_ID_FOR_RECRO = "dc0b3a40-e30f-4832-b1d0-c6264a344e29";

	public Boolean includeWorkExperienceFilterForScheduling(final String partnerId) {
		if (PARTNER_ID_FOR_RECRO.equals(partnerId)) {
			return true;
		}
		return false;
	}
}
