/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.data_science;

import com.barraiser.data_science.DTO.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Log4j2
@AllArgsConstructor
public class DataScienceAPIClient {
	private static final String STRENGTHS = "STRENGTHS";
	private static final String AREAS_OF_IMPROVEMENTS = "AREAS_OF_IMPROVEMENTS";

	private final FeedbackRecommendationFeignClient feedbackRecommendationFeignClient;
	private final FollowupQuestionDetectionFeignClient followupQuestionDetectionFeignClient;
	private final OverallFeedbackSuggestionsClient overallFeedbackSuggestionsClient;
	private final QuestionCategoryPredictionFeignClient questionCategoryPredictionFeignClient;
	private final DataScienceCacheManager dataScienceCacheManager;

	public FeedbackRecommendationResponseDTO getFeedbackRecommendation(
			final FeedbackRecommendationRequestDTO requestDTO)
			throws Exception {
		FeedbackRecommendationResponseDTO responseDTO = this.dataScienceCacheManager
				.getResponseFromCache(
						requestDTO,
						FeedbackRecommendationResponseDTO.class);

		if (responseDTO != null) {
			return responseDTO;
		}

		responseDTO = this.feedbackRecommendationFeignClient
				.getFeedbackRecommendation(requestDTO)
				.getBody();

		this.dataScienceCacheManager
				.addResponseToCache(requestDTO, responseDTO);

		return responseDTO;
	}

	public FollowupQuestionDetectionResponseDTO getFollowupQuestionDetection(
			final FollowupQuestionDetectionRequestDTO requestDTO)
			throws Exception {
		FollowupQuestionDetectionResponseDTO responseDTO = this.dataScienceCacheManager
				.getResponseFromCache(
						requestDTO,
						FollowupQuestionDetectionResponseDTO.class);

		if (responseDTO != null) {
			return responseDTO;
		}

		responseDTO = this.followupQuestionDetectionFeignClient
				.getFollowupQuestionDetection(requestDTO)
				.getBody();

		this.dataScienceCacheManager
				.addResponseToCache(requestDTO, responseDTO);

		return responseDTO;
	}

	public QuestionCategoryPredictionResponseDTO getQuestionCategoryPrediction(
			final QuestionCategoryPredictionRequestDTO requestDTO)
			throws Exception {
		QuestionCategoryPredictionResponseDTO responseDTO = this.dataScienceCacheManager
				.getResponseFromCache(
						requestDTO,
						QuestionCategoryPredictionResponseDTO.class);

		if (responseDTO != null) {
			return responseDTO;
		}

		responseDTO = this.questionCategoryPredictionFeignClient
				.getQuestionCategoryPrediction(requestDTO)
				.getBody();

		this.dataScienceCacheManager
				.addResponseToCache(requestDTO, responseDTO);

		return responseDTO;
	}

	public OverallFeedbackSuggestionResponseDTO getOverallFeedbackSuggestion(
			final List<OverallFeedbackSuggestionRequestDTO> requestDTOList,
			final String type)
			throws Exception {
		final Map<String, Object> cacheRequest = Map.of("requestDTOList", requestDTOList, "type", type);
		OverallFeedbackSuggestionResponseDTO responseDTO = this.dataScienceCacheManager
				.getResponseFromCache(cacheRequest, OverallFeedbackSuggestionResponseDTO.class);

		if (responseDTO != null) {
			return responseDTO;
		}

		if (STRENGTHS.equals(type)) {
			responseDTO = this.overallFeedbackSuggestionsClient
					.fetchStrengthFeedbackSuggestion(requestDTOList)
					.getBody();
		}

		if (AREAS_OF_IMPROVEMENTS.equals(type)) {
			responseDTO = this.overallFeedbackSuggestionsClient
					.fetchAresOfImprovementsFeedbackSuggestion(requestDTOList)
					.getBody();
		}

		this.dataScienceCacheManager
				.addResponseToCache(cacheRequest, responseDTO);

		return responseDTO;
	}
}
