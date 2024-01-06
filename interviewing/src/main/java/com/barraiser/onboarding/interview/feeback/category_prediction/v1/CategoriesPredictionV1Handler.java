/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.feeback.category_prediction.v1;

import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.EventListener;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.question_tagging_submission_event.QuestionTaggingSubmissionEvent;
import com.barraiser.onboarding.events.InterviewingConsumer;
import com.barraiser.onboarding.interview.feeback.InterviewFeedbackVersionFetcher;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@Log4j2
@AllArgsConstructor
public class CategoriesPredictionV1Handler implements EventListener<InterviewingConsumer> {
	private final ObjectMapper objectMapper;
	private final InterviewFeedbackVersionFetcher interviewFeedbackVersionFetcher;
	private final InterviewFeedbackV1CategoriesPredictor categoriesPredictor;

	@Override
	public List<Class> eventsToListen() {
		return List.of(QuestionTaggingSubmissionEvent.class);
	}

	@Override
	public void handleEvent(Event event) throws Exception {
		final QuestionTaggingSubmissionEvent questionTaggingSubmissionEvent = this.objectMapper.convertValue(
				event.getPayload(),
				QuestionTaggingSubmissionEvent.class);
		final String interviewId = questionTaggingSubmissionEvent.getInterview().getId();
		if (shouldPredict(interviewId)) {
			this.categoriesPredictor.predictCategories(interviewId);
		}
	}

	private boolean shouldPredict(final String interviewId) {
		return this.interviewFeedbackVersionFetcher.getFeedbackVersion(interviewId) == 1;
	}
}
