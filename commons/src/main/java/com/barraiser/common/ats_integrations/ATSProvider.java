/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.ats_integrations;

import java.util.NoSuchElementException;

/**
 * TODO : Moved to commons , eliminate
 */
public enum ATSProvider {
	LEVER("LEVER"), GREENHOUSE("GREENHOUSE"), MERGE("MERGE"), SMART_RECRUITERS("SMART_RECRUITERS"), SKILLATE(
			"SKILLATE"), MERGE_GREENHOUSE("MERGE_greenhouse"), MERGE_LEVER("MERGE_lever"), MERGE_ASHBY(
					"MERGE_ashby"), MERGE_BREEZY("MERGE_breezy"), MERGE_JOBVITE("MERGE_jobvite");

	private final String atsProvider;

	public static ATSProvider fromString(String ats) {
		for (ATSProvider atsProvider : values()) {
			if (atsProvider.getValue().equals(ats)) {
				return atsProvider;
			}
		}
		throw new NoSuchElementException("Element with value " + ats + " has not been found");
	}

	ATSProvider(final String atsProvider) {
		this.atsProvider = atsProvider;
	}

	public String getValue() {
		return this.atsProvider;
	}
}
