/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling;

import com.barraiser.onboarding.candidate.CandidateInformationManager;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.GetInterviewers;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewStructureManager;
import com.barraiser.onboarding.interview.jobrole.JobRoleManager;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.MatchInterviewersData;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class MatchInterviewersDataHelper {
	private final String RESPONSE_ERROR = "Contact BarRaiser Team";
	private final InterViewRepository interviewRepository;
	private final EvaluationRepository evaluationRepository;
	private final JobRoleManager jobRoleManager;
	private final CandidateInformationManager candidateInformationManager;
	private final GetInterviewers getInterviewers;
	private final InterviewStructureRepository interviewStructureRepository;
	private final InterviewStructureManager interviewStructureManager;
	private final PartnerCompanyRepository partnerCompanyRepository;

	public MatchInterviewersData prepareDataForInterviewSlots(final String interviewId) {
		final Optional<InterviewDAO> interview = this.interviewRepository.findById(interviewId);
		if (interview.isEmpty()) {
			throw new IllegalArgumentException(this.RESPONSE_ERROR);
		}
		if (interview.get().getEvaluationId() == null) {
			throw new IllegalArgumentException(this.RESPONSE_ERROR);
		}
		final Optional<EvaluationDAO> evaluationDAO = this.evaluationRepository
				.findById(interview.get().getEvaluationId());

		if (evaluationDAO.isEmpty() || evaluationDAO.get().getJobRoleId() == null) {
			throw new IllegalArgumentException(this.RESPONSE_ERROR);
		}

		final String jobRoleId = evaluationDAO.get().getJobRoleId();
		final Integer jobRoleVersion = evaluationDAO.get().getJobRoleVersion();
		final Optional<JobRoleDAO> jobRoleDAO = this.jobRoleManager.getJobRole(jobRoleId, jobRoleVersion);

		if (jobRoleDAO.isEmpty()) {
			throw new IllegalArgumentException(this.RESPONSE_ERROR);
		}

		if (jobRoleDAO.get().getEntityId() == null || interview.get().getInterviewRound() == null) {
			throw new IllegalArgumentException(this.RESPONSE_ERROR);
		}

		final String domainId = this.getInterviewers.getDomainOfInterview(interview);

		if (jobRoleDAO.get().getCompanyId() == null
				|| domainId == null
				|| domainId.isEmpty()
				|| jobRoleDAO.get().getCategory() == null) {
			throw new IllegalArgumentException(this.RESPONSE_ERROR);
		}

		final Optional<InterviewStructureDAO> interviewStructureDAO = this.interviewStructureRepository.findById(
				interview.get().getInterviewStructureId());
		if (interviewStructureDAO.isEmpty()
				|| !Boolean.TRUE.equals(interviewStructureDAO.get().getAllSkillsFound())) {
			throw new IllegalArgumentException(this.RESPONSE_ERROR);
		}
		final CandidateDAO candidateDAO = this.candidateInformationManager
				.getCandidate(interview.get().getIntervieweeId());

		return MatchInterviewersData.builder()
				.domainId(domainId)
				.interviewRound(interview.get().getInterviewRound())
				.workExperienceOfIntervieweeInMonths(candidateDAO.getWorkExperienceInMonths())
				.hiringCompanyId(jobRoleDAO.get().getCompanyId())
				.evaluationId(evaluationDAO.get().getId())
				.interviewStructureId(interviewStructureDAO.get().getId())
				.category(jobRoleDAO.get().getCategory())
				.expertJoiningTime(
						this.interviewStructureManager.getExpertJoiningTime(
								interviewStructureDAO.get().getId()))
				.interviewId(interview.get().getId())
				.durationOfInterview(interviewStructureDAO.get().getDuration().longValue())
				.candidateId(candidateDAO.getId())
				.interview(interview.get())
				.eligibleCountriesForExperts(
						jobRoleDAO.get().getEligibleCountriesOfExperts())
				.partnerCompanyId(this.partnerCompanyRepository
						.findByCompanyId(jobRoleDAO.get().getCompanyId())
						.get()
						.getId())
				.jobRoleId(jobRoleId)
				.rescheduleCount(interview.get().getRescheduleCount())
				.build();
	}
}
