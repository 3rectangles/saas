/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.sfn;

import com.barraiser.onboarding.communication.InterviewSchedulingCommunicationService;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingCommunicationData;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingProcessingData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class SendInterviewScheduleMailToCandidateActivity implements InterviewSchedulingActivity {
	public static final String SEND_INTERVIEW_SCHEDULED_MAIL_TO_CANDIDATE = "send-interview-scheduled-mail-to-candidate";

	private final InterviewSchedulingCommunicationService interviewSchedulingCommunicationService;
	private final ObjectMapper objectMapper;

	@Override
	public String name() {
		return SEND_INTERVIEW_SCHEDULED_MAIL_TO_CANDIDATE;
	}

	@Override
	public SchedulingProcessingData process(String input) throws Exception {
		final SchedulingProcessingData data = objectMapper.readValue(input, SchedulingProcessingData.class);
		// below code not required as this email is now sent using event with template
		// in db
		// final SchedulingCommunicationData schedulingEmailData =
		// data.getSchedulingCommunicationData();
		// this.interviewSchedulingCommunicationService
		// .sendEmailToCandidate(schedulingEmailData);
		return data;
	}
}
