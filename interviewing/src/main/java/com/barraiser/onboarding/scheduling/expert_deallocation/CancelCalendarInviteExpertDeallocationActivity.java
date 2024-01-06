/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.expert_deallocation;

import com.barraiser.onboarding.communication.client.CalendaringServiceClient;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.scheduling.expert_deallocation.dto.ExpertDeAllocatorData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Log4j2
public class CancelCalendarInviteExpertDeallocationActivity implements ExpertDeallocationSfnActivity {
	private final CalendaringServiceClient calendaringServiceClient;
	private final ObjectMapper objectMapper;

	@Override
	public String name() {
		return "cancel-calendar-invite-of-expert";
	}

	@Override
	public ExpertDeAllocatorData process(String input) throws Exception {
		final ExpertDeAllocatorData data = this.objectMapper.readValue(input, ExpertDeAllocatorData.class);
		if (data.getNewInterviewThatExpertCanTake() != null) {
			return data;
		}

		final InterviewDAO interview = data.getInterview();
		this.calendaringServiceClient
				.cancelCalendarInviteForUserIdAndEntityWithRescheduleCount(interview.getId(),
						data.getOriginalInterviewerId(),
						interview.getRescheduleCount());
		return data;
	}
}
