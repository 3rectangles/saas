/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.data_science.followupQuestionDetection;

import com.barraiser.common.requests.FollowupQuestionDetectionData;
import com.barraiser.common.requests.FollowupQuestionDetectionRequest;
import com.barraiser.common.responses.FollowupQuestionDetectionResponse;
import com.barraiser.data_science.DataScienceAPIClient;
import com.barraiser.data_science.dal.FollowupQuestionDetectionDAO;
import com.barraiser.data_science.dal.FollowupQuestionDetectionRepository;
import com.barraiser.data_science.DTO.FollowupQuestionDetectionRequestDTO;
import com.barraiser.data_science.DTO.FollowupQuestionDetectionResponseDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@Log4j2
@AllArgsConstructor
public class FollowupQuestionDetector {
	private static final String DS_MODEL_VERSION = "1";

	private final DataScienceAPIClient dataScienceAPIClient;
	private final FollowupQuestionDetectionRepository followupQuestionDetectionRepository;
	private final ObjectMapper objectMapper;

	public FollowupQuestionDetectionResponse detectFollowupForQuestions(
			final FollowupQuestionDetectionRequest request) throws Exception {
		Map<String, Boolean> questionIdToIsFollowUpMap = new HashMap<>();

		for (int index = 1; index < request.getFollowupQuestionDetectionDataList().size(); index++) {
			final FollowupQuestionDetectionData previousQuestionData = request
					.getFollowupQuestionDetectionDataList()
					.get(index - 1);

			final FollowupQuestionDetectionData currentQuestionData = request
					.getFollowupQuestionDetectionDataList()
					.get(index);

			if (questionIdToIsFollowUpMap
					.containsKey(previousQuestionData
							.getQuestionDTO()
							.getId())
					&& questionIdToIsFollowUpMap
							.get(previousQuestionData
									.getQuestionDTO()
									.getId())) {
				continue;
			}

			log.info(String.format(
					"Fetching followup question data for questionId:%s and masterQuestionId:%s",
					currentQuestionData
							.getQuestionDTO()
							.getId(),
					previousQuestionData
							.getQuestionDTO()
							.getId()));

			final FollowupQuestionDetectionRequestDTO requestDTO = this.getFollowupQuestionDetectionRequest(
					previousQuestionData,
					currentQuestionData);

			final FollowupQuestionDetectionResponseDTO responseDTO = this.getFollowupQuestionResponse(requestDTO);

			this.saveFollowupQuestionDetectionToDatabase(
					previousQuestionData,
					currentQuestionData,
					requestDTO,
					responseDTO);

			questionIdToIsFollowUpMap.put(
					previousQuestionData
							.getQuestionDTO()
							.getId(),
					false);

			questionIdToIsFollowUpMap.put(
					currentQuestionData
							.getQuestionDTO()
							.getId(),
					responseDTO
							.getResult()
							.getPrediction()
							.getIsFollowup());
		}

		return FollowupQuestionDetectionResponse
				.builder()
				.questionIdToIsFollowUpMap(questionIdToIsFollowUpMap)
				.build();
	}

	private FollowupQuestionDetectionRequestDTO getFollowupQuestionDetectionRequest(
			final FollowupQuestionDetectionData previousQuestionData,
			final FollowupQuestionDetectionData currentQuestionData) {
		return FollowupQuestionDetectionRequestDTO
				.builder()
				.question(currentQuestionData
						.getQuestionDTO()
						.getQuestion())
				.questionCategory(currentQuestionData
						.getQuestionCategoryDTO()
						.getName())
				.previousQuestion(previousQuestionData
						.getQuestionDTO()
						.getQuestion())
				.previousQuestionCategory(previousQuestionData
						.getQuestionCategoryDTO()
						.getName())
				.build();
	}

	private FollowupQuestionDetectionResponseDTO getFollowupQuestionResponse(
			final FollowupQuestionDetectionRequestDTO requestDTO)
			throws Exception {
		try {
			return this.dataScienceAPIClient
					.getFollowupQuestionDetection(requestDTO);
		} catch (Exception exception) {
			log.error(
					"Unable to detect follow up question from Data Science API",
					exception);

			throw exception;
		}
	}

	private void saveFollowupQuestionDetectionToDatabase(
			final FollowupQuestionDetectionData previousQuestionData,
			final FollowupQuestionDetectionData currentQuestionData,
			final FollowupQuestionDetectionRequestDTO requestDTO,
			final FollowupQuestionDetectionResponseDTO responseDTO) {
		log.info(String.format(
				"Saving followup question data for questionId:%s and masterQuestionId:%s",
				currentQuestionData
						.getQuestionDTO()
						.getId(),
				previousQuestionData
						.getQuestionDTO()
						.getId()));

		final FollowupQuestionDetectionDAO followupQuestionDetectionDAO = FollowupQuestionDetectionDAO
				.builder()
				.id(UUID.randomUUID().toString())
				.interviewId(currentQuestionData
						.getQuestionDTO()
						.getInterviewId())
				.questionId(currentQuestionData
						.getQuestionDTO()
						.getId())
				.masterQuestionId(previousQuestionData
						.getQuestionDTO()
						.getId())
				.isFollowup(responseDTO
						.getResult()
						.getPrediction()
						.getIsFollowup())
				.request(this.objectMapper
						.convertValue(
								requestDTO,
								JsonNode.class))
				.dsModelVersion(DS_MODEL_VERSION)
				.build();

		this.followupQuestionDetectionRepository
				.save(followupQuestionDetectionDAO);
	}
}
