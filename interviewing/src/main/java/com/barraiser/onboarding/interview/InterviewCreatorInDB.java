/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.dal.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Component
public class InterviewCreatorInDB {

	private final InterviewUtil interviewUtil;
	private final JobRoleToInterviewStructureRepository jobRoleToInterviewStructureRepository;
	private final InterviewService interviewService;
	private final PartnerConfigManager partnerConfigManager;
	private final EvaluationRepository evaluationRepository;
	private final InterviewStructureManager interviewStructureManager;

	public List<InterviewDAO> createInterviewInDatabase(final EvaluationDAO evaluationDAO, final int orderIndex,
			final String createdBy) {
		final List<JobRoleToInterviewStructureDAO> jobRoleToInterviewStructureDAOs = this.jobRoleToInterviewStructureRepository
				.findByJobRoleIdAndJobRoleVersionAndOrderIndexGreaterThanOrderByOrderIndexAsc(
						evaluationDAO.getJobRoleId(), evaluationDAO.getJobRoleVersion(), orderIndex);
		final List<InterviewDAO> interviewDAOS = new ArrayList<>();
		for (final JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO : jobRoleToInterviewStructureDAOs) {
			if (this.interviewUtil.doesRoundExists(evaluationDAO.getId(),
					jobRoleToInterviewStructureDAO.getInterviewStructureId())) {
				break;
			}
			interviewDAOS.add(this.createAndSaveInterview(jobRoleToInterviewStructureDAO, evaluationDAO, createdBy));
			if (this.interviewUtil.isNextRoundCreationDependent(jobRoleToInterviewStructureDAO.getJobRoleId(),
					jobRoleToInterviewStructureDAO.getJobRoleVersion(),
					jobRoleToInterviewStructureDAO.getInterviewStructureId())) {
				break;
			}
		}
		return interviewDAOS;
	}

	private InterviewDAO createAndSaveInterview(final JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO,
			final EvaluationDAO evaluationDAO, final String createdBy) {
		InterviewDAO interviewDAO = InterviewDAO.builder()
				.id(UUID.randomUUID().toString())
				.intervieweeId(evaluationDAO.getCandidateId())
				.evaluationId(evaluationDAO.getId())
				.interviewRound(
						jobRoleToInterviewStructureDAO != null ? jobRoleToInterviewStructureDAO.getInterviewRound()
								: this.partnerConfigManager.getDefaultRoundTypeForPartner(evaluationDAO.getPartnerId()))
				.interviewStructureId(jobRoleToInterviewStructureDAO != null
						? jobRoleToInterviewStructureDAO.getInterviewStructureId()
						: null)
				.status(InterviewStatus.PENDING_SCHEDULING.getValue())
				.rescheduleCount(0)
				.isPendingScheduling(this.interviewUtil.checkIfInterviewIsPendingScheduling(evaluationDAO,
						InterviewStatus.PENDING_SCHEDULING.getValue()))
				.partnerId(this.partnerConfigManager.getPartnerIdFromCompanyId(evaluationDAO.getCompanyId()))
				.pocEmail(evaluationDAO.getPocEmail())
				.isTaggingAgentNeeded(this.interviewStructureManager
						.isTaggingAgentRequired(jobRoleToInterviewStructureDAO != null
								? jobRoleToInterviewStructureDAO.getInterviewStructureId()
								: null))
				.build();
		return this.interviewService.save(interviewDAO, createdBy);
	}

	public InterviewDAO createInterviewInDatabase(final String evaluationId, final String interviewStructureId,
			final String createdBy) {
		final EvaluationDAO evaluationDAO = this.evaluationRepository.findById(evaluationId).get();
		JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO = null;

		if (interviewStructureId != null) {
			jobRoleToInterviewStructureDAO = this.jobRoleToInterviewStructureRepository
					.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId(evaluationDAO.getJobRoleId(),
							evaluationDAO.getJobRoleVersion(), interviewStructureId)
					.get();
		}

		return this.createAndSaveInterview(jobRoleToInterviewStructureDAO, evaluationDAO, createdBy);
	}

}
