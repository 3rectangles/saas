/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.PartnerCompanyDAO;
import com.barraiser.onboarding.dal.PartnerCompanyRepository;
import com.barraiser.onboarding.interview.InterviewUtil;
import com.barraiser.onboarding.scheduling.dto.InterviewDetailsRequestDTO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.barraiser.common.constants.ServiceConfigurationConstants.SCHEDULING_CONTEXT_PATH;
import static com.barraiser.common.constants.ServiceConfigurationConstants.INTERVIEWING_SERVICE_CONTEXT_PATH;

@Log4j2
@RestController
@AllArgsConstructor
public class MeetingInfoManagementController {
	private final InterviewUtil interviewUtil;
	private final PartnerCompanyRepository partnerCompanyRepository;

	/*
	 * Would only work for Zoom meeting IDs
	 */
	@GetMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/meeting/partnershipModel")
	ResponseEntity<MeetingInfoDTO> getPartnershipModelId(@RequestParam("meetingId") final String meetingId) {

		InterviewDAO interviewDAO = this.interviewUtil.getInterviewFromZoomMeetingId(meetingId);
		if (interviewDAO == null) {
			return ResponseEntity.badRequest().build();
		}
		PartnerCompanyDAO partnerCompanyDAO = this.partnerCompanyRepository.findById(interviewDAO.getPartnerId()).get();

		return ResponseEntity.ok().body(MeetingInfoDTO.builder().partnerId(interviewDAO.getPartnerId())
				.partnershipModelId(partnerCompanyDAO.getPartnershipModelId()).build());
	}

	@PostMapping(value = SCHEDULING_CONTEXT_PATH + "/meeting-instance/entity")
	public String postMeetingIdFromMeetingUrlInstance(@RequestBody InterviewDetailsRequestDTO meetingRequestDTO) {
		final InterviewDAO interviewDAO = interviewUtil
				.getMeetingIdFromMeetingURL(meetingRequestDTO.getMeetingUrl());
		if (interviewDAO == null) {
			return null;
		}
		return interviewDAO.getId();
	}

}
