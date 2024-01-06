/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment;

import com.barraiser.onboarding.availability.AvailabilityManager;
import com.barraiser.onboarding.dal.InterviewStructureDAO;
import com.barraiser.onboarding.interview.InterviewStructureManager;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO.ExpertAllocatorData;
import com.barraiser.onboarding.user.expert.ExpertUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class BlockExpertAvailabilityProcessor implements ExpertAllocationProcessor {
	private final AvailabilityManager availabilityManager;
	private final InterviewStructureManager interviewStructureManager;
	private final ExpertUtil expertUtil;

	@Override
	public void process(final ExpertAllocatorData data) {
		final InterviewStructureDAO interviewStructureDAO = this.interviewStructureManager
				.getInterviewStructureById(data.getInterview().getInterviewStructureId());
		final Long expertJoiningTime = data.getStartDate() +
				this.interviewStructureManager.getExpertJoiningTime(interviewStructureDAO);
		final Long endTimeOfInterview = data.getStartDate() + this.interviewStructureManager
				.getDurationOfInterview(interviewStructureDAO);

		this.availabilityManager
				.bookSlotNeedlessAvailability(
						data.getInterviewerId(),
						data.getAllocatedBy(),
						expertJoiningTime,
						endTimeOfInterview,
						this.expertUtil.getTimeGapBetweenInterviewForExpert(data.getInterviewerId()));
	}
}
