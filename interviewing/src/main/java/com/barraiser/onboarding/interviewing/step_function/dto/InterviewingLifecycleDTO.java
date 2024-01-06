/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interviewing.step_function.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class InterviewingLifecycleDTO {
	private String interviewId;
	private String waitTillInterviewStartTimestamp;
	private TriggerBotData triggerBotData;

	@Data
	@Builder(toBuilder = true)
	public static class TriggerBotData {
		private String meetingURL;
		private String meetingPasscode;
		private String duration;
		private String zoomMeetingUrl;
		private String zoomPasscode;
		private Boolean isRecordingConsentNeeded;
		private Long interviewScheduledTime;
		private String partnerId;
	}
}
