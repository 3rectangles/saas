/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.availability;

import com.barraiser.onboarding.availability.DTO.InterviewingTimeSlot;
import com.barraiser.onboarding.dal.AvailabilityDAO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@AllArgsConstructor
@Component
public class InterviewingTimeslotUtilityService {

	public List<InterviewingTimeSlot> getOverlappingSlots(final List<InterviewingTimeSlot> slots) {
		final Set<InterviewingTimeSlot> overlappingSlots = new HashSet<>();
		this.sortSlotsByStartTime(slots);

		if (slots.size() >= 2) {
			for (int i = 1; i < slots.size(); i++) {
				if (this.isOverlapping(slots.get(i - 1), slots.get(i))) {
					overlappingSlots.add(slots.get(i - 1));
					overlappingSlots.add(slots.get(i));
				}
			}
		}

		return overlappingSlots.stream().collect(Collectors.toList());
	}

	public List<InterviewingTimeSlot> mergeSlots(final List<InterviewingTimeSlot> slots) {

		this.sortSlotsByStartTime(slots);
		final List<InterviewingTimeSlot> mergedSlots = new ArrayList<>();

		for (InterviewingTimeSlot slot : slots) {

			if (mergedSlots.size() == 0) {
				mergedSlots.add(slot);
				continue;
			}

			final InterviewingTimeSlot lastMergedSlot = mergedSlots.get(mergedSlots.size() - 1);

			if (this.isMergeable(slot, lastMergedSlot)) {
				final InterviewingTimeSlot mergedTimeSlot = this.mergeSlot(slot, lastMergedSlot);
				mergedSlots.remove(lastMergedSlot);
				mergedSlots.add(mergedTimeSlot);
			} else {
				mergedSlots.add(slot);
			}
		}

		return mergedSlots;
	}

	private void sortSlotsByStartTime(final List<InterviewingTimeSlot> slots) {
		Collections.sort(slots, new Comparator<InterviewingTimeSlot>() {
			@Override
			public int compare(InterviewingTimeSlot o1, InterviewingTimeSlot o2) {
				return (int) (o1.getStartTimeEpoch() - o2.getStartTimeEpoch());
			}
		});
	}

	public Boolean isMergeable(final InterviewingTimeSlot s1, final InterviewingTimeSlot s2) {

		if (this.isOverlapping(s1, s2)) {
			log.info(
					"Slots A (start_time :{}, end_time :{} ) and B (start_time :{} , end_time :{}) are overlapping for user: {}",
					s1.getStartTimeEpoch(), s1.getEndTimeEpoch(), s2.getStartTimeEpoch(), s2.getEndTimeEpoch(),
					s1.getUserId());
			return Boolean.FALSE;
		}

		if ((s1.getEndTimeEpoch().longValue() == s2.getStartTimeEpoch().longValue()) ||
				(s2.getEndTimeEpoch().longValue() == s1.getStartTimeEpoch().longValue())) {
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	public Boolean isOverlapping(final InterviewingTimeSlot s1, final InterviewingTimeSlot s2) {

		final InterviewingTimeSlot slotToTheLeft = s1.getStartTimeEpoch().longValue() <= s2.getStartTimeEpoch()
				.longValue() ? s1 : s2;
		final InterviewingTimeSlot slotToTheRight = slotToTheLeft == s1 ? s2 : s1;

		if (slotToTheLeft.getEndTimeEpoch().longValue() > slotToTheRight.getStartTimeEpoch().longValue()) {
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	/**
	 * User to merge slots belonging to the same user
	 *
	 * @param s1
	 * @param s2
	 * @return
	 */
	private InterviewingTimeSlot mergeSlot(final InterviewingTimeSlot s1, final InterviewingTimeSlot s2) {

		final Boolean leftAdjacent = (s1.getEndTimeEpoch().longValue() == s2.getStartTimeEpoch().longValue());
		final Boolean rightAdjacent = (s2.getEndTimeEpoch().longValue() == s1.getStartTimeEpoch().longValue());

		final Long mergedSlotStart = leftAdjacent ? s1.getStartTimeEpoch()
				: s2.getStartTimeEpoch();
		final Long mergedSlotEnd = leftAdjacent ? s2.getEndTimeEpoch()
				: s1.getEndTimeEpoch();

		final Integer mergedMaxInterviewsCount = Math.min(s1.getMaxInterviews()
				+ s2.getMaxInterviews(), 5);

		return InterviewingTimeSlot
				.builder()
				.userId(s1.getUserId())
				.startTimeEpoch(mergedSlotStart)
				.endTimeEpoch(mergedSlotEnd)
				.maxInterviews(mergedMaxInterviewsCount)
				.build();
	}

	public InterviewingTimeSlot toInterviewingTimeslot(final AvailabilityDAO availability) {
		return InterviewingTimeSlot
				.builder()
				.id(availability.getId())
				.userId(availability.getUserId())
				.startTimeEpoch(availability.getStartDate())
				.endTimeEpoch(availability.getEndDate())
				.maxInterviews(availability.getMaximumNumberOfInterviews())
				.build();
	}

	public AvailabilityDAO toAvailabilityDAO(final InterviewingTimeSlot timeSlot) {
		return AvailabilityDAO
				.builder()
				.id(timeSlot.getId())
				.userId(timeSlot.getUserId())
				.startDate(timeSlot.getStartTimeEpoch())
				.endDate(timeSlot.getEndTimeEpoch())
				.maximumNumberOfInterviews(timeSlot.getMaxInterviews())
				.build();

	}
}
