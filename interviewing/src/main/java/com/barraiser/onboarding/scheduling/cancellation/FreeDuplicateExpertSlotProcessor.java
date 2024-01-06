/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.onboarding.availability.AvailabilityManager;
import com.barraiser.onboarding.availability.DTO.BookedSlotDTO;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.interview.InterviewUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@AllArgsConstructor
@Component
public class FreeDuplicateExpertSlotProcessor implements CancellationProcessor {
	private final AvailabilityManager availabilityManager;
	private final InterviewUtil interviewUtil;

	@Override
	public void process(final CancellationProcessingData data) throws Exception {
		final InterviewDAO interviewDAO = data.getInterviewThatExpertCanTake();
		this.releaseBookedSlotOfDuplicateExpert(interviewDAO);
	}

	private void releaseBookedSlotOfDuplicateExpert(final InterviewDAO interviewDAO) {
		final Long startTimeOfExpertForInterview = this.interviewUtil.getExpertStartTimeForInterview(interviewDAO);
		final BookedSlotDTO bookedSlot = this.availabilityManager
				.findInterviewingBookedSlot(interviewDAO.getInterviewerId(), startTimeOfExpertForInterview,
						interviewDAO.getEndDate());
		this.availabilityManager.freeBookedSlot(bookedSlot);
	}
}
