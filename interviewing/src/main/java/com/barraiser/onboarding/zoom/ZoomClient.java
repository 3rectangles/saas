/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.zoom;

import com.barraiser.onboarding.config.ZoomClientConfig;
import com.barraiser.onboarding.zoom.dto.ZoomMeetingDTO;
import com.barraiser.onboarding.zoom.dto.ZoomMeetingInstancesDTO;
import com.barraiser.onboarding.zoom.dto.ZoomRecordingsDTO;
import com.barraiser.onboarding.zoom.dto.ZoomUpdateStatusDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "zoom-client", url = "https://api.zoom.us/v2", configuration = ZoomClientConfig.class)
public interface ZoomClient {
	@PostMapping("/users/{userId}/meetings")
	ZoomMeetingDTO createMeeting(@PathVariable("userId") String userId, @RequestBody ZoomMeetingDTO meeting);

	@DeleteMapping("/meetings/{meetingId}")
	void deleteMeeting(@PathVariable("meetingId") String meetingId);

	@PutMapping("/meetings/{meetingId}/status")
	void updateStatus(@PathVariable("meetingId") String meetingId, @RequestBody ZoomUpdateStatusDTO status);

	@GetMapping("/meetings/{meetingId}")
	ZoomMeetingDTO getZoomMeeting(@PathVariable("meetingId") String meetingId);

	// This will return meeting instances in which there was atleast 1 non host
	// participant
	@GetMapping("/past_meetings/{meetingId}/instances")
	ZoomMeetingInstancesDTO getZoomMeetingInstances(@PathVariable("meetingId") String meetingId);

	@GetMapping("/meetings/{meetingId}/recordings")
	ZoomRecordingsDTO getZoomRecordings(@PathVariable("meetingId") String meetingId);

	@GetMapping("/metrics/meetings/{meetingUuid}")
	ZoomMeetingDTO getMeetingFromInstance(@PathVariable("meetingUuid") String meetingId);
}
