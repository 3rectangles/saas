/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling;

import com.barraiser.common.enums.RoundType;
import com.barraiser.common.utilities.DateUtils;
import com.barraiser.commons.auth.UserRole;
import com.barraiser.onboarding.candidate.CandidateInformationManager;
import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewUtil;
import com.barraiser.onboarding.interview.PartnerConfigManager;
import com.barraiser.onboarding.interview.jobrole.JobRoleManager;
import com.barraiser.onboarding.interview.jobrole.SkillWeightageManager;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.InterviewSchedulingConfig;
import com.barraiser.onboarding.user.expert.ExpertUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import com.barraiser.commons.auth.AuthenticatedUser;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.barraiser.onboarding.common.Constants.OTHERS_SKILL_ID;

@Component("schedulingDataValidationProcessor")
@AllArgsConstructor
public class DataValidationProcessor implements SchedulingProcessor {
	private static final String ROUND_TYPE_INTERNAL = "INTERNAL";
	private static final List<String> SCHEDULING_PLATFORMS_WHICH_REQUIRE_MINIMUM_TIME_DIFFERENCE_CHECK = List.of("new");
	private static final String MIN_SCHEDULING_BUFFER_FROM_NOW_MINUTES = "min_scheduling_buffer_from_now_minutes";
	private static final String INTERVIEW_STATUS_PENDING_SCHEDULING = "pending_scheduling";
	private static final String SCHEDULING_TIME_BUFFER_ERROR_MESSAGE = "Interview start time and scheduling time are not %d minutes apart";

	private final DynamicAppConfigProperties dynamicAppConfigProperties;
	private final InterViewRepository interViewRepository;
	private final ExpertRepository expertRepository;
	private final SkillWeightageManager skillWeightageManager;
	private final CandidateInformationManager candidateInformationManager;
	private final EvaluationRepository evaluationRepository;
	private final JobRoleManager jobRoleManager;
	private final JobRoleToInterviewStructureRepository jobRoleToInterviewStructureRepository;
	private final InterviewStructureSkillsRepository interviewStructureSkillsRepository;
	private final InterviewStructureRepository interviewStructureRepository;
	private final InterviewUtil interviewUtil;
	private final SchedulingSessionManager schedulingSessionManager;
	private final ExpertUtil expertUtil;
	private final DateUtils dateUtils;
	private final InterviewSchedulingConfig config;

	private final PartnerConfigManager partnerConfigManager;

	@Override
	public void process(final SchedulingProcessingData data) {
		final InterviewDAO interview = this.interViewRepository
				.findById(data.getInput().getInterviewId())
				.orElseThrow(() -> new IllegalArgumentException(
						String.format("Interview does not exist for id: %s", data.getInput().getInterviewId())));

		if (!this.interviewUtil.isFastrackedInterview(interview.getInterviewRound())
				&& !ROUND_TYPE_INTERNAL.equalsIgnoreCase(interview.getInterviewRound())) {
			this.checkSchedulingTimeBuffer(data);
		}

		this.expertRepository
				.findById(data.getInput().getInterviewerId())
				.orElseThrow(() -> new IllegalArgumentException(String.format(
						"Interviewer does not exist for interviewer_id: %s", data.getInput().getInterviewerId())));

		final CandidateDAO candidateDAO = this.candidateInformationManager.getCandidate(interview.getIntervieweeId());
		if (candidateDAO == null) {
			throw new IllegalArgumentException(String
					.format("Interviewee does not exist for interviewee_id: %s", interview.getIntervieweeId()));
		}

		final EvaluationDAO evaluationDAO = this.evaluationRepository.findById(interview.getEvaluationId())
				.orElseThrow(() -> new IllegalArgumentException(
						String.format("Evaluation does not exist for evaluation_id: %s", interview.getEvaluationId())));

		if (Boolean.TRUE.equals(evaluationDAO.getBlockCandidateScheduling())) {
			// When a candidate cancells her interview for 2 time, we put a flag in
			// Evaluation - "block_candidate_scheduling". In this case only BarRaiser team
			// or Partner can schedule the interview.
			final List<UserRole> userRolesEligibleForScheduling = List.of(UserRole.ADMIN, UserRole.PARTNER,
					UserRole.OPS, UserRole.SUPER_ADMIN);

			data.getUser().getRoles().stream().filter(userRolesEligibleForScheduling::contains).findAny()
					.orElseThrow(() -> new IllegalArgumentException(
							"Interview cannot be scheduled at this time, please contact your recruiter"));
		}

		if (EvaluationStatus.CANCELLED.getValue().equalsIgnoreCase(evaluationDAO.getStatus())) {
			throw new IllegalArgumentException("Evaluation has been cancelled, so scheduling is not possible.");
		}

		final JobRoleDAO jobRoleDAO = this.jobRoleManager.getJobRoleFromEvaluation(evaluationDAO)
				.orElseThrow(() -> new IllegalArgumentException(
						String.format("Job role does not exist for job_role_id: %s", evaluationDAO.getJobRoleId())));

		final JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO = this.jobRoleToInterviewStructureRepository
				.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId(jobRoleDAO.getEntityId().getId(),
						jobRoleDAO.getEntityId().getVersion(), interview.getInterviewStructureId())
				.orElseThrow(() -> new IllegalArgumentException(String.format(
						"job_role_id: %s and  version %d  and interview_round: %s " +
								"does not exist in job_role_to_interview_structure table",
						jobRoleDAO.getEntityId().getId(), jobRoleDAO.getEntityId().getVersion(),
						interview.getInterviewRound())));

		this.checkSkillWeightageExistence(jobRoleToInterviewStructureDAO, evaluationDAO);

		final InterviewStructureDAO interviewStructureDAO = this.interviewStructureRepository
				.findById(interview.getInterviewStructureId()).get();
		if (data.getInput().getEndDate() - data.getInput().getStartDate() != interviewStructureDAO.getDuration() * 60) {
			throw new IllegalArgumentException(
					"Given duration of interview is not equal to actual duration of interview");
		}

		if (INTERVIEW_STATUS_PENDING_SCHEDULING.equals(interview.getStatus())
				|| InterviewStatus.SLOT_REQUESTED_BY_CANDIDATE.getValue().equals(interview.getStatus())) {
			if (interview.getStartDate() != null || interview.getInterviewerId() != null) {
				throw new IllegalArgumentException("Interview is already scheduled, please contact support.");
			}
		} else {
			throw new IllegalArgumentException("Interview cannot be scheduled, please contact support.");
		}

		if (!Objects.equals(interview.getInterviewRound(), ROUND_TYPE_INTERNAL))
			this.checkIfRepeatingExpert(evaluationDAO.getId(), data.getInput().getInterviewerId());

		this.checkIfCandidateHasOverlappingBookedSlot(interview.getIntervieweeId(), data.getInput().getStartDate(),
				data.getInput().getEndDate());

		this.schedulingSessionManager.checkIfSchedulingSessionDataIsStale(interview.getId(),
				interview.getRescheduleCount());

		this.checkIfSchedulingAllowedInTheGivenSlot(interview, data, data.getInput().getSchedulingPlatform(),
				interview.getInterviewRound(), data.getInput().getStartDate(), data.getInput().getTimezone());
	}

	public void checkSkillWeightageExistence(final JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO,
			final EvaluationDAO evaluationDAO) {
		final List<SkillWeightageDAO> skillWeightageList = this.skillWeightageManager
				.getSkillWeightageForJobRole(evaluationDAO.getJobRoleId(), evaluationDAO.getJobRoleVersion());
		if (skillWeightageList == null || skillWeightageList.size() == 0) {
			throw new IllegalArgumentException(String
					.format("Skill weightage snapshot does not exist for evaluation id: %s", evaluationDAO.getId()));
		}

		this.checkSkillWeightageExistenceForSkills(jobRoleToInterviewStructureDAO, skillWeightageList);
	}

	public void checkSkillWeightageExistenceForSkills(
			final JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO,
			final List<SkillWeightageDAO> skillWeightageList) {

		final Map<String, Double> skillWeightageMap = skillWeightageList.stream()
				.collect(Collectors.toMap(SkillWeightageDAO::getSkillId, SkillWeightageDAO::getWeightage));

		final List<InterviewStructureSkillsDAO> interviewStructureSkillsList = this.interviewStructureSkillsRepository
				.findAllByInterviewStructureIdAndIsSpecific(jobRoleToInterviewStructureDAO.getInterviewStructureId(),
						false);

		if (interviewStructureSkillsList == null || interviewStructureSkillsList.size() == 0) {
			throw new IllegalArgumentException(
					String.format("There is no mapping for interview structure to skill for interview structure id: %s",
							jobRoleToInterviewStructureDAO.getInterviewStructureId()));
		}

		String message = ("Skill weightage does not exist for the following skill ids:\n");
		boolean throwException = false;

		for (final InterviewStructureSkillsDAO interviewStructureSkillsDAO : interviewStructureSkillsList) {
			if (!OTHERS_SKILL_ID.equals(interviewStructureSkillsDAO.getSkillId())
					&& !skillWeightageMap.containsKey(interviewStructureSkillsDAO.getSkillId())) {
				message += (interviewStructureSkillsDAO.getSkillId() + "\n");
				if (!throwException) {
					throwException = true;
				}
			}
		}

		if (throwException) {
			throw new IllegalArgumentException(message);
		}
	}

	private void checkIfRepeatingExpert(final String evaluationId, final String interviewerId) {
		if (this.expertUtil.isExpertDuplicate(interviewerId)) {
			return;
		}
		final List<String> usedInterviewers = this.interViewRepository.findAllByEvaluationId(evaluationId).stream()
				.filter(x -> !x.getStatus().equalsIgnoreCase(InterviewStatus.CANCELLATION_DONE.getValue()))
				.map(InterviewDAO::getInterviewerId).collect(Collectors.toList());
		if (usedInterviewers.contains(interviewerId)) {
			throw new IllegalArgumentException("Oops! That was not supposed to happen.Please refresh the page.");
		}
	}

	public void checkIfCandidateHasOverlappingBookedSlot(final String intervieweeId, final Long startDate,
			final Long endDate) {
		final List<InterviewDAO> overlappingInterviews = this.interviewUtil
				.getOverlappingInterviewsForCandidate(intervieweeId, startDate, endDate);
		if (overlappingInterviews.size() > 0) {
			throw new IllegalArgumentException(
					"Candidate already has another interview scheduled around this time. Please select another slot");
		}
	}

	private void checkSchedulingTimeBuffer(final SchedulingProcessingData data) {
		final Long minTimeDiffInMinutes = (long) this.dynamicAppConfigProperties
				.getInt(MIN_SCHEDULING_BUFFER_FROM_NOW_MINUTES);

		if (SCHEDULING_PLATFORMS_WHICH_REQUIRE_MINIMUM_TIME_DIFFERENCE_CHECK
				.contains(data.getInput().getSchedulingPlatform())) {
			final Long timeDifferenceBetweenSchedulingTimeAndInterviewStartTimeInMinutes = this
					.getTimeDifferenceBetweenInterviewStartTimeAndSchedulingTimeInMinutes(
							data
									.getInput()
									.getStartDate());

			if (timeDifferenceBetweenSchedulingTimeAndInterviewStartTimeInMinutes < minTimeDiffInMinutes) {
				throw new IllegalArgumentException(String.format(
						SCHEDULING_TIME_BUFFER_ERROR_MESSAGE,
						minTimeDiffInMinutes));
			}
		}
	}

	private Long getTimeDifferenceBetweenInterviewStartTimeAndSchedulingTimeInMinutes(final Long interviewStartDate) {
		final Instant schedulingTime = Instant.now();

		final Instant interviewStartTime = Instant.ofEpochSecond(interviewStartDate);

		return Duration
				.between(schedulingTime, interviewStartTime)
				.toMinutes();
	}

	public void checkIfSchedulingAllowedInTheGivenSlot(final InterviewDAO interview,
			final SchedulingProcessingData data,
			final String schedulingPlatform, final String interviewRound,
			final Long startDate, final String timezone) {
		final boolean isSlotForMidnight = this.dateUtils.isBetweenTimeOfDay(startDate,
				config.getScheduledTimeUpperBound(), config.getScheduledTimeLowerBound(), timezone);

		boolean checkMidNightSlotFlag = !(partnerConfigManager.is24HourSchedulingAllowed(interview)
				|| RoundType.INTERNAL.getValue().equals(interviewRound)
				|| schedulingPlatform.equals("old")
				|| schedulingPlatform.equals("manual_scheduling"));

		if (checkMidNightSlotFlag) {
			if (isSlotForMidnight) {
				throw new IllegalArgumentException(
						"Please refresh the page and try again");
			}
		}
	}
}
