/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.responses.FollowupQuestionDetectionResponse;
import com.barraiser.common.responses.QuestionCategoryPredictionResponse;
import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.EventListener;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.question_tagging_submission_event.QuestionTaggingSubmissionEvent;
import com.barraiser.onboarding.dataScience.FollowupQuestionPredictor;
import com.barraiser.onboarding.dataScience.QuestionCategoryPredictionGenerator;
import com.barraiser.onboarding.events.InterviewingConsumer;
import com.barraiser.onboarding.interview.feeback.InterviewFeedbackVersionFetcher;
import com.barraiser.onboarding.interview.pojo.InterviewQuestionDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
@AllArgsConstructor
public class FollowupQuestionDetectionHandler
		implements EventListener<InterviewingConsumer> {
	private final ObjectMapper objectMapper;
	private final FollowupQuestionMarker followupQuestionMarker;
	private final InterviewQuestionDetailsFetcher interviewQuestionDetailsFetcher;
	private final QuestionCategoryPredictionGenerator questionCategoryPredictionGenerator;
	private final FollowupQuestionPredictor followupQuestionPredictor;
	private final PredictedQuestionCategoryFirestorePopulator predictedQuestionCategoryFirestorePopulator;
	private final InterviewFeedbackVersionFetcher interviewFeedbackVersionFetcher;

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
		if (this.interviewFeedbackVersionFetcher.getFeedbackVersion(interviewId) > 0) {
			return;
		}

		log.info(String.format(
				"Detecting followup questions for interviewId:%s after submission of questions",
				questionTaggingSubmissionEvent.getInterview().getId()));

		this.predictAndMarkFollowupQuestionsForInterview(questionTaggingSubmissionEvent
				.getInterview()
				.getId());
	}

	public void predictAndMarkFollowupQuestionsForInterview(final String interviewId) {
		try {
			final InterviewQuestionDetails interviewQuestionDetails = this.interviewQuestionDetailsFetcher
					.getInterviewQuestionDetails(interviewId);

			final QuestionCategoryPredictionResponse questionCategoryPredictionResponse = this.questionCategoryPredictionGenerator
					.predictQuestionCategories(interviewQuestionDetails);

			this.predictedQuestionCategoryFirestorePopulator
					.addPredictedQuestionCategoryToQuestionsInFirestore(
							interviewQuestionDetails.getInterviewId(),
							questionCategoryPredictionResponse);

			final FollowupQuestionDetectionResponse followupQuestionDetectionResponse = this.followupQuestionPredictor
					.predictFollowupQuestions(
							interviewQuestionDetails,
							questionCategoryPredictionResponse);

			this.followupQuestionMarker
					.markFollowupQuestionsForInterview(
							interviewQuestionDetails.getInterviewId(),
							followupQuestionDetectionResponse);
		} catch (Exception exception) {
			log.error(
					String.format(
							"Unable to mark followup questions for interviewId:%s on Firestore",
							interviewId,
							exception));
		}
	}
}
