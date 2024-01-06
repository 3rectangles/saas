/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Component
@AllArgsConstructor
public class FilterSlotsProcessor implements MatchInterviewersProcessor {
	private final InterviewUtil interviewUtil;
	private final InterViewRepository interViewRepository;

	@Override
	public void process(final MatchInterviewersData data) {
		data.setSlotInterviewerMapping(this.filterForOverlappingSlotOfInterviewee(data));
	}

	private Map<Long, String> filterForOverlappingSlotOfInterviewee(final MatchInterviewersData data) {
		final String intervieweeId = this.interViewRepository.findById(data.getInterviewId()).get().getIntervieweeId();
		final List<InterviewDAO> overlappingInterviews = this.interviewUtil.getOverlappingInterviewsForCandidate(
				intervieweeId,
				data.getAvailabilityStartDate(), data.getAvailabilityEndDate());
		final Map<Long, String> filteredSlotInterviewerMapping = new HashMap<>();
		for (Map.Entry<Long, String> slotInterviewerMapping : data.getSlotInterviewerMapping().entrySet()) {
			if (!this.shouldSlotBeFiltered(slotInterviewerMapping.getKey(), data.getDurationOfInterview(),
					overlappingInterviews, data.getInterviewId())) {
				filteredSlotInterviewerMapping.put(slotInterviewerMapping.getKey(), slotInterviewerMapping.getValue());
			}
		}
		return filteredSlotInterviewerMapping;
	}

	private boolean shouldSlotBeFiltered(final Long startDateOfSlot, final Long durationOfInterview,
			final List<InterviewDAO> overlappingInterviews, final String interviewId) {
		final Long endDateOfSlot = startDateOfSlot + durationOfInterview * 60;
		final Optional<InterviewDAO> overlappingInterview = overlappingInterviews.stream()
				.filter(x -> x.getEndDate() > startDateOfSlot
						&& x.getStartDate() < endDateOfSlot && !x.getId().equals(interviewId))
				.findFirst();
		return overlappingInterview.isPresent();
	}
}
