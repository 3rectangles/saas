/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dyte;

import com.barraiser.onboarding.config.JiraConfig;
import com.barraiser.onboarding.dyte.dto.DyteAddParticipantRequestDTO;
import com.barraiser.onboarding.dyte.dto.DyteAddParticipantResponseDTO;
import com.barraiser.onboarding.dyte.dto.DyteCreateMeetingRequestDTO;
import com.barraiser.onboarding.dyte.dto.DyteCreateMeetingResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "dyte-client", url = "https://api.cluster.dyte.in/v1/", configuration = DyteClientConfig.class)
public interface DyteClient {

	@PostMapping("/organizations/{organizationId}/meeting")
	DyteCreateMeetingResponseDTO createMeeting(@PathVariable("organizationId") String organizationId,
			@RequestBody DyteCreateMeetingRequestDTO request);

	@PostMapping("/organizations/{organizationId}/meetings/{meetingId}/participant")
	DyteAddParticipantResponseDTO addParticipant(@PathVariable("organizationId") String organizationId,
			@PathVariable("meetingId") String meetingId, @RequestBody DyteAddParticipantRequestDTO request);
}
