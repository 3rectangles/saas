/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers.internalInterviews;

import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.FilterSlotsProcessor;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.MatchInterviewersData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.ResponseConstructionProcessor;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Log4j2
@Component("MatchInterviewersForInternalInterviews")
@AllArgsConstructor
public class MatchInterviewersForInternalInterviews {

	private final GetEligibleInterviewersForInternalInterviewsProcessor getEligibleInterviewersProcessor;
	private final MapInterviewersToSlotForInternalInterviewsProcessor mapInterviewersToSlotProcessor;
	private final InterviewersSortingForInternalInterviewsProcessor interviewersSortingProcessor;
	private final SlotToPriorityInterviewerAssignmentForInternalInterviewsProcessor slotToPriorityInterviewerAssignmentProcessor;
	private final FilterSlotsProcessor filterSlotsProcessor;
	private final ResponseConstructionProcessor responseConstructionProcessor;

	public void getInterviewSlots(final MatchInterviewersData data) throws IOException {

		// 1. Get all eligible interviewers
		this.getEligibleInterviewersProcessor.process(data);

		// 2. Prepare Day wise list and get booked slots for each day for each
		// interviewer
		this.mapInterviewersToSlotProcessor.process(data);

		// 3. NOTE : Randomizing for now. Will be load balanced in the future.
		this.interviewersSortingProcessor.process(data);

		// 4. Map slot to priority interviewer
		this.slotToPriorityInterviewerAssignmentProcessor.process(data);

		// 5. Filter for fitting slots
		this.filterSlotsProcessor.process(data);

		// 6. Create return object
		this.responseConstructionProcessor.process(data);
	}
}
