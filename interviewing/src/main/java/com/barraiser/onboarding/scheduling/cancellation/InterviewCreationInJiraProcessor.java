/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.onboarding.dal.JiraUUIDRepository;
import com.barraiser.onboarding.interview.InterviewCreatorInJira;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Arrays;

@Log4j2
@Component
@AllArgsConstructor
public class InterviewCreationInJiraProcessor implements CancellationProcessor {

	private final InterviewCreatorInJira interviewCreatorInJira;
	private final JiraUUIDRepository jiraUUIDRepository;

	@Transactional
	@Override
	public void process(final CancellationProcessingData data) {
		if (!data.getIsNonReschedulableInterview()) {
			this.interviewCreatorInJira.createInterviewsInJira(Arrays.asList(data.getInterviewToBeCancelled()),
					this.jiraUUIDRepository.findByUuid(data.getInterviewToBeCancelled().getEvaluationId()).get()
							.getJira());
		}
	}
}
