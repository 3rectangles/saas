/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.expert_deallocation;

import com.barraiser.onboarding.scheduling.cancellation.SearchNewInterviewForExpertProcessor;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.AssignNewInterviewToOriginalExpertProcessor;
import com.barraiser.onboarding.scheduling.expert_deallocation.dto.ExpertDeAllocatorData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class AssignNewInterviewExpertDeallocationActivity implements ExpertDeallocationSfnActivity {
	private final ObjectMapper objectMapper;
	private final SearchNewInterviewForExpertProcessor searchNewInterviewForExpertProcessor;
	private final AssignNewInterviewToOriginalExpertProcessor assignNewInterviewToOriginalExpertProcessor;

	@Override
	public String name() {
		return "assign-new-interview-expert-deallocation";
	}

	@Override
	public ExpertDeAllocatorData process(String input) throws Exception {
		final ExpertDeAllocatorData data = this.objectMapper.readValue(input, ExpertDeAllocatorData.class);
		this.searchNewInterviewForExpertProcessor.process(data);
		if (data.getNewInterviewThatExpertCanTake() != null) {
			this.assignNewInterviewToOriginalExpertProcessor.process(data);
		}
		return data;
	}
}
