/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.enums;

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

}
