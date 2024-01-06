/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.data_science.endpoint;

import com.barraiser.common.graphql.input.GetFeedbackRecommendationsInput;
import com.barraiser.common.graphql.input.GetJobRoleSkillsValuesInput;
import com.barraiser.common.graphql.input.GetOverallFeedbackSuggestionsInput;
import com.barraiser.common.graphql.types.FeedbackRecommendations;
import com.barraiser.common.graphql.types.GetJobRoleSkillsAndValues;
import com.barraiser.common.graphql.types.OverallFeedbackSuggestions;
import com.barraiser.common.requests.CancellationPredictionRequest;
import com.barraiser.common.requests.FollowupQuestionDetectionRequest;
import com.barraiser.common.requests.QuestionCategoryPredictionRequest;
import com.barraiser.common.responses.CancellationPredictionResponse;
import com.barraiser.common.responses.FollowupQuestionDetectionResponse;
import com.barraiser.common.responses.QuestionCategoryPredictionResponse;
import com.barraiser.data_science.CancellationPredictionFeignClient;
import com.barraiser.data_science.feedbackRecommendation.FeedbackRecommendationGenerator;
import com.barraiser.data_science.followupQuestionDetection.FollowupQuestionDetector;
import com.barraiser.data_science.interviewStructureSkillsValues.InterviewStructureSkillsValuesGenerator;
import com.barraiser.data_science.questionCategoryPrediction.QuestionCategoryPredictor;
import com.barraiser.data_science.overallFeedbackSuggestions.OverallFeedbackSuggestionsGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Log4j2
@AllArgsConstructor
public class DataScienceController {
	private final FeedbackRecommendationGenerator feedbackRecommendationGenerator;
	private final QuestionCategoryPredictor questionCategoryPredictor;
	private final FollowupQuestionDetector followupQuestionDetector;
	private final CancellationPredictionFeignClient cancellationPredictionFeignClient;
	private final OverallFeedbackSuggestionsGenerator overallFeedbackSuggestionsGenerator;
	private final InterviewStructureSkillsValuesGenerator interviewStructureSkillsValuesGenerator;

	@PostMapping(value = "/generate_feedback_recommendation", produces = "application/json", consumes = "application/json")
	ResponseEntity<FeedbackRecommendations> generateFeedbackRecommendations(
			@RequestBody final GetFeedbackRecommendationsInput input)
			throws Exception {
		return ResponseEntity
				.ok()
				.body(this.feedbackRecommendationGenerator
						.generateFeedbackRecommendations(input));
	}

	@PostMapping(value = "/predict_question_categories", produces = "application/json", consumes = "application/json")
	ResponseEntity<QuestionCategoryPredictionResponse> predictCategoriesOfQuestions(
			@RequestBody final QuestionCategoryPredictionRequest request) throws Exception {
		return ResponseEntity
				.ok()
				.body(this.questionCategoryPredictor
						.predictCategoryForQuestions(request));
	}

	@PostMapping(value = "/detect_followup_question", produces = "application/json", consumes = "application/json")
	ResponseEntity<FollowupQuestionDetectionResponse> detectFollowupForQuestions(
			@RequestBody final FollowupQuestionDetectionRequest request) throws Exception {
		return ResponseEntity
				.ok()
				.body(this.followupQuestionDetector
						.detectFollowupForQuestions(request));
	}

	@PostMapping(value = "/get_cancellation_probability", produces = "application/json", consumes = "application/json")
	ResponseEntity<CancellationPredictionResponse> getCancellationProbability(
			@RequestBody final CancellationPredictionRequest request) {
		return ResponseEntity
				.ok()
				.body(this.cancellationPredictionFeignClient
						.getCancellationProbability(request));
	}

	@PostMapping(value = "/generate_overall_feedback_suggestions", produces = "application/json", consumes = "application/json")
	ResponseEntity<OverallFeedbackSuggestions> generateOverallFeedbackSuggestions(
			@RequestBody final GetOverallFeedbackSuggestionsInput input)
			throws Exception {
		return ResponseEntity
				.ok()
				.body(this.overallFeedbackSuggestionsGenerator
						.generateOverallFeedbackSuggestions(input));
	}

	@PostMapping(value = "/generate_skills_and_values", produces = "application/json", consumes = "application/json")
	ResponseEntity<GetJobRoleSkillsAndValues> generateSkillsAndValues(
			@RequestBody final GetJobRoleSkillsValuesInput input) throws Exception {
		return ResponseEntity.ok()
				.body(this.interviewStructureSkillsValuesGenerator.generateInterviewStructureSkillsValues(input));
	}
}
