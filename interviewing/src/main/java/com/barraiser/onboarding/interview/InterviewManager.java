/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.auth.pojo.Constants;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.dal.JiraUUIDRepository;
import com.barraiser.onboarding.scheduling.scheduling.OverBookingThresholdCalculator;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.barraiser.onboarding.communication.InterviewSchedulingCommunicationService.INTERVIEW_DATE_TIME_FORMAT;

@Component
@AllArgsConstructor
public class InterviewManager {
	public static final String LAST_MINUTE_CANCELLED_INTERVIEW = "last_minute_cancelled_interview";
	private final InterViewRepository interViewRepository;
	private final JiraUUIDRepository jiraUUIDRepository;
	private final DateUtils dateUtils;
	private final OverBookingThresholdCalculator overBookingThresholdCalculator;
	private final InterviewUtil interviewUtil;

	public Boolean isInterviewCancelled(final InterviewDAO interviewDAO, final List<String> cancellationReasonIds) {
		return InterviewStatus.CANCELLATION_DONE.getValue().equals(interviewDAO.getStatus()) && cancellationReasonIds
				.contains(interviewDAO.getCancellationReasonId());
	}

	public Boolean isInterviewLastMinuteCancelled(final InterviewDAO interviewDAO) {
		return interviewDAO.getStartDate() - Integer
				.parseInt(interviewDAO.getCancellationTime()) <= Constants.LAST_MINUTE_INTERVIEW_CANCELLATION_THRESHOLD;
	}

	public Map<String, Integer> getCountPerInterviewStatus(final List<InterviewDAO> interviews,
			final List<String> cancellationReasonIds) {
		final Map<String, Integer> countPerInterviewStatus = new HashMap<>();
		for (InterviewDAO interviewDAO : interviews) {
			if (this.isInterviewCancelled(interviewDAO, cancellationReasonIds)) {
				countPerInterviewStatus.put(InterviewStatus.CANCELLATION_DONE.getValue(),
						countPerInterviewStatus.getOrDefault(InterviewStatus.CANCELLATION_DONE.getValue(), 0) + 1);
				if (this.isInterviewLastMinuteCancelled(interviewDAO)) {
					countPerInterviewStatus.put(LAST_MINUTE_CANCELLED_INTERVIEW,
							countPerInterviewStatus.getOrDefault(LAST_MINUTE_CANCELLED_INTERVIEW, 0) + 1);
				}
			}
			if (InterviewStatus.fromString(interviewDAO.getStatus()).isDone()) {
				countPerInterviewStatus.put(InterviewStatus.DONE.getValue(),
						countPerInterviewStatus.getOrDefault(InterviewStatus.DONE.getValue(), 0) + 1);
			}
		}
		return countPerInterviewStatus;
	}

	public Map<String, Integer> getTaShortagePerSlot(Long startTime, Long endTime) {
		final Map<String, Integer> taShortagePerSlot = new LinkedHashMap<>();
		final Map<String, Integer> interviewsPerSlot = new LinkedHashMap<>();

		List<InterviewDAO> unAssignedInterviews = this.interViewRepository
				.findAllByInterviewRoundNotInAndStartDateGreaterThanEqualAndEndDateLessThanEqualAndStatusNotAndTaggingAgentIsNull(
						this.interviewUtil.getRoundTypesThatNeedNoTaggingAgent(), startTime,
						endTime, InterviewStatus.CANCELLATION_DONE.getValue());
		this.makeSlotCountMap(taShortagePerSlot, unAssignedInterviews);

		List<InterviewDAO> interviews = this.interViewRepository
				.findAllByInterviewRoundNotInAndStartDateGreaterThanEqualAndEndDateLessThanEqualAndStatusNot(
						this.interviewUtil.getRoundTypesThatNeedNoTaggingAgent(), startTime, endTime,
						InterviewStatus.CANCELLATION_DONE.getValue());
		this.makeSlotCountMap(interviewsPerSlot, interviews);

		List<String> slotList = new ArrayList<>();

		this.adjustTaShortage(taShortagePerSlot, interviewsPerSlot, slotList);
		return taShortagePerSlot;
	}

	public Double getTaBookingFractionForSlot(final Long startTime, final Long endTime) {
		long unAssignedInterviewsCount = this.interViewRepository
				.countByInterviewRoundNotInAndStartDateGreaterThanEqualAndEndDateLessThanEqualAndStatusNotAndTaggingAgentIsNull(
						this.interviewUtil.getRoundTypesThatNeedNoTaggingAgent(), startTime,
						endTime, InterviewStatus.CANCELLATION_DONE.getValue());
		final long totalInterviewsInSlot = this.interViewRepository
				.countByInterviewRoundNotInAndStartDateGreaterThanEqualAndEndDateLessThanEqualAndStatusNot(
						this.interviewUtil.getRoundTypesThatNeedNoTaggingAgent(), startTime, endTime,
						InterviewStatus.CANCELLATION_DONE.getValue());
		final Double assignedTaFraction = totalInterviewsInSlot == 0l ? 0.0D
				: (double) (totalInterviewsInSlot - unAssignedInterviewsCount) / (totalInterviewsInSlot);
		return assignedTaFraction;
	}

	private void makeSlotCountMap(Map<String, Integer> slotCountMap, List<InterviewDAO> interviews) {
		for (InterviewDAO interviewDAO : interviews) {
			String slot = this.dateUtils.getFormattedDateString(interviewDAO.getStartDate(), null,
					INTERVIEW_DATE_TIME_FORMAT) +
					" - " + this.dateUtils.getFormattedDateString(interviewDAO.getEndDate(), null,
							INTERVIEW_DATE_TIME_FORMAT);
			slotCountMap.putIfAbsent(slot, 0);
			slotCountMap.put(slot, slotCountMap.get(slot) + 1);
		}
	}

	private void adjustTaShortage(Map<String, Integer> taShortagePerSlot, Map<String, Integer> interviewsPerSlot,
			List<String> slotList) {
		for (String slot : slotList) {
			Integer totalInterviews = interviewsPerSlot.get(slot);
			Integer assignedTa = totalInterviews - taShortagePerSlot.get(slot);
			Integer adjustedTaDelta = totalInterviews
					- (int) (assignedTa * (1 + this.overBookingThresholdCalculator.getOverBookingThresholdForTa()));
			if (totalInterviews.equals(0) || adjustedTaDelta <= 0) {
				taShortagePerSlot.remove(slot);
			} else {
				taShortagePerSlot.put(slot, adjustedTaDelta);
			}
		}
	}

	public Map<String, List<InterviewDAO>> getInterviewsPerExpert(final Specification<InterviewDAO> specification) {
		final List<InterviewDAO> interviewDAOs = this.interViewRepository.findAll(specification);
		final Map<String, List<InterviewDAO>> interviewsPerExpert = new HashMap<>();
		for (InterviewDAO interviewDAO : interviewDAOs) {
			final List<InterviewDAO> interviewsOfAnExpert = interviewsPerExpert.getOrDefault(
					interviewDAO.getInterviewerId(),
					new ArrayList<>());
			interviewsOfAnExpert.add(interviewDAO);
			interviewsPerExpert.put(interviewDAO.getInterviewerId(), interviewsOfAnExpert);
		}
		return interviewsPerExpert;
	}

	public List<InterviewDAO> getLinkedInterviews(final InterviewDAO interviewDAO) {
		return this.interViewRepository
				.findAllByEvaluationIdAndInterviewStructureId(interviewDAO.getEvaluationId(),
						interviewDAO.getInterviewStructureId());
	}

	public Long getRescheduledTimeOfInterview(final String interviewId) {
		return this.interViewRepository
				.findByRescheduledFrom(this.jiraUUIDRepository.findByUuid(interviewId).get().getJira()).getStartDate();
	}
}
