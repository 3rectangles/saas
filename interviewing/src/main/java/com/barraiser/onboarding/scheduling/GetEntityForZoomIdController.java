/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.interview.InterviewUtil;
import com.barraiser.onboarding.zoom.ZoomClient;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

import static com.barraiser.common.constants.ServiceConfigurationConstants.SCHEDULING_CONTEXT_PATH;

@Log4j2
@RestController
@AllArgsConstructor
public class GetEntityForZoomIdController {
	private final ZoomClient zoomClient;
	private final InterviewUtil interviewUtil;

	@GetMapping(value = SCHEDULING_CONTEXT_PATH + "/zoom-instance/entity")
	public String getMeetingIdFromInstance(@RequestParam("meetingUuid") final String meetingUuid) {
		final String meetingId = this.zoomClient.getMeetingFromInstance(meetingUuid).getMeetingId().toString();
		final InterviewDAO interviewDAO = interviewUtil.getInterviewFromZoomMeetingId(meetingId);
		if (interviewDAO == null) {
			return null;
		}
		return interviewDAO.getId();
	}

	@GetMapping(value = SCHEDULING_CONTEXT_PATH + "/zoom/{meetingId}/entity")
	public String createEvent(@PathVariable("meetingId") final String meetingId) {
		final InterviewDAO interviewDAO = interviewUtil.getInterviewFromZoomMeetingId(meetingId);
		if (interviewDAO == null) {
			return null;
		}
		return interviewDAO.getId();
	}
}
