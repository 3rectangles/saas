/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import java.io.IOException;

@Log4j2
@Component
@AllArgsConstructor
public class MatchInterviewers {
	private final MapInterviewersToSlotProcessor mapInterviewersToSlotProcessor;
	private final InterviewersSortingProcessor interviewersSortingProcessor;
	private final SlotToPriorityInterviewerAssignmentProcessor slotToPriorityInterviewerAssignmentProcessor;
	private final ResponseConstructionProcessor responseConstructionProcessor;
	private final FilterSlotsProcessor filterSlotsProcessor;
	private final GetEligibleInterviewersProcessor getEligibleInterviewersProcessor;
	private final FilterMidnightSlotsProcessor filterMidnightSlotsProcessor;

	public void getInterviewSlots(final MatchInterviewersData data) throws IOException {

		// 1. Get all eligible interviewers
		this.getEligibleInterviewersProcessor.process(data);

		// 2. Prepare Day wise list and get booked slots for each day for each
		// interviewer
		this.mapInterviewersToSlotProcessor.process(data);

		// 3. Sort Interviewers within a date based on workEx, bookedSlots, cost
		this.interviewersSortingProcessor.process(data);

		// 4. Map slot to priority interviewer + filter based on availability
		this.slotToPriorityInterviewerAssignmentProcessor.process(data);

		// 5. Filter for fitting slots
		this.filterSlotsProcessor.process(data);

		// 6. Filter midnight slots
		this.filterMidnightSlotsProcessor.process(data);

		// 7. Create return object
		this.responseConstructionProcessor.process(data);
	}
}
