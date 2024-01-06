/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.dal.*;
import com.barraiser.common.enums.RoundType;
import com.barraiser.onboarding.interview.evaluation.scores.BgsScoreFetcher;
import com.barraiser.onboarding.interview.ruleEngine.InterviewRoundClearanceRuleChecker;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class InterviewUtil {

	final InterviewStructureRepository interviewStructureRepository;
	private final EvaluationRepository evaluationRepository;
	private final InterviewStructureManager interviewStructureManager;
	private final InterViewRepository interViewRepository;
	private final JobRoleToInterviewStructureRepository jobRoleToInterviewStructureRepository;
	private final PartnerCompanyRepository partnerCompanyRepository;
	private final WaitingInformationRepository waitingInformationRepository;
	private final ReasonRepository reasonRepository;

	private final BgsScoreFetcher bgsScoreFetcher;
	final InterviewRoundClearanceRuleChecker interviewRoundClearanceRuleChecker;

	public InterviewStructureDAO getInterviewStructureForInterview(
			final InterviewDAO interviewDAO) {
		return this.interviewStructureRepository
				.findById(interviewDAO.getInterviewStructureId())
				.orElseThrow(
						() -> new IllegalArgumentException(
								String.format(
										"Interview structure does not exist for id"
												+ " %s",
										interviewDAO.getInterviewStructureId())));
	}

	public EvaluationDAO getEvaluationForInterview(final String interviewId) {
		return this.evaluationRepository
				.findById(this.interViewRepository.findById(interviewId).get().getEvaluationId())
				.get();
	}

	public int getRoundNumberOfInterview(final InterviewDAO interviewDAO) {
		EvaluationDAO evaluationDAO = this.getEvaluationForInterview(interviewDAO.getId());
		final String jobRoleId = evaluationDAO.getJobRoleId();
		final Integer jobRoleVersion = evaluationDAO.getJobRoleVersion();
		final List<JobRoleToInterviewStructureDAO> jobRoleToInterviewStructureDAOs = this.jobRoleToInterviewStructureRepository
				.findAllByJobRoleIdAndJobRoleVersionOrderByOrderIndexAsc(
						jobRoleId, jobRoleVersion);
		int index = 1;
		for (final JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO : jobRoleToInterviewStructureDAOs) {
			if (jobRoleToInterviewStructureDAO
					.getInterviewStructureId()
					.equals(interviewDAO.getInterviewStructureId())) {
				break;
			}
			index++;
		}
		return index;
	}

	public Long getExpertStartTimeForInterview(final InterviewDAO interview) {
		final Long expertJoiningTime = interview.getStartDate()
				+ this.interviewStructureManager.getExpertJoiningTime(
						interview.getInterviewStructureId());
		return expertJoiningTime;
	}

	public InterviewDAO getInterviewFromZoomMeetingId(final String meetingId) {
		return this.interViewRepository.findByMeetingLinkLike("%" + meetingId + "%");
	}

	public InterviewDAO getMeetingIdFromMeetingURL(final String meetingUrl) {
		return this.interViewRepository
				.findByStatusNotInAndMeetingLinkContainingIgnoreCase(List.of("Done", "cancellation_done"), meetingUrl);
	}

	public Boolean doesInterviewRequireApproval(
			final InterviewDAO interviewDAO,
			final JobRoleToInterviewStructureDAO currentJobRoleToInterviewStructureDAO) {
		if (!InterviewStatus.DONE.getValue().equalsIgnoreCase(interviewDAO.getStatus())) {
			return false;
		}
		final EvaluationDAO evaluationDAO = this.evaluationRepository.findById(interviewDAO.getEvaluationId()).get();
		if (EvaluationStatus.DONE.getValue().equals(evaluationDAO.getStatus())) {
			return false;
		}
		final boolean isNextRoundCreationDependent = this.isNextRoundCreationDependent(
				currentJobRoleToInterviewStructureDAO.getJobRoleId(),
				currentJobRoleToInterviewStructureDAO.getJobRoleVersion(),
				currentJobRoleToInterviewStructureDAO.getInterviewStructureId());
		if (isNextRoundCreationDependent) {

			final Integer scoreInterview = this.bgsScoreFetcher.getBgsScoreForInterview(interviewDAO.getId());
			if (currentJobRoleToInterviewStructureDAO.getInterviewCutoffScore() != null) {
				if (scoreInterview < currentJobRoleToInterviewStructureDAO.getInterviewCutoffScore()) {
					return false;
				}
			}

			if (currentJobRoleToInterviewStructureDAO.getCategoryRejectionJSON() != null
					&& !this.bgsScoreFetcher.isCategoryThresholdCleared(interviewDAO.getId(),
							currentJobRoleToInterviewStructureDAO.getCategoryRejectionJSON())) {
				return false;
			}

			final Integer bgsScore = this.bgsScoreFetcher.getBgsScoreForEvaluation(interviewDAO.getEvaluationId());
			// Old job Role
			if (currentJobRoleToInterviewStructureDAO.getAcceptanceCutoffScore() != null
					&& currentJobRoleToInterviewStructureDAO.getRejectionCutoffScore() != null) {
				if (bgsScore >= currentJobRoleToInterviewStructureDAO.getAcceptanceCutoffScore()
						|| bgsScore < currentJobRoleToInterviewStructureDAO.getRejectionCutoffScore()) {
					return false;
				} else
					return this.doesNextRoundsNeedsToBeGenerated(interviewDAO, currentJobRoleToInterviewStructureDAO);
			}
			// New Job Role
			final boolean isInterviewApproved = this.interviewRoundClearanceRuleChecker
					.isInterviewApproved(currentJobRoleToInterviewStructureDAO, interviewDAO);
			final boolean isInterviewRejected = this.interviewRoundClearanceRuleChecker
					.isInterviewRejected(currentJobRoleToInterviewStructureDAO, interviewDAO);
			if (isInterviewApproved || isInterviewRejected) {
				if (isInterviewApproved && isInterviewRejected)
					return this.doesNextRoundsNeedsToBeGenerated(interviewDAO, currentJobRoleToInterviewStructureDAO);
				return false;
			}
			if (this.isManualActionRequiredFlagEnabled(currentJobRoleToInterviewStructureDAO)) {
				return this.doesNextRoundsNeedsToBeGenerated(interviewDAO, currentJobRoleToInterviewStructureDAO);
			}
			return false;
		}
		return false;
	}

	private Boolean doesNextRoundsNeedsToBeGenerated(InterviewDAO interviewDAO,
			JobRoleToInterviewStructureDAO currentJobRoleToInterviewStructureDAO) {
		final Optional<JobRoleToInterviewStructureDAO> nextJobRoleToInterviewStructureDAO = this.jobRoleToInterviewStructureRepository
				.findTopByJobRoleIdAndJobRoleVersionAndOrderIndexGreaterThanOrderByOrderIndexAsc(
						currentJobRoleToInterviewStructureDAO.getJobRoleId(),
						currentJobRoleToInterviewStructureDAO.getJobRoleVersion(),
						currentJobRoleToInterviewStructureDAO.getOrderIndex());
		if (nextJobRoleToInterviewStructureDAO.isEmpty()) {
			return false;
		}
		return !this.doesRoundExists(
				interviewDAO.getEvaluationId(),
				nextJobRoleToInterviewStructureDAO.get().getInterviewStructureId());
	}

	public Boolean doesRoundExists(final String evaluationId, final String interviewStructureId) {
		return !this.interViewRepository
				.findAllByEvaluationIdAndInterviewStructureId(evaluationId, interviewStructureId)
				.isEmpty();
	}

	public boolean isNextRoundCreationDependent(final String jobRoleId, Integer jobRoleVersion,
			final String interviewStructureId) {
		final Optional<JobRoleToInterviewStructureDAO> jobRoleToInterviewStructureDAO = this.jobRoleToInterviewStructureRepository
				.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId(jobRoleId, jobRoleVersion,
						interviewStructureId);

		if (jobRoleToInterviewStructureDAO.get().getAcceptanceCutoffScore() != null
				&& jobRoleToInterviewStructureDAO.get().getRejectionCutoffScore() != null) {
			return true;
		}
		if (jobRoleToInterviewStructureDAO.get().getApprovalRuleId() != null
				|| jobRoleToInterviewStructureDAO.get().getRejectionRuleId() != null
				|| this.isManualActionRequiredFlagEnabled(jobRoleToInterviewStructureDAO.get())) {
			return true;
		}

		return false;
	}

	public Boolean isManualActionRequiredFlagEnabled(JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO) {
		return jobRoleToInterviewStructureDAO.getIsManualActionForRemainingCases() != null
				&& jobRoleToInterviewStructureDAO.getIsManualActionForRemainingCases();
	}

	public List<InterviewDAO> getOverlappingInterviewsForCandidate(
			final String intervieweeId, final Long startDate, final Long endDate) {
		final List<InterviewDAO> interviews = this.interViewRepository.findAllByIntervieweeId(intervieweeId);
		return interviews.stream()
				.filter(
						x -> !InterviewStatus.CANCELLATION_DONE.getValue().equals(x.getStatus())
								&& x.getStartDate() != null
								&& x.getEndDate() != null
								&& x.getEndDate() > startDate
								&& x.getStartDate() < endDate)
				.collect(Collectors.toList());
	}

	public Boolean checkIfInterviewIsPendingScheduling(EvaluationDAO evaluationDAO, String interviewStatus) {
		Optional<PartnerCompanyDAO> partnerCompanyDAO = this.partnerCompanyRepository
				.findByCompanyId(evaluationDAO.getCompanyId());
		if (partnerCompanyDAO.isPresent()) {
			if (Boolean.FALSE.equals(partnerCompanyDAO.get().getIsCandidateSchedulingEnabled())) {
				return InterviewStatus.PENDING_SCHEDULING.getValue().equalsIgnoreCase(interviewStatus);
			} else {
				Optional<WaitingInformationDAO> waitingInformationDAO = this.waitingInformationRepository
						.findById(evaluationDAO.getId());
				if (waitingInformationDAO.isEmpty()) {
					return false;
				}
				Optional<ReasonDAO> reasonDAO = this.reasonRepository
						.findById(
								waitingInformationDAO.get()
										.getWaitingReasonId());
				if (reasonDAO.isEmpty()) {
					log.error(
							"Waiting_Client Reason is not configured, Reason_ID: {}",
							waitingInformationDAO.get().getWaitingReasonId());
					return false;
				}
				return (EvaluationStatus.WAITING_CLIENT
						.getValue()
						.equalsIgnoreCase(evaluationDAO.getStatus())
						&& (WaitingClientReason.CANDIDATE_IS_PENDING_FOR_SCHEDULING
								.getValue()
								.equalsIgnoreCase(reasonDAO.get().getReason()))
						&& InterviewStatus.PENDING_SCHEDULING
								.getValue()
								.equalsIgnoreCase(interviewStatus));
			}
		} else {
			log.error(
					"Partner missing in Company portal, PartnerID : "
							+ evaluationDAO.getCompanyId());
			return false;
		}
	}

	public boolean isLastRound(final InterviewDAO interviewDAO) {
		final EvaluationDAO evaluationDAO = this.getEvaluationForInterview(interviewDAO.getId());
		final JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO = this.jobRoleToInterviewStructureRepository
				.findTopByJobRoleIdAndJobRoleVersionOrderByOrderIndexDesc(evaluationDAO.getJobRoleId(),
						evaluationDAO.getJobRoleVersion());
		return jobRoleToInterviewStructureDAO.getInterviewStructureId().equals(interviewDAO.getInterviewStructureId());
	}

	public List<InterviewDAO> getAllInternalInterviewsForEvaluation(final String evaluationId) {
		return this.interViewRepository.findAllByEvaluationIdAndInterviewRound(evaluationId,
				RoundType.INTERNAL.getValue());
	}

	public Boolean shouldInterviewBeConsideredForEvaluation(final InterviewDAO interviewDAO) {
		if (interviewDAO.getRedoReasonId() != null) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	public Boolean isFastrackedInterview(final String roundType) {
		return RoundType.FASTTRACK.getValue().equalsIgnoreCase(roundType);
	}

	public List<String> getRoundTypesThatNeedNoTaggingAgent() {
		return List.of(RoundType.FASTTRACK.getValue());
	}

	public Boolean isSaasInterview(final String roundType) {
		return "INTERNAL".equals(roundType);
	}

	public static Boolean isInternalInterview(final String roundType) {
		return "INTERNAL".equals(roundType);
	}

	// short term. Will migrate to passing channels in event
	public Boolean isScheduledViaATSCalInterception(final String schedulingPlatform) {
		return schedulingPlatform != null && schedulingPlatform.endsWith("CAL_INTERCEPTION");
	}

	public Boolean isAddedViaCalInterception(final String source) {
		return source.endsWith("CAL_INTERCEPTION");
	}
}
