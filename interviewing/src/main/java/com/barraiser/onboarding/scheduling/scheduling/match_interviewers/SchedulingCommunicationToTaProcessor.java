/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers;

import com.barraiser.onboarding.communication.InterviewSchedulingCommunicationService;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingCommunicationData;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingProcessingData;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingProcessor;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Log4j2
@Component
@AllArgsConstructor
public class SchedulingCommunicationToTaProcessor implements SchedulingProcessor {
	private final InterviewSchedulingCommunicationService interviewSchedulingCommunicationService;

	@Override
	public void process(SchedulingProcessingData data) throws IOException {
		if (!data.getExecuteTaAssignment() || !data.getIsTaAllocated())
			return;
		final SchedulingCommunicationData schedulingEmailData = data.getSchedulingCommunicationData();
		this.interviewSchedulingCommunicationService
				.sendEmailToTa(schedulingEmailData);

	}
}
