/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.data_science.feedbackRecommendation;

import com.barraiser.common.graphql.input.GetFeedbackRecommendationsInput;
import com.barraiser.common.graphql.types.FeedbackRecommendation;
import com.barraiser.common.graphql.types.FeedbackRecommendations;
import com.barraiser.data_science.DTO.FeedbackRecommendationRequestDTO;
import com.barraiser.data_science.DTO.FeedbackRecommendationResponseDTO;
import com.barraiser.data_science.DataScienceAPIClient;
import com.barraiser.data_science.dal.FeedbackRecommendationDAO;
import com.barraiser.data_science.dal.FeedbackRecommendationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Log4j2
@AllArgsConstructor
public class FeedbackRecommendationGenerator {
	private static final String QUESTION = "question";
	private static final String QUESTION_CATEGORY = "question_category";
	private static final String RATING = "rating";

	private static final Integer MAX_FEEDBACK_RECOMMENDATION_REQUIRED = 5;

	private final DataScienceAPIClient dataScienceAPIClient;
	private final FeedbackRecommendationRepository feedbackRecommendationRepository;
	private final ObjectMapper objectMapper;

	public FeedbackRecommendations generateFeedbackRecommendations(
			final GetFeedbackRecommendationsInput input)
			throws Exception {
		final FeedbackRecommendationResponseDTO responseDTO = this.getFeedbackRecommendationResponse(input);

		final List<FeedbackRecommendation> feedbackRecommendationList = this.getFeedbackRecommendations(responseDTO);

		final FeedbackRecommendations feedbackRecommendations = FeedbackRecommendations
				.builder()
				.feedbackRecommendations(feedbackRecommendationList)
				.build();

		this.saveFeedbackRecommendationsToDatabase(
				input,
				feedbackRecommendations);

		while (feedbackRecommendations.getFeedbackRecommendations().size() > MAX_FEEDBACK_RECOMMENDATION_REQUIRED) {
			feedbackRecommendations
					.getFeedbackRecommendations()
					.remove(feedbackRecommendations.getFeedbackRecommendations().size() - 1);
		}

		return feedbackRecommendations;
	}

	private FeedbackRecommendationResponseDTO getFeedbackRecommendationResponse(
			final GetFeedbackRecommendationsInput input)
			throws Exception {
		try {
			final FeedbackRecommendationRequestDTO requestDTO = FeedbackRecommendationRequestDTO
					.builder()
					.question(input.getQuestion())
					.questionCategory(input.getQuestionCategory())
					.rating(input.getRating().intValue())
					.build();

			return this.dataScienceAPIClient
					.getFeedbackRecommendation(requestDTO);
		} catch (Exception exception) {
			log.error(
					String.format(
							"Unable to fetch FeedbackRecommendation from DS model[question:%s question_category:%s, rating:%s]",
							input.getQuestion(),
							input.getQuestionCategory(),
							input.getRating()),
					exception);

			throw exception;
		}
	}

	private List<FeedbackRecommendation> getFeedbackRecommendations(
			final FeedbackRecommendationResponseDTO responseDTO) {
		final List<FeedbackRecommendation> feedbackRecommendationList = new ArrayList<>();

		for (FeedbackRecommendationResponseDTO.Prediction prediction : responseDTO.getResult().getPredictions()) {
			feedbackRecommendationList
					.add(FeedbackRecommendation
							.builder()
							.id(UUID
									.randomUUID()
									.toString())
							.recommendation(prediction.getText())
							.build());
		}

		return feedbackRecommendationList;
	}

	private void saveFeedbackRecommendationsToDatabase(
			final GetFeedbackRecommendationsInput input,
			final FeedbackRecommendations feedbackRecommendations) throws Exception {
		log.info(String.format(
				"Saving feedback recommendations for interviewId:%s feedbackId:%s to database",
				input.getInterviewId(),
				input.getFeedbackId()));

		final Map<String, Object> requestData = new HashMap<>();
		requestData.put(QUESTION, input.getQuestion());
		requestData.put(QUESTION_CATEGORY, input.getQuestionCategory());
		requestData.put(RATING, input.getRating());

		final JsonNode requestObject = this.objectMapper
				.reader()
				.readTree(this.objectMapper
						.writeValueAsString(requestData));

		for (FeedbackRecommendation feedbackRecommendation : feedbackRecommendations.getFeedbackRecommendations()) {
			final FeedbackRecommendationDAO feedbackRecommendationDAO = FeedbackRecommendationDAO
					.builder()
					.id(feedbackRecommendation.getId())
					.interviewId(input.getInterviewId())
					.feedbackId(input.getFeedbackId())
					.recommendation(feedbackRecommendation.getRecommendation())
					.request(requestObject)
					.build();

			this.feedbackRecommendationRepository
					.save(feedbackRecommendationDAO);
		}
	}
}
