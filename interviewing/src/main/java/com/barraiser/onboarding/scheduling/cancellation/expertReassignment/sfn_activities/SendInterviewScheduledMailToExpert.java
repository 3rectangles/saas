/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment.sfn_activities;

import com.barraiser.onboarding.communication.InterviewReassignedToExpertCommunicationService;
import com.barraiser.onboarding.communication.InterviewSchedulingCommunicationService;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO.ExpertAllocatorData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Log4j2
@Component
@AllArgsConstructor
public class SendInterviewScheduledMailToExpert implements ExpertAllocatorSfnActivity {
	public static final String SEND_INTERVIEW_SCHEDULED_MAIL_TO_EXPERT_ACTIVITY_NAME = "send-allocation-mail-to-expert";

	private final InterviewReassignedToExpertCommunicationService interviewReassignedToExpertCommunicationService;
	private InterviewSchedulingCommunicationService interviewSchedulingCommunicationService;
	private final ObjectMapper objectMapper;

	@Override
	public String name() {
		return SEND_INTERVIEW_SCHEDULED_MAIL_TO_EXPERT_ACTIVITY_NAME;
	}

	@Override
	public ExpertAllocatorData process(final String input) throws IOException {
		final ExpertAllocatorData data = objectMapper.readValue(input, ExpertAllocatorData.class);
		if (Boolean.FALSE.equals(data.getIsOnlyCandidateChanged())) {
			this.interviewSchedulingCommunicationService.sendEmailToExpert(data.getSchedulingCommunicationData());
		} else {
			this.interviewReassignedToExpertCommunicationService.communicateInterviewUpdationToExpert(
					data.getInterview(),
					data.getInterviewerId());
		}
		return data;
	}
}
