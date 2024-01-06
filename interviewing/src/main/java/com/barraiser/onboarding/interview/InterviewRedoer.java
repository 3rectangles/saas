/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.JiraUUIDRepository;
import com.barraiser.onboarding.interview.evaluation.BgsDataGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Log4j2
@Component
@AllArgsConstructor
public class InterviewRedoer {
	public static final String SOURCE = "REDO_INTERVIEW";

	private final InterviewService interviewService;
	private final InterviewCreatorInJira interviewCreatorInJira;
	private final JiraUUIDRepository jiraUUIDRepository;
	private final InterviewCreatorInDB interviewCreatorInDB;
	private final BgsDataGenerator bgsDataGenerator;

	public void redoInterview(final InterviewDAO interviewDAO, final String redoReasonId, final String redoRequester) {
		this.interviewService.save(
				interviewDAO.toBuilder().redoReasonId(redoReasonId).build(), redoRequester,
				SOURCE);
		final InterviewDAO newInterview = this.interviewCreatorInDB
				.createInterviewInDatabase(interviewDAO.getEvaluationId(), interviewDAO.getInterviewStructureId(),
						redoRequester);
		this.resetBgsData(interviewDAO.getEvaluationId());
		this.interviewCreatorInJira.createInterviewsInJira(Arrays.asList(newInterview),
				this.jiraUUIDRepository.findByUuid(interviewDAO.getEvaluationId()).get().getJira());
	}

	private void resetBgsData(final String evaluationId) {
		this.bgsDataGenerator.generateBgsDataEvaluation(evaluationId);
	}
}
