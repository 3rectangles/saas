/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers;

import com.barraiser.common.graphql.types.InterviewSlots;
import com.barraiser.common.graphql.types.Slot;
import com.barraiser.common.utilities.DateUtils;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class ResponseConstructionProcessor implements MatchInterviewersProcessor {
	private final DateUtils dateUtils;

	@Override
	public void process(final MatchInterviewersData data) {
		data.setInterviewSlots(
				this.createInterviewSlotsObjectToReturn(
						data.getSlotInterviewerMapping(), data.getInterviewersPerDayDataList(), data.getTimezone()));
		for (final InterviewSlots interviewSlot : data.getInterviewSlots()) {
			if (interviewSlot.getAllSlots().isEmpty()) {
				log.info("no slots found for interview id : {}, and date : {}", data.getInterviewId(),
						interviewSlot.getDate());
			}
		}
	}

	private List<InterviewSlots> createInterviewSlotsObjectToReturn(
			final Map<Long, String> slotToInterviewer,
			final List<InterviewersPerDayData> interviewersPerDayDataList, final String timezone) {

		final List<InterviewSlots> interviewSlots = new ArrayList<>();
		for (InterviewersPerDayData interviewersPerDayData : interviewersPerDayDataList) {
			final List<Slot> prioritySlots = new ArrayList<>();
			final List<Slot> allSlots = new ArrayList<>();
			final Map<Long, String> slotToInterviewerForThatDay = new HashMap<>();
			slotToInterviewer.forEach(
					(slot, interviewer) -> {
						if (this.dateUtils
								.getFormattedDateString(
										slot, timezone, DateUtils.DATE_IN_YYYY_MM_DD_FORMAT)
								.equals(interviewersPerDayData.getDate())) {
							slotToInterviewerForThatDay.put(slot, interviewer);
						}
					});
			final List<String> listOfInterviewersThatHaveASlot = this.filterInterviewersHavingASlot(
					interviewersPerDayData.getInterviewers(), slotToInterviewerForThatDay);
			final int numberOfPriorityInterviewers = Math.max(listOfInterviewersThatHaveASlot.size() / 4, 2);
			final List<String> priorityInterviewers = listOfInterviewersThatHaveASlot
					.size() >= numberOfPriorityInterviewers
							? listOfInterviewersThatHaveASlot.subList(
									0, numberOfPriorityInterviewers)
							: listOfInterviewersThatHaveASlot;
			slotToInterviewerForThatDay.forEach(
					(slot, interviewer) -> {
						final Slot slotToBeAdded = Slot.builder().startDate(slot).userId(interviewer).build();
						if (priorityInterviewers.contains(interviewer)) {
							prioritySlots.add(slotToBeAdded);
						}
						allSlots.add(slotToBeAdded);
					});
			interviewSlots.add(
					InterviewSlots.builder()
							.date(interviewersPerDayData.getDate())
							.prioritySlots(prioritySlots)
							.allSlots(allSlots)
							.build());
		}
		return interviewSlots;
	}

	private List<String> filterInterviewersHavingASlot(
			final List<InterviewerData> interviewers, final Map<Long, String> slotToInterviewer) {

		List<String> listOfInterviewerId = interviewers.stream().map(InterviewerData::getId)
				.collect(Collectors.toList());
		listOfInterviewerId = listOfInterviewerId.stream()
				.filter(slotToInterviewer::containsValue)
				.collect(Collectors.toList());
		return listOfInterviewerId;
	}
}
