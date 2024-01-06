/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.webex.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateWebexMeetingRequestDTO {

	private String title;

	private String start;

	private String end;

	private Boolean enabledJoinBeforeHost;

	private Boolean enabledAutoRecordMeeting;

	private Boolean enableConnectAudioBeforeHost;

	private Integer joinBeforeHostMinutes;

	private String password;
}
