/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.expertAssignment;

import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO.ExpertAllocatorData;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.ExpertAllocationProcessor;
import com.barraiser.onboarding.user.expert.ExpertUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Log4j2
@Component
@AllArgsConstructor
public class DataValidationProcessor implements ExpertAllocationProcessor {

	@Override
	public void process(final ExpertAllocatorData data) throws IOException {
		final String interviewStatus = data.getInterview().getStatus();
		if (InterviewStatus.CANCELLATION_DONE.getValue().equals(interviewStatus)
				|| InterviewStatus.DONE.getValue().equals(interviewStatus)) {
			throw new IllegalArgumentException("expert cannot be allocated");
		}

		if (data.getInterviewerId() == null) {
			throw new IllegalArgumentException("expert not present in the request");
		}
	}
}
