/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dataScience;

import com.barraiser.common.DTO.QuestionCategoryDTO;
import com.barraiser.common.graphql.input.PredictQuestionCategoryInput;
import com.barraiser.common.graphql.types.InterviewCategory;
import com.barraiser.common.requests.QuestionCategoryPredictionRequest;
import com.barraiser.common.DTO.QuestionDTO;
import com.barraiser.common.responses.QuestionCategoryPredictionResponse;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.interview.pojo.InterviewQuestionDetails;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Log4j2
@AllArgsConstructor
public class QuestionCategoryPredictionGenerator {
	private final DataScienceFeignClient dataScienceFeignClient;

	public QuestionCategoryPredictionResponse predictQuestionCategories(
			final InterviewQuestionDetails interviewQuestionDetails) {
		log.info(String.format(
				"Fetching question categories for question of interviewId:%s",
				interviewQuestionDetails.getInterviewId()));

		return this.dataScienceFeignClient
				.predictCategoriesOfQuestions(QuestionCategoryPredictionRequest
						.builder()
						.questions(this.getQuestions(interviewQuestionDetails))
						.questionCategories(
								this.getQuestionCategories(interviewQuestionDetails.getInterviewCategories()))
						.build())
				.getBody();
	}

	public QuestionCategoryPredictionResponse predictQuestionCategory(
			final PredictQuestionCategoryInput input,
			final InterviewDAO interviewDAO,
			final List<InterviewCategory> interviewCategories) {
		log.info(String.format(
				"Fetching question categories for question of question:%s questionId:%s",
				input.getQuestion(),
				input.getQuestionId()));

		return this.dataScienceFeignClient
				.predictCategoriesOfQuestions(QuestionCategoryPredictionRequest
						.builder()
						.questions(List.of(this.getQuestion(input, interviewDAO)))
						.questionCategories(this.getQuestionCategories(interviewCategories))
						.build())
				.getBody();
	}

	private List<QuestionDTO> getQuestions(
			final InterviewQuestionDetails interviewQuestionDetails) {
		return interviewQuestionDetails
				.getQuestions()
				.stream()
				.map(questionDAO -> QuestionDTO
						.builder()
						.id(questionDAO.getId())
						.interviewId(questionDAO.getInterviewId())
						.question(questionDAO.getQuestion())
						.build())
				.collect(Collectors.toList());
	}

	private List<QuestionCategoryDTO> getQuestionCategories(
			final List<InterviewCategory> interviewCategories) {
		return interviewCategories
				.stream()
				.map(interviewCategory -> QuestionCategoryDTO
						.builder()
						.id(interviewCategory.getId())
						.name(interviewCategory.getName())
						.build())
				.collect(Collectors.toList());
	}

	private QuestionDTO getQuestion(
			final PredictQuestionCategoryInput input,
			final InterviewDAO interviewDAO) {
		return QuestionDTO
				.builder()
				.id(input.getQuestionId())
				.interviewId(interviewDAO.getId())
				.question(input.getMasterQuestion() != null
						? String.format(
								"%s %s",
								input.getMasterQuestion(),
								input.getQuestion())
						: input.getQuestion())
				.build();
	}
}
