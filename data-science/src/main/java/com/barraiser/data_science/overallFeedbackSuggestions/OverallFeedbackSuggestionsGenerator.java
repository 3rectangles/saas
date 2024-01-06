/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.data_science.overallFeedbackSuggestions;

import com.barraiser.common.graphql.input.GetOverallFeedbackSuggestionsInput;
import com.barraiser.common.graphql.types.FeedbackSuggestion;
import com.barraiser.common.graphql.types.OverallFeedbackSuggestion;
import com.barraiser.common.graphql.types.OverallFeedbackSuggestions;
import com.barraiser.data_science.DTO.OverallFeedbackSuggestionRequestDTO;
import com.barraiser.data_science.DTO.OverallFeedbackSuggestionResponseDTO;
import com.barraiser.data_science.DataScienceAPIClient;
import com.barraiser.data_science.dal.OverallFeedbackSuggestionDAO;
import com.barraiser.data_science.dal.OverallFeedbackSuggestionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Log4j2
@AllArgsConstructor
public class OverallFeedbackSuggestionsGenerator {
	private static final String STRENGTHS = "STRENGTHS";
	private static final String AREAS_OF_IMPROVEMENTS = "AREAS_OF_IMPROVEMENTS";

	private final DataScienceAPIClient dataScienceAPIClient;
	private final OverallFeedbackSuggestionRepository overallFeedbackSuggestionRepository;
	private final ObjectMapper objectMapper;

	public OverallFeedbackSuggestions generateOverallFeedbackSuggestions(
			final GetOverallFeedbackSuggestionsInput input) throws Exception {
		log.info(String.format(
				"Generating overall feedback suggestions for InterviewId:%s",
				input.getInterviewId()));

		final List<OverallFeedbackSuggestion> overallFeedbackSuggestionList = new ArrayList<>();

		overallFeedbackSuggestionList
				.add(this.getOverallFeedbackSuggestion(
						input,
						STRENGTHS));

		overallFeedbackSuggestionList
				.add(this.getOverallFeedbackSuggestion(
						input,
						AREAS_OF_IMPROVEMENTS));

		return OverallFeedbackSuggestions
				.builder()
				.suggestions(overallFeedbackSuggestionList)
				.build();
	}

	private OverallFeedbackSuggestion getOverallFeedbackSuggestion(
			final GetOverallFeedbackSuggestionsInput input,
			final String type) throws Exception {
		log.info(String.format(
				"Generating overall feedback suggestions for interviewId:%s type:%s",
				input.getInterviewId(), type));

		final OverallFeedbackSuggestionResponseDTO responseDTO = this.getOverallFeedbackSuggestionResponse(
				input,
				type);

		final OverallFeedbackSuggestion suggestion = OverallFeedbackSuggestion
				.builder()
				.type(type)
				.feedbackSuggestions(responseDTO
						.getResult()
						.getPredictions()
						.stream()
						.map(prediction -> FeedbackSuggestion
								.builder()
								.id(UUID.randomUUID().toString())
								.suggestion(prediction)
								.build())
						.collect(Collectors.toList()))
				.build();

		this.saveOverallFeedbackSuggestionToDatabase(
				input,
				suggestion,
				type);

		return suggestion;
	}

	public OverallFeedbackSuggestionResponseDTO getOverallFeedbackSuggestionResponse(
			final GetOverallFeedbackSuggestionsInput input,
			final String type)
			throws Exception {
		final List<OverallFeedbackSuggestionRequestDTO> requestDTOList = input
				.getFeedbackDetails()
				.stream()
				.map(feedbackDetailsInput -> OverallFeedbackSuggestionRequestDTO
						.builder()
						.question(feedbackDetailsInput.getQuestion())
						.feedback(feedbackDetailsInput.getFeedback())
						.rating(feedbackDetailsInput.getRating())
						.difficulty(feedbackDetailsInput.getDifficulty())
						.build())
				.collect(Collectors.toList());

		OverallFeedbackSuggestionResponseDTO responseDTO;
		try {
			responseDTO = this.dataScienceAPIClient
					.getOverallFeedbackSuggestion(requestDTOList, type);
		} catch (final Exception exception) {
			log.error(
					String.format(
							"Unable to fetch Overall FeedbackSuggestion of type:%s from DS model[interviewId:%s]",
							type,
							input.getInterviewId()),
					exception);

			throw exception;
		}

		return responseDTO;
	}

	private void saveOverallFeedbackSuggestionToDatabase(
			final GetOverallFeedbackSuggestionsInput input,
			final OverallFeedbackSuggestion suggestion,
			final String type) {
		log.info(String.format(
				"Saving overall feedback suggestions to database for interviewId:%s type:%s",
				input.getInterviewId(),
				type));

		final JsonNode requestData = this.objectMapper
				.convertValue(
						input.getFeedbackDetails(),
						JsonNode.class);

		suggestion.getFeedbackSuggestions().forEach(feedbackSuggestion -> {
			final OverallFeedbackSuggestionDAO overallFeedbackSuggestionDAO = OverallFeedbackSuggestionDAO
					.builder()
					.id(feedbackSuggestion.getId())
					.interviewId(input.getInterviewId())
					.type(type)
					.suggestion(feedbackSuggestion.getSuggestion())
					.request(requestData)
					.build();

			this.overallFeedbackSuggestionRepository
					.save(overallFeedbackSuggestionDAO);
		});
	}
}
