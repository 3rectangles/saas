/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.add_evaluation;

import com.barraiser.onboarding.jobRoleManagement.JobRole.JobRoleEvaluationStatisticsHandler;
import com.barraiser.onboarding.user.CandidateEventGenerator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AddEvaluation {
	private final DataValidationProcessor dataValidationProcessor;
	private final CreateEvaluationInJiraProcessor createEvaluationInJiraProcessor;
	private final CreateInterviewInJiraProcessor createInterviewInJiraProcessor;
	private final AddCandidateTransactionOrchestrator addCandidateTransactionOrchestrator;
	private final CandidateEventGenerator candidateEventGenerator;
	private final JobRoleEvaluationStatisticsHandler jobRoleEvaluationStatisticsHandler;

	public void add(final AddEvaluationProcessingData data) {

		this.dataValidationProcessor.process(data);
		if (!data.getResult().getSuccess()) {
			return;
		}
		this.addCandidateTransactionOrchestrator.orchestrate(data);
		// todo: evaluation job role id and version are required fields in db
		this.performJiraActions(data);

		if (data.getJobRoleDAO() != null) {
			this.candidateEventGenerator.sendCandidateEventForAddition(data);
			this.jobRoleEvaluationStatisticsHandler
					.addActiveCandidateCount(data.getJobRoleDAO().getEntityId().getId(),
							data.getJobRoleDAO().getEntityId().getVersion());
		}

	}

	public void performJiraActions(final AddEvaluationProcessingData data) {
		this.createEvaluationInJiraProcessor.process(data);
		this.createInterviewInJiraProcessor.process(data);
	}

}
