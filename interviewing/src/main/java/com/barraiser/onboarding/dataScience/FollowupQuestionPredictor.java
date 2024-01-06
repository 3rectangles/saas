/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dataScience;

import com.barraiser.common.requests.FollowupQuestionDetectionData;
import com.barraiser.common.requests.FollowupQuestionDetectionRequest;
import com.barraiser.common.DTO.QuestionCategoryDTO;
import com.barraiser.common.DTO.QuestionDTO;
import com.barraiser.common.responses.FollowupQuestionDetectionResponse;
import com.barraiser.common.responses.QuestionCategoryPredictionResponse;
import com.barraiser.onboarding.dal.QuestionDAO;
import com.barraiser.onboarding.interview.pojo.InterviewQuestionDetails;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Log4j2
@AllArgsConstructor
public class FollowupQuestionPredictor {
	private final DataScienceFeignClient dataScienceFeignClient;

	public FollowupQuestionDetectionResponse predictFollowupQuestions(
			final InterviewQuestionDetails interviewQuestionDetails,
			final QuestionCategoryPredictionResponse questionCategoryPredictionResponse) {
		log.info(String.format(
				"Predicting followup questions for interviewId:%s",
				interviewQuestionDetails.getInterviewId()));

		final List<FollowupQuestionDetectionData> followupQuestionDetectionDataList = this
				.getFollowupQuestionDetectionData(
						interviewQuestionDetails,
						questionCategoryPredictionResponse);

		return this.dataScienceFeignClient
				.detectFollowupForQuestions(FollowupQuestionDetectionRequest
						.builder()
						.followupQuestionDetectionDataList(followupQuestionDetectionDataList)
						.build())
				.getBody();
	}

	private List<FollowupQuestionDetectionData> getFollowupQuestionDetectionData(
			final InterviewQuestionDetails interviewQuestionDetails,
			final QuestionCategoryPredictionResponse questionCategoryPredictionResponse) {
		return interviewQuestionDetails
				.getQuestions()
				.stream()
				.map(questionDAO -> FollowupQuestionDetectionData
						.builder()
						.questionDTO(this.getQuestion(questionDAO))
						.questionCategoryDTO(this.getQuestionCategory(
								questionDAO,
								questionCategoryPredictionResponse))
						.build())
				.collect(Collectors.toList());

	}

	private QuestionDTO getQuestion(final QuestionDAO questionDAO) {
		return QuestionDTO
				.builder()
				.id(questionDAO.getId())
				.interviewId(questionDAO.getInterviewId())
				.question(questionDAO.getQuestion())
				.build();
	}

	private QuestionCategoryDTO getQuestionCategory(
			final QuestionDAO questionDAO,
			final QuestionCategoryPredictionResponse questionCategoryPredictionResponse) {
		return questionCategoryPredictionResponse
				.getQuestionIdToQuestionCategoryMap()
				.get(questionDAO.getId());
	}
}
