/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dataScience;

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
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "data-science-feign-client", url = "http://localhost:5000")
public interface DataScienceFeignClient {
	@PostMapping(value = "/generate_feedback_recommendation", produces = "application/json", consumes = "application/json")
	ResponseEntity<FeedbackRecommendations> generateFeedbackRecommendations(
			@RequestBody final GetFeedbackRecommendationsInput input);

	@PostMapping(value = "/predict_question_categories", produces = "application/json", consumes = "application/json")
	ResponseEntity<QuestionCategoryPredictionResponse> predictCategoriesOfQuestions(
			@RequestBody final QuestionCategoryPredictionRequest request);

	@PostMapping(value = "/detect_followup_question", produces = "application/json", consumes = "application/json")
	ResponseEntity<FollowupQuestionDetectionResponse> detectFollowupForQuestions(
			@RequestBody final FollowupQuestionDetectionRequest request);

	@PostMapping(value = "/get_cancellation_probability", produces = "application/json", consumes = "application/json")
	ResponseEntity<CancellationPredictionResponse> getCancellationProbability(
			@RequestBody final CancellationPredictionRequest request);

	@PostMapping(value = "/generate_overall_feedback_suggestions", produces = "application/json", consumes = "application/json")
	ResponseEntity<OverallFeedbackSuggestions> generateOverallFeedbackSuggestions(
			@RequestBody final GetOverallFeedbackSuggestionsInput input);

	@PostMapping(value = "/generate_skills_and_values", produces = "application/json", consumes = "application/json")
	ResponseEntity<GetJobRoleSkillsAndValues> generateSkillsAndValues(
			@RequestBody final GetJobRoleSkillsValuesInput input);
}
