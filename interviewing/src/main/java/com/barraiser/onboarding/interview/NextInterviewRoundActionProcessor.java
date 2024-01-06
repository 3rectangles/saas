/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.JobRoleToInterviewStructureDAO;
import com.barraiser.onboarding.dal.JobRoleToInterviewStructureRepository;
import com.barraiser.onboarding.interview.evaluation.scores.BgsScoreFetcher;
import com.barraiser.onboarding.interview.ruleEngine.InterviewRoundClearanceRuleChecker;
import com.barraiser.onboarding.interview.status.EvaluationStatusManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Log4j2
@Component
@RequiredArgsConstructor
public class NextInterviewRoundActionProcessor {

	private final InterviewApprovalProcessor interviewApprovalProcessor;
	private final BgsScoreFetcher bgsScoreFetcher;
	private final InterviewRejectionProcessor interviewRejectionProcessor;
	private final InterviewUtil interviewUtil;
	private final JobRoleToInterviewStructureRepository jobRoleToInterviewStructureRepository;
	private final InterviewRoundClearanceRuleChecker interviewRuleManager;

	public void takeActionForNextRound(final InterviewDAO interview) throws ParseException {
		final EvaluationDAO evaluationDAO = this.interviewUtil.getEvaluationForInterview(interview.getId());
		final Optional<JobRoleToInterviewStructureDAO> jobRoleToInterviewStructureDAO = this.jobRoleToInterviewStructureRepository
				.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId(
						evaluationDAO.getJobRoleId(),
						evaluationDAO.getJobRoleVersion(),
						interview.getInterviewStructureId());

		final Integer scoreInterview = this.bgsScoreFetcher.getBgsScoreForInterview(interview.getId());
		if (jobRoleToInterviewStructureDAO.get().getInterviewCutoffScore() != null) {
			if (scoreInterview < jobRoleToInterviewStructureDAO.get().getInterviewCutoffScore()) {
				this.autoRejectInterview(interview);
				return;
			}
		}

		if (jobRoleToInterviewStructureDAO.get().getCategoryRejectionJSON() != null
				&& !this.bgsScoreFetcher.isCategoryThresholdCleared(interview.getId(),
						jobRoleToInterviewStructureDAO.get().getCategoryRejectionJSON())) {
			this.autoRejectInterview(interview);
			return;
		}

		final int bgsScore = this.bgsScoreFetcher.getBgsScoreForEvaluation(interview.getEvaluationId());
		// Old job role tool
		if (jobRoleToInterviewStructureDAO.get().getAcceptanceCutoffScore() != null
				&& jobRoleToInterviewStructureDAO.get().getRejectionCutoffScore() != null) {
			if (this.isBgsScoreGreaterThanCutOffScore(jobRoleToInterviewStructureDAO.get(), bgsScore)) {
				this.autoApproveInterview(interview);
			} else if (this.isBgsScoreLessThanThresholdScore(jobRoleToInterviewStructureDAO.get(), bgsScore)) {
				this.autoRejectInterview(interview);
			} else {
				this.manualActionRequiredFromClientForInterview(interview, evaluationDAO);
			}
		}
		// New job role tool
		else {
			final Boolean isInterviewApproved = this.interviewRuleManager
					.isInterviewApproved(jobRoleToInterviewStructureDAO.get(), interview);
			final Boolean isInterviewRejected = this.interviewRuleManager
					.isInterviewRejected(jobRoleToInterviewStructureDAO.get(), interview);
			if (isInterviewApproved || isInterviewRejected) {
				if (isInterviewApproved && isInterviewRejected) {
					log.error("Interview {} has approved and rejected both", interview.getId());
					this.manualActionRequiredFromClientForInterview(interview, evaluationDAO);
					return;
				} else if (isInterviewApproved) {
					this.autoApproveInterview(interview);
					return;
				} else if (isInterviewRejected) {
					this.autoRejectInterview(interview);
					return;
				}
			}
			if (this.interviewUtil.isManualActionRequiredFlagEnabled(jobRoleToInterviewStructureDAO.get())) {
				this.manualActionRequiredFromClientForInterview(interview, evaluationDAO);
			} else if (this.automaticRejectionActionConfigured(jobRoleToInterviewStructureDAO.get())) {
				this.autoRejectInterview(interview);
			} else if (this.automaticApprovalActionConfigured(jobRoleToInterviewStructureDAO.get())) {
				this.autoApproveInterview(interview);
			}

		}

	}

	private boolean automaticApprovalActionConfigured(JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO) {
		return !jobRoleToInterviewStructureDAO.getIsManualActionForRemainingCases()
				&& jobRoleToInterviewStructureDAO.getRejectionRuleId() != null;
	}

	private boolean automaticRejectionActionConfigured(JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO) {
		return !jobRoleToInterviewStructureDAO.getIsManualActionForRemainingCases()
				&& jobRoleToInterviewStructureDAO.getApprovalRuleId() != null;
	}

	private void manualActionRequiredFromClientForInterview(InterviewDAO interview, EvaluationDAO evaluationDAO) {
		if (!this.interviewUtil.isLastRound(interview)) {
			log.info("approval required from client : {}", interview.getId());
			this.interviewApprovalProcessor.takeActionForApprovalRequiredFromClient(evaluationDAO);
		}
	}

	private void autoRejectInterview(InterviewDAO interview) {
		if (!this.interviewUtil.isLastRound(interview)) {
			log.info("auto rejection for interview : {}", interview.getId());
			this.interviewRejectionProcessor.rejectInterview(
					interview.getId(),
					AuthenticatedUser.builder().userName("BarRaiser").build(),
					InterviewRejectionProcessor.SOURCE_BARRAISER_REJECTED);
		}
	}

	private void autoApproveInterview(InterviewDAO interview) {
		if (!this.interviewUtil.isLastRound(interview)) {
			log.info("auto approval for interview : {}", interview.getId());
			this.interviewApprovalProcessor.approveInterview(
					interview.getId(), EvaluationStatusManager.BARRAISER_PARTNER_ID);
		}
	}

	private boolean isBgsScoreGreaterThanCutOffScore(
			final JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO,
			final int bgsScore) {
		return jobRoleToInterviewStructureDAO.getAcceptanceCutoffScore() != null
				&& bgsScore >= jobRoleToInterviewStructureDAO.getAcceptanceCutoffScore();
	}

	private boolean isBgsScoreLessThanThresholdScore(
			final JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO,
			final int bgsScore) {
		return jobRoleToInterviewStructureDAO.getRejectionCutoffScore() != null
				&& bgsScore < jobRoleToInterviewStructureDAO.getRejectionCutoffScore();
	}
}
