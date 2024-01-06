/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.onboarding.availability.AvailabilityManager;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.interview.InterviewStructureManager;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class BookInterviewerSlotForNewInterviewProcessor implements CancellationProcessor {
	private final AvailabilityManager availabilityManager;
	private final InterviewStructureManager interviewStructureManager;

	@Override
	public void process(final CancellationProcessingData data) throws Exception {
		final InterviewDAO interviewDAO = data.getInterviewThatExpertCanTake();
		final Long expertStartTime = interviewDAO.getStartDate()
				+ this.interviewStructureManager.getExpertJoiningTime(interviewDAO.getInterviewStructureId());
		this.availabilityManager.bookSlotNeedlessAvailability(
				data.getPreviousStateOfCancelledInterview().getInterviewerId(),
				"BarRaiser", expertStartTime, interviewDAO.getEndDate(), data.getBuffer());
	}
}
