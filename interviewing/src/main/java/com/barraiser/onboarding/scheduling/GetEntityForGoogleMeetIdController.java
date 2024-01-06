/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.interview.InterviewUtil;
import com.barraiser.onboarding.scheduling.dto.GoogleMeetRequestDTO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.barraiser.common.constants.ServiceConfigurationConstants.SCHEDULING_CONTEXT_PATH;

@Log4j2
@RestController
@AllArgsConstructor
public class GetEntityForGoogleMeetIdController {

	private final InterviewUtil interviewUtil;

	@PostMapping(value = SCHEDULING_CONTEXT_PATH + "/google-meet-instance/entity")
	public String postMeetingIdFromGoogleMeetInstance(@RequestBody GoogleMeetRequestDTO googleMeetRequestDTO) {
		final InterviewDAO interviewDAO = this.interviewUtil
				.getMeetingIdFromMeetingURL(googleMeetRequestDTO.getGoogleMeetUrl());
		if (interviewDAO == null) {
			return null;
		}
		return interviewDAO.getId();
	}

}
