/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.webex;

import com.barraiser.onboarding.webex.dto.CreateWebexMeetingRequestDTO;
import com.barraiser.onboarding.webex.dto.CreateWebexMeetingResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@Log4j2
@RequiredArgsConstructor
public class WebexMeetingManager {
	private final WebexClient webexClient;
	private final WebexAccessTokenGenerator accessTokenGenerator;

	public CreateWebexMeetingResponseDTO scheduleMeeting(final Long startTime, final Long endTime, final String topic) {
		final String startTimeFormatted = Instant.ofEpochSecond(startTime).truncatedTo(ChronoUnit.SECONDS).toString();
		final String endTimeFormatted = Instant.ofEpochSecond(endTime).truncatedTo(ChronoUnit.SECONDS).toString();

		final CreateWebexMeetingRequestDTO request = CreateWebexMeetingRequestDTO.builder()
				.title(topic)
				.start(startTimeFormatted)
				.end(endTimeFormatted)
				.enableConnectAudioBeforeHost(true)
				.enabledJoinBeforeHost(true)
				.joinBeforeHostMinutes(15)
				.enabledAutoRecordMeeting(true)
				.password("br@123")
				.build();

		return this.webexClient.createMeeting(request, this.accessTokenGenerator.getAdminAuthHeader());
	}

}
