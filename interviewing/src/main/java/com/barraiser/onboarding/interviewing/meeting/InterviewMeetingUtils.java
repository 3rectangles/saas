/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interviewing.meeting;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class InterviewMeetingUtils {
	public static String GOOGLE_MEET_LINK_PREFIX = "meet.google.com";
	public static String ZOOM_MEETING_LINK_PREFIX = "zoom.us";
	public static String TEAMS_MEETING_LINK_PREFIX = "teams.";

	public static MeetingPlatform getMeetingPlatformFromURL(final String url) {
		if (url.contains(GOOGLE_MEET_LINK_PREFIX)) {
			return MeetingPlatform.GOOGLE_MEET;
		} else if (url.contains(ZOOM_MEETING_LINK_PREFIX)) {
			return MeetingPlatform.ZOOM;
		} else if (url.contains(TEAMS_MEETING_LINK_PREFIX)) {
			return MeetingPlatform.MICROSOFT_TEAMS;
		}
		throw new IllegalArgumentException("no meeting platform supported for the given url");
	}
}
