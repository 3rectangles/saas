/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment.config;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewHistoryDAO;
import com.barraiser.onboarding.dal.InterviewHistoryRepository;
import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.scheduling.expert_deallocation.dto.ExpertDeAllocatorData;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.ExpertDeAllocationProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Log4j2
@Component
@AllArgsConstructor
public class UpdateStatusProcessor implements ExpertDeAllocationProcessor {
	private final InterviewHistoryRepository interviewHistoryRepository;
	private final ObjectMapper objectMapper;

	@Override
	public void process(final ExpertDeAllocatorData data) throws Exception {
		// this is a hack because we want cancellation to be displayed in interview
		// history and not interview for
		// expert cost calculation and expert summary
		final InterviewDAO interviewDAO = data.getInterview().toBuilder()
				.status(InterviewStatus.CANCELLATION_DONE.getValue())
				.cancellationReasonId(data.getDeAllocationReason())
				.cancellationTime(data.getDeAllocationTime().toString()).build();
		this.interviewHistoryRepository.save(
				this.objectMapper.convertValue(interviewDAO, InterviewHistoryDAO.class).toBuilder()
						.id(UUID.randomUUID().toString())
						.interviewId(interviewDAO.getId())
						.createdBy(data.getDeallocatedBy())
						.source(data.getSource())
						.build());
	}
}
