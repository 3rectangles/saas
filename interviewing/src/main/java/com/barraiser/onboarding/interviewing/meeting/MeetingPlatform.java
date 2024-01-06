/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interviewing.meeting;

import java.util.NoSuchElementException;

public enum MeetingPlatform {

	ZOOM("ZOOM"),

	GOOGLE_MEET("GOOGLE_MEET"),

	MICROSOFT_TEAMS("MICROSOFT_TEAMS");

	private final String meetingPlatform;

	MeetingPlatform(final String meetingPlatform) {
		this.meetingPlatform = meetingPlatform;
	}

	public String getValue() {
		return this.meetingPlatform;
	}

	public static MeetingPlatform fromString(String meetingPlatform) {
		for (MeetingPlatform mt : values()) {
			if (mt.getValue().equals(meetingPlatform)) {
				return mt;
			}
		}
		throw new NoSuchElementException("Element with value " + meetingPlatform + " has not been found");
	}
}
