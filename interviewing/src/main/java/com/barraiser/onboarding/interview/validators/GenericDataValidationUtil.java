/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.validators;

import com.barraiser.onboarding.candidate.CandidateInformationManager;
import com.barraiser.onboarding.dal.CandidateDAO;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import com.barraiser.onboarding.interview.InterViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * Validations performed on data across various
 * components is done here
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class GenericDataValidationUtil {

	private final InterViewRepository interViewRepository;
	private final CandidateInformationManager candidateInformationManager;
	private final UserDetailsRepository userDetailsRepository;

	public String validateInterviewInformation(final String interviewId) {
		final StringBuilder errorStringBuilder = new StringBuilder();

		final InterviewDAO interviewDAO = this.interViewRepository.findById(interviewId)
				.orElseThrow(() -> new IllegalArgumentException("No interview found with the id: " + interviewId));

		if (interviewDAO.getStatus() == null || interviewDAO.getStatus().isEmpty()) {
			errorStringBuilder.append("Interview status is null or empty");
		}

		if (interviewDAO.getStartDate() == null) {
			errorStringBuilder.append("Interview start date  is null or empty");
		}

		if (interviewDAO.getInterviewerId() == null) {
			errorStringBuilder.append("Interviewer is null or empty");
		} else {

			if (this.userDetailsRepository.findById(interviewDAO.getInterviewerId()).isEmpty()) {
				errorStringBuilder.append("No such interviewer in user details table");
			} else {
				final UserDetailsDAO interviewer = this.userDetailsRepository.findById(interviewDAO.getInterviewerId())
						.get();

				if (interviewer.getFirstName() == null || interviewer.getFirstName().isEmpty()) {
					errorStringBuilder.append("Interviewer first name is null or empty");
				}

				if (interviewer.getEmail() == null || interviewer.getEmail().isEmpty()) {
					errorStringBuilder.append("Interviewer email is null or empty");
				}

			}
		}

		// Scope for reusability
		if (interviewDAO.getIntervieweeId() == null) {
			errorStringBuilder.append("Candidate  is null or empty");
		} else {
			final CandidateDAO candidate = this.candidateInformationManager
					.getCandidate(interviewDAO.getIntervieweeId());
			if (candidate == null) {
				errorStringBuilder.append("No such candidate present.");
			} else {
				if (candidate.getFirstName() == null || candidate.getFirstName().isEmpty()) {
					errorStringBuilder.append("Candidate first name is null or empty");
				}
			}
		}
		return errorStringBuilder.toString();
	}
}
