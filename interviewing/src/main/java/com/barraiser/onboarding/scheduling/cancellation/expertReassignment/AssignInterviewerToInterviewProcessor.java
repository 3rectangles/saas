/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.interview.InterviewService;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO.ExpertAllocatorData;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class AssignInterviewerToInterviewProcessor implements ExpertAllocationProcessor {
	private final InterviewService interviewService;

	@Override
	public void process(final ExpertAllocatorData data) {
		final InterviewDAO interviewDAO = this.interviewService.findById(data.getInterviewId());
		data.setInterview(this.interviewService.save(
				interviewDAO.toBuilder().interviewerId(data.getInterviewerId())
						.build(),
				data.getAllocatedBy(), data.getSource()));
	}
}
