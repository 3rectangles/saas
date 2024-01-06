/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO.ExpertAllocatorData;
import com.barraiser.onboarding.scheduling.expert_deallocation.dto.ExpertDeAllocatorData;
import com.barraiser.onboarding.scheduling.expert_deallocation.ExpertDeallocator;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.Instant;

@Log4j2
@Component
@AllArgsConstructor
public class AssignNewInterviewToOriginalExpertProcessor implements ExpertDeAllocationProcessor {
	public static final String ACTION_PERFORMED_BY_SYSTEM = "BarRaiser";

	private final ExpertAllocator expertAllocator;
	private final ExpertDeallocator expertDeallocator;

	@Override
	@Transactional
	public void process(final ExpertDeAllocatorData data) throws Exception {
		final InterviewDAO interviewDAO = data.getNewInterviewThatExpertCanTake();

		final ExpertDeAllocatorData dataForInterviewScheduledWithDuplicate = ExpertDeAllocatorData
				.builder()
				.interviewId(interviewDAO.getId())
				.deAllocationReason(null)
				.deallocatedBy(ACTION_PERFORMED_BY_SYSTEM)
				.deAllocationTime(Instant.now().getEpochSecond())
				.build();

		this.expertDeallocator.deallocateExpertForInterview(dataForInterviewScheduledWithDuplicate);

		final ExpertAllocatorData expertAllocatorData = ExpertAllocatorData.builder()
				.interviewId(interviewDAO.getId())
				.interviewerId(data.getOriginalInterviewerId())
				.startDate(interviewDAO.getStartDate())
				.isOnlyCandidateChanged(true)
				.allocatedBy(ACTION_PERFORMED_BY_SYSTEM)
				.previousInterviewOfExpert(
						data
								.getInterview()
								.getId())
				.build();

		this.expertAllocator
				.allocateExpertToInterview(expertAllocatorData);
	}
}
