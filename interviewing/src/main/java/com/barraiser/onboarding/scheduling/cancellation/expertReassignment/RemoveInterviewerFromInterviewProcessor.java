/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.interview.InterviewService;
import com.barraiser.onboarding.scheduling.expert_deallocation.dto.ExpertDeAllocatorData;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Log4j2
@AllArgsConstructor
@Component
public class RemoveInterviewerFromInterviewProcessor implements ExpertDeAllocationProcessor {
	private final InterviewService interviewService;

	@Override
	public void process(final ExpertDeAllocatorData data) throws IOException {
		final InterviewDAO interviewDAO = this.interviewService.findById(data.getInterviewId());
		data.setInterview(this.interviewService.save(interviewDAO.toBuilder().interviewerId(null).build()));
	}
}
