/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dataScience;

import com.barraiser.common.DTO.QuestionCategoryDTO;
import com.barraiser.common.graphql.input.GetFeedbackRecommendationsInput;
import com.barraiser.common.graphql.types.FeedbackRecommendations;
import com.barraiser.onboarding.graphql.GraphQLQuery;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.interview.feeback.category_prediction.v1.InterviewFeedbackV1CategoriesPredictor;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class FeedbackRecommendationQuery implements GraphQLQuery {
	private final GraphQLUtil graphQLUtil;
	private final DataScienceFeignClient dataScienceFeignClient;
	private final InterviewFeedbackV1CategoriesPredictor interviewFeedbackV1CategoriesPredictor;

	@Override
	public String name() {
		return "getFeedbackRecommendations";
	}

	@Override
	public Object get(DataFetchingEnvironment environment) throws Exception {
		GetFeedbackRecommendationsInput input = this.graphQLUtil
				.getInput(
						environment,
						GetFeedbackRecommendationsInput.class);

		log.info(String.format(
				"Getting Feedback Recommendation from DS model for interviewId %s and feedbackId %s",
				input.getInterviewId(),
				input.getFeedbackId()));

		if (input.getQuestionCategory() == null) {
			final QuestionCategoryDTO category = this.interviewFeedbackV1CategoriesPredictor
					.predictCategory(input.getInterviewId(), input.getQuestion());
			input = input.toBuilder().questionCategory(category.getName()).build();
		}

		final FeedbackRecommendations feedbackRecommendations = this.dataScienceFeignClient
				.generateFeedbackRecommendations(input)
				.getBody();

		return DataFetcherResult
				.newResult()
				.data(feedbackRecommendations)
				.build();
	}

}
