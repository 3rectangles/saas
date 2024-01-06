/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.add_evaluation;

import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.InterviewCreatorInDB;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@AllArgsConstructor
@Component
public class CreateInterviewInDatabaseProcessor implements AddEvaluationProcessor {

	private final InterviewCreatorInDB interviewCreatorInDB;
	private final EvaluationRepository evaluationRepository;

	@Override
	public void process(final AddEvaluationProcessingData data) {
		if (data.getJobRoleDAO() != null) { // todo: null
			this.createInterviews(data);
		}
	}

	private void createInterviews(final AddEvaluationProcessingData data) {
		final String createdBy = data.getAuthenticatedUser().getUserName();
		final EvaluationDAO evaluationDAO = this.evaluationRepository.findById(data.getEvaluationId()).get();
		final List<InterviewDAO> interviewDAOS = this.interviewCreatorInDB.createInterviewInDatabase(evaluationDAO, -1,
				createdBy);
		data.setInterviewDAOs(interviewDAOS);
	}

}
