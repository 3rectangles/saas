/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.candidate.CandidateInformationManager;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.barraiser.onboarding.communication.channels.email.EmailService;
import com.barraiser.onboarding.dal.CandidateDAO;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
class InterviewNotificationService {
	private final EmailService emailService;
	private final InterViewRepository interViewRepository;
	private final CandidateInformationManager candidateInformationManager;
	private final UserDetailsRepository userDetailsRepository;
	private final StaticAppConfigValues staticAppConfigValues;

	void sendFeedbackCompletedEmail(final String interviewId) throws IOException {
		final InterviewDAO interview = this.interViewRepository.findById(interviewId).orElseThrow();

		final UserDetailsDAO interviewer = this.userDetailsRepository.findById(interview.getInterviewerId())
				.orElseThrow(() -> new IllegalStateException("No interviewer found for the interview."));
		final CandidateDAO interviewee = this.candidateInformationManager.getCandidate(interview.getIntervieweeId());

		if (interviewee == null) {
			throw new IllegalArgumentException("No candidate found for the interview.");
		}

		final String subject = String.format("%s has submitted feedback for interview %s", interviewer.getFirstName(),
				interviewId);

		final Map<String, String> data = new HashMap<>();
		data.put("interviewId", interviewId);
		data.put("interviewerFirstName", interviewer.getFirstName());
		data.put("interviewerLastName", interviewer.getLastName());
		data.put("candidateFirstName", interviewee.getFirstName());
		data.put("candidateLastName", interviewee.getLastName());

		this.emailService.sendEmail(this.staticAppConfigValues.getInterviewNotificationEmail(), subject,
				"interview_feedback_submitted", data, null);
	}

}
