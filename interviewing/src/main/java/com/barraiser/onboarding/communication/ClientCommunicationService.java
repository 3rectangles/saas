/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.communication;

import com.barraiser.onboarding.candidate.CandidateInformationManager;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.barraiser.onboarding.communication.channels.email.EmailService;
import com.barraiser.onboarding.dal.CandidateDAO;
import com.barraiser.onboarding.dal.EvaluationDAO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class ClientCommunicationService {

	private static final String SCHEDULED_INTERVIEW_SUBJECT = "Evaluation has been done";
	private final CandidateInformationManager candidateInformationManager;
	private final EmailService emailService;
	private final static String emailTemplate = "evaluation_done_mail_to_client";
	private final StaticAppConfigValues staticAppConfigValues;

	public void sendEvaluationDoneMailToClient(final EvaluationDAO evaluation) {

		final Map<String, String> emailDataMap = this.constructEmailData(evaluation);

		final String fromEmail = this.staticAppConfigValues.getInterviewLifecycleInformationEmail();

		final List<String> toEmail = Arrays.stream(evaluation.getPocEmail().split(","))
				.map(String::trim).collect(Collectors.toList());

		final List<String> ccEmail = new ArrayList<>();
		ccEmail.add(fromEmail);

		try {
			this.emailService.sendEmail(fromEmail, SCHEDULED_INTERVIEW_SUBJECT, emailTemplate, toEmail, ccEmail,
					emailDataMap, null);
		} catch (final Exception e) {
			throw new RuntimeException("Error in sending evaluation done mail to client");
		}
	}

	public Map<String, String> constructEmailData(final EvaluationDAO evaluation) {
		final Map<String, String> emailDataMap = new HashMap<>();
		final CandidateDAO candidateDetails = this.candidateInformationManager
				.getCandidate(evaluation.getCandidateId());
		final String firstName = candidateDetails.getFirstName() == null ? "" : candidateDetails.getFirstName();
		final String lastName = candidateDetails.getLastName() == null ? "" : candidateDetails.getLastName();
		emailDataMap.put("userName", firstName + " " + lastName);
		emailDataMap.put("bgsLink", "https://app.barraiser.com/candidate-evaluation/" + evaluation.getId());

		return emailDataMap;
	}
}
