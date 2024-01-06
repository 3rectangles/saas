/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user;

import com.barraiser.onboarding.candidate.CandidateInformationManager;
import com.barraiser.onboarding.dal.CandidateDAO;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.jobrole.JobRoleManager;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class TimezoneManager {
	private final InterViewRepository interViewRepository;
	private CandidateInformationManager candidateInformationManager;
	private final UserDetailsRepository userDetailsRepository;
	private final JobRoleManager jobRoleManager;

	public String getTimezoneOfCandidate(final String interviewId) {
		final InterviewDAO interviewDAO = this.interViewRepository.findById(interviewId).get();
		if (interviewDAO.getIntervieweeTimezone() != null) {
			return interviewDAO.getIntervieweeTimezone();
		}

		final CandidateDAO candidate = this.candidateInformationManager.getCandidate(interviewDAO.getIntervieweeId());
		final String userTimezone = candidate.getTimezone();
		if (userTimezone != null) {
			return userTimezone;
		}
		return this.jobRoleManager.getJobRoleFromEvaluation(interviewDAO.getEvaluationId()).get().getTimezone();
	}

	public String getTimezoneOfExpert(final String expertId) {
		return this.userDetailsRepository.findById(expertId).get().getTimezone();
	}
}
