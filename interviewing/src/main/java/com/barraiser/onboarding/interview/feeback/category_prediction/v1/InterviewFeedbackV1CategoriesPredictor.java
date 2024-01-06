/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.feeback.category_prediction.v1;

import com.barraiser.common.DTO.QuestionCategoryDTO;
import com.barraiser.common.graphql.types.InterviewCategory;
import com.barraiser.common.responses.QuestionCategoryPredictionResponse;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.dataScience.QuestionCategoryPredictionGenerator;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewCategoriesDataFetcher;
import com.barraiser.onboarding.interview.pojo.InterviewQuestionDetails;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.barraiser.onboarding.common.Constants.OTHERS_SKILL_ID;

@Component
@Log4j2
@AllArgsConstructor
public class InterviewFeedbackV1CategoriesPredictor {
	private final QuestionCategoryPredictionGenerator questionCategoryPredictionGenerator;
	private final QuestionRepository questionRepository;
	private final InterViewRepository interViewRepository;
	private final InterviewCategoriesDataFetcher interviewCategoriesDataFetcher;
	private final FeedbackRepository feedbackRepository;
	private final InterviewFeedbackV1PredictedCategorySaver predictedCategorySaver;

	private static final String OTHERS_CATEGORY_NAME = "Others";

	public void predictCategories(final String interviewId) {
		final InterviewDAO interview = this.interViewRepository.findById(interviewId).get();
		final InterviewQuestionDetails interviewQuestionDetails = new InterviewQuestionDetails();
		interviewQuestionDetails.setInterviewId(interviewId);
		this.populateQuestions(interviewQuestionDetails, interview);
		this.populateCategories(interviewQuestionDetails, interview);

		final Boolean doesInterviewOnlyContainOthersCategory = this
				.doesInterviewOnlyContainOthersCategory(interviewQuestionDetails.getInterviewCategories());
		// Model currently doesn't support prediction on Others category
		this.filterOutOthersCategory(interviewQuestionDetails);

		QuestionCategoryPredictionResponse questionCategoryPredictionResponse;

		if (doesInterviewOnlyContainOthersCategory) {
			questionCategoryPredictionResponse = this
					.manuallyMarkAllQuestionsInOthersCategory(interviewQuestionDetails);
		} else {
			questionCategoryPredictionResponse = this.questionCategoryPredictionGenerator
					.predictQuestionCategories(interviewQuestionDetails);
		}

		this.predictedCategorySaver.savePredictedCategories(interviewId, questionCategoryPredictionResponse);
	}

	public void filterOutOthersCategory(final InterviewQuestionDetails interviewQuestionDetails) {

		List<InterviewCategory> filteredOutInterviewCategories = interviewQuestionDetails.getInterviewCategories()
				.stream()
				.filter(c -> !OTHERS_SKILL_ID.equals(c.getId()))
				.collect(Collectors.toList());

		interviewQuestionDetails.setInterviewCategories(filteredOutInterviewCategories);
	}

	/**
	 * This basically
	 */
	private Boolean doesInterviewOnlyContainOthersCategory(final List<InterviewCategory> categories) {
		return ((categories.size() == 1) && (OTHERS_SKILL_ID.equals(categories.get(0).getId())));
	}

	private QuestionCategoryPredictionResponse manuallyMarkAllQuestionsInOthersCategory(
			final InterviewQuestionDetails interviewQuestionDetails) {
		final Map<String, QuestionCategoryDTO> questionCategoryDTOMap = new HashMap<>();

		interviewQuestionDetails.getQuestions().stream()
				.forEach(q -> questionCategoryDTOMap.put(q.getId(), QuestionCategoryDTO.builder()
						.id(OTHERS_SKILL_ID)
						.name(OTHERS_CATEGORY_NAME)
						.build()));

		return QuestionCategoryPredictionResponse.builder()
				.questionIdToQuestionCategoryMap(questionCategoryDTOMap)
				.build();
	}

	public QuestionCategoryDTO predictCategory(final String interviewId, final String question) {
		final InterviewDAO interview = this.interViewRepository.findById(interviewId).get();
		final InterviewQuestionDetails interviewQuestionDetails = new InterviewQuestionDetails();
		interviewQuestionDetails.setInterviewId(interviewId);
		this.populateCategories(interviewQuestionDetails, interview);
		final String randomQuestionId = UUID.randomUUID().toString();
		interviewQuestionDetails.setQuestions(List.of(
				QuestionDAO.builder().id(randomQuestionId).question(question).build()));
		return this.questionCategoryPredictionGenerator.predictQuestionCategories(interviewQuestionDetails)
				.getQuestionIdToQuestionCategoryMap().get(randomQuestionId);
	}

	private void populateQuestions(final InterviewQuestionDetails interviewQuestionDetails,
			final InterviewDAO interview) {
		final List<QuestionDAO> questions = this.questionRepository
				.findAllByInterviewIdAndRescheduleCount(interview.getId(), interview.getRescheduleCount());
		final List<QuestionDAO> filteredQuestions = new ArrayList<>();
		for (QuestionDAO question : questions) {
			final List<FeedbackDAO> feedbacks = this.feedbackRepository.findAllByReferenceId(question.getId());
			final boolean allFeedbackContainsCategory = feedbacks.stream().noneMatch(f -> f.getCategoryId() == null);
			if (feedbacks.isEmpty() || !allFeedbackContainsCategory) {
				filteredQuestions.add(question);
			}
		}
		interviewQuestionDetails.setQuestions(filteredQuestions);
	}

	/**
	 * NOTE : These categories will not include SOFT SKILLS
	 */
	private void populateCategories(final InterviewQuestionDetails interviewQuestionDetails,
			final InterviewDAO interview) {

		final List<InterviewCategory> interviewCategories = this.interviewCategoriesDataFetcher
				.getParentInterviewCategoryOfSkills(
						interview.getInterviewStructureId());

		interviewQuestionDetails.setInterviewCategories(interviewCategories);
	}

}
