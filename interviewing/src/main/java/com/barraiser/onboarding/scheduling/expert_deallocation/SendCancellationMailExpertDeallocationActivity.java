/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.expert_deallocation;

import com.barraiser.onboarding.communication.InterviewCancellationCommunicationService;
import com.barraiser.onboarding.scheduling.expert_deallocation.dto.ExpertDeAllocatorData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Log4j2
public class SendCancellationMailExpertDeallocationActivity implements ExpertDeallocationSfnActivity {
	private final InterviewCancellationCommunicationService interviewCancellationCommunicationService;
	private final ObjectMapper objectMapper;

	@Override
	public String name() {
		return "send-interview-cancellation-mail-to-expert";
	}

	@Override
	public ExpertDeAllocatorData process(String input) throws Exception {
		final ExpertDeAllocatorData data = this.objectMapper.readValue(input, ExpertDeAllocatorData.class);
		if (data.getNewInterviewThatExpertCanTake() != null) {
			return data;
		}

		this.interviewCancellationCommunicationService.communicateCancellationToExpert(data.getInterview(),
				data.getOriginalInterviewerId());
		return data;
	}
}
