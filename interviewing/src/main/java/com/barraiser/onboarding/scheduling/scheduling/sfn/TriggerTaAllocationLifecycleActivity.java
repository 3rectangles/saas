/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.sfn;

import com.barraiser.onboarding.scheduling.InterviewLifecycleUtil;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingProcessingData;
import com.barraiser.onboarding.scheduling.ta.TaAllocationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Log4j2
@AllArgsConstructor
@Component
public class TriggerTaAllocationLifecycleActivity implements InterviewSchedulingActivity {
	public static final String TRIGGER_TA_ALLOCATION_LIFECYCLE = "trigger-ta-allocation-lifecycle";

	private final InterviewLifecycleUtil interviewLifecycleUtil;
	private final TaAllocationService taAllocationService;
	private final ObjectMapper objectMapper;

	private void startTaAllocationLifecycle(final SchedulingProcessingData data) throws IOException {
		log.info("Starting TA allocation lifecycle for {} ", data);

		this.taAllocationService.startTaAllocationLifecycleExecution(data.getInput().getInterviewId(),
				this.objectMapper.writeValueAsString(data));
	}

	@Override
	public String name() {
		return TRIGGER_TA_ALLOCATION_LIFECYCLE;
	}

	@Override
	public SchedulingProcessingData process(String input) throws Exception {
		final SchedulingProcessingData data = objectMapper.readValue(input, SchedulingProcessingData.class);
		final String interviewId = data.getInput().getInterviewId();

		if (data.getIsTAAutoAllocationNeeded()) {
			log.info("Started Ta Allocation for interview id:{}", interviewId);
			final Boolean stepFunctionEnabled = this.interviewLifecycleUtil
					.checkIfStepFunctionIsEnabledForTaAllocation(interviewId);

			if (stepFunctionEnabled) {
				this.startTaAllocationLifecycle(data);
			} else {
				log.info("TA Allocation lifecycle is disabled");
			}
		}
		return data;
	}
}
