/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment;

import com.amazonaws.services.sqs.AmazonSQS;
import com.barraiser.onboarding.common.Constants;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.scheduling.expert_deallocation.dto.ExpertDeAllocatorData;
import com.barraiser.onboarding.payment.expert.InterviewConcludedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@AllArgsConstructor
@Component
public class UpdateInterviewCostProcessor implements ExpertDeAllocationProcessor {
	private final AmazonSQS amazonSQS;
	private final StaticAppConfigValues staticAppConfigValues;
	private final ObjectMapper objectMapper;

	@Override
	public void process(final ExpertDeAllocatorData data) throws Exception {
		this.sendToQueueForCostCalculation(data);
	}

	private void sendToQueueForCostCalculation(final ExpertDeAllocatorData data)
			throws JsonProcessingException {
		final InterviewDAO interviewDAO = data.getInterview();
		if (!Constants.ROUND_TYPE_INTERNAL.equals(interviewDAO.getInterviewRound()) &&
				interviewDAO.getDuplicateReason() == null) {
			final InterviewConcludedEvent event = InterviewConcludedEvent.builder()
					.interviewId(interviewDAO.getId())
					.interviewStatus(InterviewStatus.CANCELLATION_DONE.getValue())
					.rescheduleCount(interviewDAO.getRescheduleCount())
					.interviewerId(data.getOriginalInterviewerId())
					.cancellationReasonId(data.getDeAllocationReason())
					.cancellationTime(data.getDeAllocationTime())
					.interviewStartDate(interviewDAO.getStartDate())
					.build();
			this.amazonSQS.sendMessage(this.staticAppConfigValues.getExpertPaymentCalculationEventQueueUrl(),
					this.objectMapper.writeValueAsString(event));
		}
	}
}
