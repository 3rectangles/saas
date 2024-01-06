/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.add_evaluation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@AllArgsConstructor
public class AddCandidateTransactionOrchestrator {
	private final CognitoUserManagementProcessor cognitoUserManagementProcessor;
	private final DatabaseUserManagementProcessor databaseUserManagementProcessor;
	private final CreateEvaluationInDatabaseProcessor createEvaluationInDatabaseProcessor;
	private final CreateInterviewInDatabaseProcessor createInterviewInDatabaseProcessor;

	@Transactional
	public void orchestrate(final AddEvaluationProcessingData data) {

		if (data.getIsCandidateAnonymous() == null || !data.getIsCandidateAnonymous()) {
			this.cognitoUserManagementProcessor.process(data);
		}

		this.databaseUserManagementProcessor.process(data);
		this.createEvaluationInDatabaseProcessor.process(data);
		this.createInterviewInDatabaseProcessor.process(data);
	}
}
