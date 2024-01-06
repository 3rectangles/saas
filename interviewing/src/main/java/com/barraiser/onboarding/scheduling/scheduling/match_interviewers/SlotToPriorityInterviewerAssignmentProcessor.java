/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers;

import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.availability.AvailabilityManager;
import com.barraiser.onboarding.availability.DTO.BookedSlotDTO;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.scheduling.scheduling.OverBookingThresholdCalculator;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class SlotToPriorityInterviewerAssignmentProcessor implements MatchInterviewersProcessor {
	private final DateUtils dateUtils;
	private final AvailabilityManager availabilityManager;
	private final OverBookingThresholdCalculator overBookingThresholdCalculator;

	@Override
	public void process(final MatchInterviewersData data) {
		data.setOverbookingThreshold(
				this.overBookingThresholdCalculator.getOverBookingThresholdForExpert(
						data.getInterviewId()));
		data.setSlotInterviewerMapping(this.fetchDayWiseSlotsForEachInterviewer(data));
	}

	private Map<Long, String> fetchDayWiseSlotsForEachInterviewer(
			final MatchInterviewersData data) {
		final Map<Long, String> slotToPriorityInterviewer = new HashMap<>();
		final Map<Long, List<InterviewerData>> slotToAllInterviewers = this.fetchListOfInterviewersForSlots(data);
		if (slotToAllInterviewers.isEmpty()) {
			log.info("no available interviewer for interview id: {}, evaluation id: {}, partner id: {}",
					data.getInterviewId(), data.getEvaluationId(), data.getPartnerCompanyId());
		}
		slotToAllInterviewers.forEach(
				(slot, interviewers) -> {
					final String interviewerId = this.getInterviewerToScheduleForSlot(slot, interviewers, data);
					if (interviewerId != null) {
						slotToPriorityInterviewer.put(slot, interviewerId);
					}
				});
		return slotToPriorityInterviewer;
	}

	private List<InterviewerData> prioritiseExperts(
			final List<InterviewersPerDayData> interviewersPerDayDataList,
			final List<InterviewerData> experts,
			final Long startDate, final String timezone) {
		final String slotDate = this.dateUtils.getFormattedDateString(
				startDate, timezone, DateUtils.DATE_IN_YYYY_MM_DD_FORMAT);
		final Optional<InterviewersPerDayData> interviewSlotsData = interviewersPerDayDataList.stream()
				.filter(x -> x.getDate().equals(slotDate))
				.findFirst();
		final List<InterviewerData> priorityListOfInterviewersForThatDay = interviewSlotsData.get().getInterviewers();
		final List<String> expertIds = experts.stream().map(InterviewerData::getId).collect(Collectors.toList());
		return priorityListOfInterviewersForThatDay.stream()
				.filter(x -> expertIds.contains(x.getId()))
				.collect(Collectors.toList());
	}

	private List<InterviewerData> filterDuplicateExpertsByActualExperts(
			final List<InterviewerData> interviewers,
			final List<InterviewerData> duplicateExperts) {
		final List<InterviewerData> duplicateInterviewers = new ArrayList<>();
		interviewers.forEach(
				x -> {
					final List<InterviewerData> experts = duplicateExperts.stream()
							.filter(y -> y.getDuplicatedFrom().equals(x.getId()))
							.collect(Collectors.toList());
					duplicateInterviewers.addAll(experts);
				});
		return duplicateInterviewers;
	}

	/**
	 * Used in overbooking context hence considering
	 * only interviewing slots.
	 */
	private List<InterviewerData> filterForCompletelyBookedExperts(
			final List<InterviewerData> interviewers,
			final Long expertJoiningTime,
			final Long endDate,
			final MatchInterviewersData data) {
		final List<InterviewerData> overlappedExpert = new ArrayList<>();
		for (final InterviewerData interviewer : interviewers) {
			final List<BookedSlotDTO> bookedSlotsDAOs = data.getBookedInterviewingSlotsPerInterviewer()
					.get(interviewer.getId());
			for (final BookedSlotDTO bookedSlot : bookedSlotsDAOs) {
				if (((bookedSlot.getStartDate() + bookedSlot.getBuffer()) <= expertJoiningTime
						&& (bookedSlot.getEndDate() - bookedSlot.getBuffer()) >= endDate)
						&& bookedSlot.getBuffer() > 0) {
					overlappedExpert.add(interviewer);
					break;
				}
			}
		}
		return overlappedExpert;
	}

	private List<InterviewerData> filterForOverlappingBookedExperts(
			final List<InterviewerData> interviewers,
			final Long expertJoiningTime,
			final Long endDate,
			final MatchInterviewersData data) {
		final List<InterviewerData> overlappedExperts = new ArrayList<>();
		for (final InterviewerData interviewer : interviewers) {
			final List<BookedSlotDTO> bookedSlotsDAOs = data.getBookedInterviewingSlotsPerInterviewer()
					.get(interviewer.getId());
			for (final BookedSlotDTO bookedSlot : bookedSlotsDAOs) {
				if (bookedSlot.getEndDate() >= expertJoiningTime
						&& bookedSlot.getStartDate() <= endDate) {
					overlappedExperts.add(interviewer);
					break;
				}
			}
		}
		return overlappedExperts;
	}

	private Map<Long, List<InterviewerData>> fetchListOfInterviewersForSlots(
			final MatchInterviewersData data) {

		final List<AvailabilityDAO> freeSlots = this.availabilityManager.getAvailableSlotsOfAllInterviewers(
				data.getInterviewersId(),
				data.getAvailabilityStartDate(),
				data.getAvailabilityEndDate());
		final Map<Long, List<InterviewerData>> slotToInterviewers = new HashMap<>();
		for (AvailabilityDAO freeSlot : freeSlots) {
			final int noOfBookedSlotsInThatAvailabilitySlot = this.availabilityManager.getOverlappingBookedSlotsCount(
					freeSlot,
					data.getBookedSlotsPerInterviewer().get(freeSlot.getUserId()));
			final int maxNoOfInterviewsThatInterviewerCanTake = freeSlot.getMaximumNumberOfInterviews() != null
					? freeSlot.getMaximumNumberOfInterviews()
					: 0;
			final AvailabilityDAO freeSlotForInterview = freeSlot.toBuilder()
					.startDate(
							this.dateUtils.getEpochTo15ThMinuteCeil(
									freeSlot.getStartDate() - data.getExpertJoiningTime()))
					.build();
			final List<AvailabilityDAO> splitSlots = this.availabilityManager.splitSlots(
					freeSlotForInterview, data.getDurationOfInterview());
			for (final AvailabilityDAO slot : splitSlots) {
				final List<InterviewerData> interviewers = slotToInterviewers.getOrDefault(slot.getStartDate(),
						new ArrayList<>());
				interviewers.add(
						InterviewerData.builder()
								.id(slot.getUserId())
								.remainingNumberOfInterviews(
										maxNoOfInterviewsThatInterviewerCanTake
												- noOfBookedSlotsInThatAvailabilitySlot)
								.build());
				slotToInterviewers.put(slot.getStartDate(), interviewers);
			}
		}
		return slotToInterviewers;
	}

	private List<InterviewerData> filterAndPrioritiseAvailableInterviewersForSlot(
			final List<InterviewerData> interviewers,
			final Long startDate,
			final Long expertJoiningTime,
			final MatchInterviewersData data) {
		final Long endDate = startDate + data.getDurationOfInterview() * 60;
		final List<InterviewerData> availableInterviewers = new ArrayList<>();
		for (final InterviewerData interviewer : interviewers) {
			final List<BookedSlotDTO> bookedSlotsDAOs = data.getBookedSlotsPerInterviewer().get(interviewer.getId());
			boolean hasAOverlappingBookedSlot = false;
			for (final BookedSlotDTO bookedSlot : bookedSlotsDAOs) {
				if (bookedSlot.getStartDate() < endDate
						&& bookedSlot.getEndDate() > expertJoiningTime) {
					hasAOverlappingBookedSlot = true;
					break;
				}
			}
			if (!hasAOverlappingBookedSlot && interviewer.getRemainingNumberOfInterviews() > 0) {
				availableInterviewers.add(interviewer);
			}
		}
		return this.prioritiseExperts(
				data.getInterviewersPerDayDataList(), availableInterviewers, startDate, data.getTimezone());
	}

	private List<InterviewerData> filterAndPrioritiseInterviewersThatCanBeOverBooked(
			final List<InterviewerData> interviewers,
			final Long startDate,
			final Long expertStartTime,
			final MatchInterviewersData data) {
		final Long endDate = startDate + data.getDurationOfInterview() * 60;
		final List<InterviewerData> bookedInterviewers = this.filterForCompletelyBookedExperts(interviewers,
				expertStartTime, endDate, data);
		return this.prioritiseExperts(
				data.getInterviewersPerDayDataList(), bookedInterviewers, startDate, data.getTimezone());
	}

	private String getInterviewerToScheduleForSlot(
			final Long interviewStartDate,
			final List<InterviewerData> interviewers,
			final MatchInterviewersData data) {
		final Long expertStartTime = interviewStartDate + data.getExpertJoiningTime();
		final List<InterviewerData> prioritisedActualAvailableInterviewers = this
				.filterAndPrioritiseAvailableInterviewersForSlot(
						interviewers, interviewStartDate, expertStartTime, data);
		final List<InterviewerData> prioritisedInterviewersThatCanBeOverbooked = this
				.filterAndPrioritiseInterviewersThatCanBeOverBooked(
						interviewers, interviewStartDate, expertStartTime, data);
		final List<InterviewerData> duplicateInterviewers = this.filterDuplicateExpertsByActualExperts(
				prioritisedInterviewersThatCanBeOverbooked, data.getDuplicateExperts());
		final List<InterviewerData> bookedDuplicateInterviewers = this.filterForOverlappingBookedExperts(
				duplicateInterviewers,
				expertStartTime,
				(interviewStartDate + data.getDurationOfInterview() * 60),
				data);
		final double requiredThresholdPercentageOfOverbooking = data.getOverbookingThreshold();
		final double actualPercentageOfOverbooking = (double) (bookedDuplicateInterviewers.size() + 1)
				/ (double) (bookedDuplicateInterviewers.size()
						+ prioritisedInterviewersThatCanBeOverbooked.size()
						+ 1);
		duplicateInterviewers.removeAll(bookedDuplicateInterviewers);
		final String interviewerId;
		if (actualPercentageOfOverbooking <= requiredThresholdPercentageOfOverbooking
				&& (duplicateInterviewers.size() > 0)) {
			interviewerId = duplicateInterviewers.get(0).getId();
		} else {
			interviewerId = prioritisedActualAvailableInterviewers.size() > 0
					? prioritisedActualAvailableInterviewers.get(0).getId()
					: null;
		}
		return interviewerId;
	}
}
