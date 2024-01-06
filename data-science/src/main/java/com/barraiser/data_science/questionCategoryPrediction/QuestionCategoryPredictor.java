/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.data_science.questionCategoryPrediction;

import com.barraiser.common.DTO.QuestionCategoryDTO;
import com.barraiser.common.requests.QuestionCategoryPredictionRequest;
import com.barraiser.common.DTO.QuestionDTO;
import com.barraiser.common.responses.QuestionCategoryPredictionResponse;
import com.barraiser.data_science.DTO.QuestionCategoryPredictionRequestDTO;
import com.barraiser.data_science.DTO.QuestionCategoryPredictionResponseDTO;
import com.barraiser.data_science.DataScienceAPIClient;
import com.barraiser.data_science.dal.QuestionCategoryPredictionDAO;
import com.barraiser.data_science.dal.QuestionCategoryPredictionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Log4j2
@AllArgsConstructor
public class QuestionCategoryPredictor {
	private static final String DS_MODEL_VERSION = "1";

	private final DataScienceAPIClient dataScienceAPIClient;
	private final QuestionCategoryPredictionRepository questionCategoryPredictionRepository;
	private final ObjectMapper objectMapper;

	public QuestionCategoryPredictionResponse predictCategoryForQuestions(
			final QuestionCategoryPredictionRequest questionCategoryPredictionRequest) throws Exception {
		Map<String, QuestionCategoryDTO> questionIdToQuestionCategoryDTOMap = new HashMap<>();

		List<String> questionCategoryNames = questionCategoryPredictionRequest.getQuestionCategories()
				.stream()
				.map(interviewCategory -> interviewCategory.getName())
				.collect(Collectors.toList());

		for (QuestionDTO question : questionCategoryPredictionRequest.getQuestions()) {
			log.info(String.format(
					"Predicting question category for question:%s",
					question.getId()));

			final QuestionCategoryDTO predictedQuestionCategoryDTO = this.getPredictedCategoryForQuestion(
					question,
					questionCategoryPredictionRequest.getQuestionCategories(),
					questionCategoryNames);

			questionIdToQuestionCategoryDTOMap.put(
					question.getId(),
					predictedQuestionCategoryDTO);
		}

		return QuestionCategoryPredictionResponse
				.builder()
				.questionIdToQuestionCategoryMap(questionIdToQuestionCategoryDTOMap)
				.build();
	}

	private QuestionCategoryDTO getPredictedCategoryForQuestion(
			final QuestionDTO question,
			final List<QuestionCategoryDTO> questionCategories,
			final List<String> questionCategoryNames) throws Exception {
		final QuestionCategoryPredictionRequestDTO requestDTO = this.getQuestionCategoryPredictionRequest(
				question,
				questionCategoryNames);

		final QuestionCategoryPredictionResponseDTO responseDTO = this
				.getQuestionCategoryPredictionResponse(requestDTO);

		final String predictedQuestionCategory = responseDTO
				.getResult()
				.getPrediction()
				.getPredictedCategory();

		final QuestionCategoryDTO predictedQuestionCategoryDTO = questionCategories
				.stream()
				.filter(interviewCategory -> predictedQuestionCategory
						.equals(interviewCategory.getName()))
				.findFirst()
				.get();

		this.saveQuestionCategoryPredictionToDatabase(
				question,
				predictedQuestionCategoryDTO,
				requestDTO);

		return predictedQuestionCategoryDTO;
	}

	private QuestionCategoryPredictionRequestDTO getQuestionCategoryPredictionRequest(
			final QuestionDTO question,
			final List<String> questionCategories) {
		return QuestionCategoryPredictionRequestDTO
				.builder()
				.question(question.getQuestion())
				.categories(questionCategories)
				.build();
	}

	private QuestionCategoryPredictionResponseDTO getQuestionCategoryPredictionResponse(
			final QuestionCategoryPredictionRequestDTO requestDTO) throws Exception {
		try {
			return this.dataScienceAPIClient
					.getQuestionCategoryPrediction(requestDTO);
		} catch (Exception exception) {
			log.error(
					"Unable to predict Question Category using DS API for question",
					exception);

			throw exception;
		}
	}

	public void saveQuestionCategoryPredictionToDatabase(
			final QuestionDTO questionDTO,
			final QuestionCategoryDTO predictedQuestionCategoryDTO,
			final com.barraiser.data_science.DTO.QuestionCategoryPredictionRequestDTO requestDTO) {
		log.info(String.format(
				"Saving question category prediction data for questionId:%s",
				questionDTO.getId()));

		final QuestionCategoryPredictionDAO questionCategoryPredictionDAO = QuestionCategoryPredictionDAO
				.builder()
				.id(UUID.randomUUID().toString())
				.interviewId(questionDTO.getInterviewId())
				.questionId(questionDTO.getId())
				.predictedCategoryId(predictedQuestionCategoryDTO.getId())
				.request(this.objectMapper
						.convertValue(
								requestDTO,
								JsonNode.class))
				.dsModelVersion(DS_MODEL_VERSION)
				.build();

		this.questionCategoryPredictionRepository
				.save(questionCategoryPredictionDAO);
	}
}
