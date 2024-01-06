/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.add_evaluation;

import com.barraiser.onboarding.interview.InterviewCreatorInJira;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * TBD: Link to evaluation pending
 */
@AllArgsConstructor
@Component
public class CreateInterviewInJiraProcessor implements AddEvaluationProcessor {
	private final InterviewCreatorInJira interviewCreatorInJira;

	@Override
	public void process(final AddEvaluationProcessingData data) {
		if (data.getJobRoleDAO() != null) {
			this.interviewCreatorInJira.createInterviewsInJira(data.getInterviewDAOs(),
					data.getEvaluationJiraKey());
		}
	}

}
