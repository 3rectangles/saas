/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dataScience;

import com.barraiser.common.DTO.QuestionCategoryDTO;
import com.barraiser.common.graphql.input.PredictQuestionCategoryInput;
import com.barraiser.common.graphql.types.InterviewCategory;
import com.barraiser.common.graphql.types.PredictQuestionCategoryOutput;
import com.barraiser.common.graphql.types.QuestionCategory;
import com.barraiser.common.responses.QuestionCategoryPredictionResponse;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.QuestionDAO;
import com.barraiser.onboarding.dal.QuestionRepository;
import com.barraiser.onboarding.graphql.GraphQLMutation;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewCategoriesDataFetcher;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
@AllArgsConstructor
public class PredictQuestionCategoryMutation implements GraphQLMutation {
	private final GraphQLUtil graphQLUtil;
	private final QuestionCategoryPredictionGenerator questionCategoryPredictionGenerator;
	private final InterviewCategoriesDataFetcher interviewCategoriesDataFetcher;
	private final InterViewRepository interViewRepository;
	private final QuestionRepository questionRepository;

	@Override
	public String name() {
		return "predictQuestionCategory";
	}

	@Override
	public Object get(DataFetchingEnvironment environment) throws Exception {
		final PredictQuestionCategoryInput input = this.graphQLUtil
				.getInput(
						environment,
						PredictQuestionCategoryInput.class);

		log.info(String.format(
				"Predicting question category for question:%s,questionId:%s",
				input.getQuestion(),
				input.getQuestionId()));

		final QuestionDAO questionDAO = this.questionRepository
				.findById(input.getQuestionId())
				.get();

		final InterviewDAO interviewDAO = this.interViewRepository
				.findById(questionDAO.getInterviewId())
				.get();

		final List<InterviewCategory> interviewCategories = this.interviewCategoriesDataFetcher
				.getParentInterviewCategoryOfSkills(interviewDAO.getInterviewStructureId());

		final QuestionCategory predictedQuestionCategory = this.getPredictedQuestionCategory(
				input,
				interviewDAO,
				interviewCategories);

		return PredictQuestionCategoryOutput
				.builder()
				.predictedQuestionCategory(predictedQuestionCategory)
				.build();
	}

	private QuestionCategory getPredictedQuestionCategory(
			final PredictQuestionCategoryInput input,
			final InterviewDAO interviewDAO,
			List<InterviewCategory> interviewCategories) {
		final QuestionCategoryPredictionResponse response = this.questionCategoryPredictionGenerator
				.predictQuestionCategory(
						input,
						interviewDAO,
						interviewCategories);

		final QuestionCategoryDTO questionCategoryDTO = response
				.getQuestionIdToQuestionCategoryMap()
				.get(input.getQuestionId());

		return QuestionCategory
				.builder()
				.id(questionCategoryDTO.getId())
				.name(questionCategoryDTO.getName())
				.build();
	}
}
