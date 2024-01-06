/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment.sfn_activities;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.expert.ExpertReminderManagementService;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO.ExpertAllocatorData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class TriggerExpertReminderLifecycleActivity implements ExpertAllocatorSfnActivity {
	private final ObjectMapper objectMapper;
	private final ExpertReminderManagementService expertReminderManagementService;
	private final InterViewRepository interViewRepository;

	@Override
	public String name() {
		return "trigger-expert-reminder-lifecycle";
	}

	@Override
	public ExpertAllocatorData process(final String input) throws Exception {
		final ExpertAllocatorData data = this.objectMapper.readValue(input, ExpertAllocatorData.class);
		final InterviewDAO interviewDAO = this.interViewRepository.findById(data.getInterviewId()).get();
		this.expertReminderManagementService.startExpertReminder(interviewDAO);
		return data;
	}
}
