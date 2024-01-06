/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.SchedulingSessionDAO;
import com.barraiser.onboarding.dal.SchedulingSessionRepository;
import com.barraiser.onboarding.interview.cost.InterviewCostDetailsCalculator;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO.ExpertAllocatorData;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Log4j2
@Component
@AllArgsConstructor
public class ExpertCostCalculationProcessor implements ExpertAllocationProcessor {
	private final InterviewCostDetailsCalculator interviewCostDetailsCalculator;
	private final SchedulingSessionRepository schedulingSessionRepository;

	@Override
	public void process(final ExpertAllocatorData data) throws Exception {
		final InterviewDAO interviewDAO = data.getInterview();
		final Optional<SchedulingSessionDAO> schedulingContext = this.schedulingSessionRepository
				.findTopByInterviewIdAndRescheduleCountOrderByCreatedOnDesc(interviewDAO.getId(),
						interviewDAO.getRescheduleCount());
		if (schedulingContext.isPresent()) {
			this.interviewCostDetailsCalculator.calculateAndSaveInterviewCost(interviewDAO,
					data.getInterviewerId(), schedulingContext.get().getInterviewCost(),
					schedulingContext.get().getUsedMargin(), schedulingContext.get().getConfiguredMargin());
		} else {
			this.interviewCostDetailsCalculator.calculateAndSaveInterviewCost(interviewDAO,
					data.getInterviewerId());
		}
	}
}
