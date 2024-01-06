/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment;

import com.barraiser.common.graphql.types.InterviewSlots;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO.ExpertReassignmentData;
import com.barraiser.onboarding.scheduling.scheduling.MatchInterviewersDataHelper;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.MatchInterviewers;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.MatchInterviewersData;
import com.barraiser.onboarding.user.TimezoneManager;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Log4j2
@Component
@AllArgsConstructor

public class FetchInterviewerForInterviewProcessor implements ExpertReassignmentProcessor {
	private final MatchInterviewers matchInterviewers;
	private final TimezoneManager timezoneManager;
	private final MatchInterviewersDataHelper matchInterviewersDataHelper;

	@Override
	public void process(final ExpertReassignmentData data) throws IOException {
		final MatchInterviewersData matchInterviewersData = this.matchInterviewersDataHelper
				.prepareDataForInterviewSlots(
						data.getInterview().getId());
		matchInterviewersData.setAvailabilityStartDate(data.getInterview().getStartDate());
		matchInterviewersData.setAvailabilityEndDate(data.getInterview().getEndDate());
		matchInterviewersData
				.setTimezone(this.timezoneManager.getTimezoneOfCandidate(data.getInterviewId()));
		this.matchInterviewers.getInterviewSlots(matchInterviewersData);
		final InterviewSlots interviewSlot = matchInterviewersData.getInterviewSlots().get(0);

		if (interviewSlot != null && interviewSlot.getAllSlots() != null && interviewSlot.getAllSlots().size() > 0) {
			data.setInterviewerToRescheduleWith(interviewSlot.getAllSlots().get(0).getUserId());
		}
	}
}
