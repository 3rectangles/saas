/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.validators;

import com.barraiser.common.enums.Weightage;
import com.barraiser.common.graphql.input.SubmitFeedbackInput;
import com.barraiser.common.graphql.types.Feedback;
import com.barraiser.common.graphql.types.FeedbackValidationError;
import com.barraiser.common.graphql.types.Question;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.SkillDAO;
import com.barraiser.onboarding.dal.SkillRepository;
import com.barraiser.onboarding.interview.InterviewMapper;
import com.barraiser.onboarding.interview.InterviewStructureManager;
import com.barraiser.onboarding.interview.evaluation.scores.EvaluationStrategy_V5Constants;
import com.barraiser.commons.auth.UserRole;

import com.barraiser.onboarding.interview.feeback.config.InterviewFeedbackConfigDataFetcher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class SubmitFeedbackInputValidator {
	private final SkillRepository skillRepository;

	private final InterviewFeedbackConfigDataFetcher interviewFeedbackConfigDataFetcher;
	private final InterviewStructureManager interviewStructureManager;
	private final InterviewMapper interviewMapper;

	public ArrayList<FeedbackValidationError> validate(
			final SubmitFeedbackInput input, final InterviewDAO interviewDAO, final List<UserRole> userRole)
			throws JsonProcessingException {
		final JsonNode config = this.interviewFeedbackConfigDataFetcher
				.fetchFeedbackConfig(this.interviewMapper.toInterview(interviewDAO));

		final ArrayList<FeedbackValidationError> errors = new ArrayList<FeedbackValidationError>();
		final List<Question> questions = this.getNonDeletedQuestions(input.getQuestions());

		final String interviewFlowVersion = this.interviewStructureManager
				.getVersionOfInterviewStructureFlow(interviewDAO.getInterviewStructureId());
		questions.forEach(x -> errors
				.addAll(this.getErrorsInQuestion(x, userRole, config.get("question"), interviewFlowVersion)));

		final Boolean isAreasOfImprovementPresent = !StringUtils
				.isBlank(input.getOverallFeedback().getAreasOfImprovement().getFeedback());
		final Boolean isStrengthPresent = !StringUtils.isBlank(input.getOverallFeedback().getStrength().getFeedback());

		if (config.get("overallFeedback").get("mergeAOIAndStrength").asBoolean()) {
			if (!isAreasOfImprovementPresent && !isStrengthPresent) {
				errors.add(
						FeedbackValidationError.builder()
								.fieldTag("overallFeedback")
								// Look for some better options
								.error("Overall feedback can't be empty")
								.build());
			}
		} else {
			if (!isAreasOfImprovementPresent) {
				errors.add(
						FeedbackValidationError.builder()
								.fieldTag("areasOfImprovement") // TODO: Hardcoding is not a good idea.
								// Look for some better options
								.error("Feedback can't be empty")
								.build());
			}
			if (!isStrengthPresent) {
				errors.add(
						FeedbackValidationError.builder()
								.fieldTag(
										"strength") // TODO:Hardcoding is not a good idea. Look for some
								// better options
								.error("Feedback can't be empty")
								.build());
			}
		}

		if (!config.get("overallFeedback").get("softSkills").get("disabled").asBoolean()) {
			errors.addAll(this.getErrorsInSoftSkillOnOverallFeedbackLevel(input, interviewFlowVersion));
		}

		return errors;
	}

	private List<Question> getNonDeletedQuestions(final List<Question> questions) {
		return questions.stream()
				.filter(q -> !EvaluationStrategy_V5Constants.QuestionType.DELETED.getValue().equals(q.getType()))
				.collect(Collectors.toList());
	}

	ArrayList<FeedbackValidationError> getErrorsInQuestion(
			final Question question, final List<UserRole> userRole, final JsonNode config,
			final String interviewFlowVersion) {
		final ArrayList<FeedbackValidationError> errors = new ArrayList<FeedbackValidationError>();
		if (!this.interviewStructureManager.isInterviewStructureOnNewFlow(interviewFlowVersion)) {
			try {
				EvaluationStrategy_V5Constants.QuestionType.fromString(question.getType());
			} catch (final NoSuchElementException e) {
				final FeedbackValidationError temp = FeedbackValidationError.builder()
						.error("Question should belong to correct question type")
						.fieldTag(question.getId() + "type")
						.build();

				errors.add(temp);
			}
		}

		if (StringUtils.isBlank(question.getQuestion()) || question.getQuestion().isBlank()) {
			final FeedbackValidationError temp = FeedbackValidationError.builder()
					.error("Question can't be empty")
					.fieldTag(question.getId() + "question")
					.build();

			errors.add(temp);
		}

		if (!this.interviewStructureManager.isInterviewStructureOnNewFlow(interviewFlowVersion)) {
			if (question.getType() == null || question.getType().isBlank()) {
				final FeedbackValidationError temp = FeedbackValidationError.builder()
						.error("Question type can't be empty")
						.fieldTag(question.getId() + "type")
						.build();

				errors.add(temp);
			}
		}

		/**
		 * Only think non compulsary for a default question is the time of the question.
		 * Rest
		 * all validations are applicable to it.
		 */
		if (!this.interviewStructureManager.isInterviewStructureOnNewFlow(interviewFlowVersion)) {
			if (!Boolean.TRUE.equals(question.getIsDefault())) {
				if (!userRole.contains(UserRole.EXPERT)
						&& (question.getStartTimeEpoch() == null
								|| question.getStartTimeEpoch() == 0)) {
					final FeedbackValidationError temp = FeedbackValidationError.builder()
							.error("Start time can't be empty")
							.fieldTag(question.getId() + "startTime")
							.build();

					errors.add(temp);
				}
			}
		}

		if (!EvaluationStrategy_V5Constants.QuestionType.DELETED
				.getValue()
				.equals(question.getType()) &&
				!EvaluationStrategy_V5Constants.QuestionType.NON_EVALUATIVE
						.getValue()
						.equals(question.getType())) {

			if (question.getFeedbacks() == null
					|| question.getFeedbacks().size() < 1) {
				final FeedbackValidationError temp = FeedbackValidationError.builder()
						.error("Question must have at least one feedback!")
						.fieldTag(question.getId() + "feedback")
						.build();

				errors.add(temp);
			} else {
				question.getFeedbacks()
						.forEach(
								x -> {
									final ArrayList<FeedbackValidationError> feedbackErrors = this
											.getErrorsInFeedback(x, question, config.get("feedback"),
													interviewFlowVersion);
									errors.addAll(feedbackErrors);
								});
			}
		}
		return errors;
	}

	ArrayList<FeedbackValidationError> getErrorsInFeedback(
			final Feedback feedback, final Question question, final JsonNode config,
			final String interviewFlowVersion) {
		final ArrayList<FeedbackValidationError> errors = new ArrayList<FeedbackValidationError>();
		final Boolean isNonEvaluative = EvaluationStrategy_V5Constants.QuestionType.NON_EVALUATIVE
				.getValue()
				.equals(question.getType());
		if (Boolean.TRUE.equals(config.get("feedback").get("required").asBoolean())) {
			if (feedback.getFeedback() == null || feedback.getFeedback().equals("")) {
				final FeedbackValidationError temp = FeedbackValidationError.builder()
						.error("Feedback can't be empty")
						.fieldTag(feedback.getId() + "feedback")
						.build();

				errors.add(temp);
				errors.addAll(this.getErrorInFeedbackWeightage(feedback, interviewFlowVersion));
			}
		}

		if (!this.interviewStructureManager.isInterviewStructureOnNewFlow(interviewFlowVersion)) {
			if (Boolean.FALSE.equals(config.get("difficulty").get("disable").asBoolean()) && feedback.getRating() > 0) {
				try {
					EvaluationStrategy_V5Constants.FeedbackDifficulty.fromString(
							feedback.getDifficulty());
				} catch (final NoSuchElementException e) {
					final FeedbackValidationError temp = FeedbackValidationError.builder()
							.error("Feedback should have valid difficulty")
							.fieldTag(feedback.getId() + "difficulty")
							.build();

					errors.add(temp);
				}
			}
		}

		if (feedback.getRating() == null) {
			final FeedbackValidationError temp = FeedbackValidationError.builder()
					.error("Choose a rating")
					.fieldTag(feedback.getId() + "rating")
					.build();

			errors.add(temp);
		}

		if (feedback.getRating() == 0 && !isNonEvaluative) {
			final FeedbackValidationError temp = FeedbackValidationError.builder()
					.error("Rating has to be 0")
					.fieldTag(feedback.getId() + "rating")
					.build();

			errors.add(temp);
		}

		if (feedback.getRating() > 0 && isNonEvaluative) {
			final FeedbackValidationError temp = FeedbackValidationError.builder()
					.error("Rating cannot be 0")
					.fieldTag(feedback.getId() + "rating")
					.build();

			errors.add(temp);
		}

		return errors;
	}

	private List<FeedbackValidationError> getErrorsInSoftSkillOnOverallFeedbackLevel(
			final SubmitFeedbackInput input, final String interviewFlowVersion) {
		final ArrayList<FeedbackValidationError> errors = new ArrayList<FeedbackValidationError>();
		final List<Feedback> feedbacks = input.getOverallFeedback().getSoftSkills();
		final List<SkillDAO> skills = this.skillRepository.findAllByIdIn(
				feedbacks.stream()
						.map(Feedback::getCategoryId)
						.collect(Collectors.toList()));
		for (final Feedback feedback : feedbacks) {
			if (feedback.getRating() == null) {
				final FeedbackValidationError feedbackValidationError = FeedbackValidationError.builder()
						.error(
								"rating not submitted for : "
										+ skills.stream()
												.filter(
														x -> x.getId()
																.equals(
																		feedback
																				.getCategoryId()))
												.findFirst()
												.get()
												.getName())
						.fieldTag("softSkills")
						.build();
				errors.add(feedbackValidationError);
			}
			errors.addAll(this.getErrorInFeedbackWeightage(feedback, interviewFlowVersion));
		}
		return errors;
	}

	private List<FeedbackValidationError> getErrorInFeedbackWeightage(final Feedback feedback,
			final String interviewFlowVersion) {
		final ArrayList<FeedbackValidationError> errors = new ArrayList<FeedbackValidationError>();
		if (this.interviewStructureManager.isInterviewStructureOnNewFlow(interviewFlowVersion)) {
			try {
				Weightage.fromString(feedback.getFeedbackWeightage().toString());
			} catch (final NoSuchElementException e) {
				final FeedbackValidationError temp = FeedbackValidationError.builder()
						.error("Feedback should belong to correct weightage type")
						.fieldTag(feedback.getId() + "weightage")
						.build();

				errors.add(temp);
			}
		}
		return errors;
	}
}
