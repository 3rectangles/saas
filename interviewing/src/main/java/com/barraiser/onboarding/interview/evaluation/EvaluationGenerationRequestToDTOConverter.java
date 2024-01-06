/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation;

import com.barraiser.onboarding.common.Constants;
import com.barraiser.onboarding.common.HtmlTagsRemover;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.feeback.FeedbackNormalisationUtil;
import com.barraiser.onboarding.interview.jobrole.SkillWeightageManager;
import com.barraiser.onboarding.interview.pojo.FeedbackData;
import com.barraiser.onboarding.interview.pojo.InterviewData;
import com.barraiser.onboarding.interview.pojo.QuestionData;
import com.barraiser.onboarding.interview.evaluation.scores.pojo.ComputeEvaluationScoresData;
import com.barraiser.onboarding.interview.evaluation.scores.EvaluationStrategy_V5Constants;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

import static com.barraiser.onboarding.common.Constants.OTHERS_SKILL_ID;
import static com.barraiser.onboarding.common.Constants.SOFT_SKILL_ID;

import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class EvaluationGenerationRequestToDTOConverter {
	private final SkillWeightageManager skillWeightageManager;
	private final QuestionRepository questionRepository;
	private final ObjectMapper objectMapper;
	private final FeedbackRepository feedbackRepository;
	private final FeedbackNormalisationUtil feedbackNormalisationUtil;
	private final EvaluationRepository evaluationRepository;

	public ComputeEvaluationScoresData process(
			final List<InterviewData> interviews, final String evaluationId) {

		final List<QuestionData> questions = this.getQuestions(interviews);
		final List<QuestionData> updatedQuestions = this.populateFeedbacksInQuestions(questions);
		final EvaluationDAO evaluationDAO = this.evaluationRepository.findById(evaluationId).get();

		final Map<String, Double> skillWeightageMap = this.getSkillWeightages(evaluationId, updatedQuestions);

		final ComputeEvaluationScoresData scoreComputationDTO = ComputeEvaluationScoresData.builder()
				.evaluationId(evaluationId)
				.questions(updatedQuestions)
				.skillWeightageMap(skillWeightageMap)
				.softSkillFeedbackList(this.getSoftSkillsFeedback(interviews, evaluationDAO))
				.interviews(this.populateQuestionsInInterview(interviews, updatedQuestions))
				.build();
		return scoreComputationDTO;
	}

	public Map<String, Double> getSkillWeightages(final String evaluationId, final List<QuestionData> questions) {
		Map<String, Double> skillWeightageMap = this.skillWeightageManager
				.getSkillWeightageForEvaluation(evaluationId)
				.stream()
				.collect(
						Collectors.toMap(
								SkillWeightageDAO::getSkillId,
								SkillWeightageDAO::getWeightage));

		final Double weightageOfOthersCategory = this.getWeightageOfOthersCategory(questions, skillWeightageMap);
		skillWeightageMap.put(OTHERS_SKILL_ID, weightageOfOthersCategory);

		return skillWeightageMap;
	}

	public Double getWeightageOfOthersCategory(final List<QuestionData> questionData,
			final Map<String, Double> skillWeightageMap) {

		final Set<String> categoriesInFeedback = this.getCategoriesAcrossFeedback(questionData);

		/**
		 * Fast track interviews will only contain soft skill category in feedback
		 */
		if (this.doesFeedbackOnlyContainSoftSkillCategory(categoriesInFeedback)) {
			return 0.0;
		} else if (!EvaluationScoreComputationManager
				.doesFeedbackOnlyContainImplicitlyIncludedCategories(categoriesInFeedback)) {
			return 0.0;
		} else {
			return 100.0 - skillWeightageMap.get(SOFT_SKILL_ID);
		}
	}

	public Boolean doesFeedbackOnlyContainSoftSkillCategory(final Set<String> categoriesInFeedback) {
		return (categoriesInFeedback.size() == 1)
				&& (categoriesInFeedback.contains(SOFT_SKILL_ID));
	}

	private Set<String> getCategoriesAcrossFeedback(final List<QuestionData> questions) {

		final Set<String> categoriesInFeedback = new HashSet<>();

		for (final QuestionData questionData : questions) {
			for (final FeedbackData feedbackData : questionData.getFeedbacks()) {
				categoriesInFeedback.add(feedbackData.getCategoryId());
			}
		}

		// Adding explicitly as in the newer evaluation versions Soft skill is present
		// but in overall feedback
		categoriesInFeedback.add(SOFT_SKILL_ID);
		return categoriesInFeedback;
	}

	private List<QuestionData> getQuestions(final List<InterviewData> interviews) {
		final List<QuestionData> questions = new ArrayList<>();
		interviews.forEach(
				x -> {
					final List<QuestionDAO> questionsInInterview = this.questionRepository
							.findAllByInterviewIdAndRescheduleCountOrderByStartTimeEpochAsc(
									x.getId(), x.getRescheduleCount());
					questions.addAll(questionsInInterview.stream()
							.map(y -> this.objectMapper.convertValue(y, QuestionData.class))
							.map(q -> q.toBuilder()
									.isSaasQuestion(x.getIsSaasInterview())
									.build())
							.collect(Collectors.toList()));
				});
		return questions;
	}

	private List<FeedbackData> getSoftSkillsFeedback(final List<InterviewData> interviews,
			final EvaluationDAO evaluationDAO) {
		final List<String> interviewIds = interviews.stream().map(InterviewData::getId).collect(Collectors.toList());
		final List<FeedbackDAO> softSkills = this.feedbackRepository.findAllByReferenceIdInAndType(
				interviewIds, Constants.OVERALL_FEEDBACK_TYPE_SOFT_SKILLS);
		if (!"14".equals(evaluationDAO.getDefaultScoringAlgoVersion())) {
			return this.populateSoftSkillsRatings(
					softSkills.stream()
							.map(x -> this.objectMapper.convertValue(x, FeedbackData.class))
							.map(x -> {
								final InterviewData interview = interviews.stream().filter(
										i -> i.getId().equals(x.getReferenceId())).findFirst().get();
								return x.toBuilder().isSaasFeedback(interview.getIsSaasInterview()).build();
							})
							.collect(Collectors.toList()));
		} else {
			return softSkills.stream().map(x -> {
				final InterviewData interview = interviews.stream().filter(
						i -> i.getId().equals(x.getReferenceId())).findFirst().get();

				return this.objectMapper.convertValue(x, FeedbackData.class).toBuilder()
						.categoryId(SOFT_SKILL_ID)
						.isSaasFeedback(interview.getIsSaasInterview())
						.build();
			})
					.collect(Collectors.toList());
		}

	}

	public List<FeedbackData> populateSoftSkillsRatings(final List<FeedbackData> softSkills) {
		final List<NormalisedRatingMapping> normalisedRatingMappings = new ArrayList<>();
		float totalSoftSkillRatings = 0F;
		final List<String> normalisationVersions = ExpertNormalisedRatingManager.VERSIONS;
		for (final String normalisationVersion : normalisationVersions) {
			float totalSoftSkillsNormalisedRatings = 0F;
			totalSoftSkillRatings = 0F;
			for (final FeedbackData feedback : softSkills) {
				totalSoftSkillRatings += feedback.getRating();
				final Float softSkillsNormalisedRating = feedback.getIsSaasFeedback() ? feedback.getRating()
						: this.feedbackNormalisationUtil.getCappedNormalisedRating(
								feedback,
								normalisationVersion);
				totalSoftSkillsNormalisedRatings += softSkillsNormalisedRating == null ? 0 : softSkillsNormalisedRating;
			}

			final float cappedNormalisedRatingOfSoftSkills = (totalSoftSkillsNormalisedRatings / softSkills.size()) * 2;
			normalisedRatingMappings.add(NormalisedRatingMapping.builder()
					.rating((totalSoftSkillRatings / softSkills.size()) * 2)
					.cappedNormalisedRating(cappedNormalisedRatingOfSoftSkills > 10 ? 10F
							: cappedNormalisedRatingOfSoftSkills < 1 ? 1F : cappedNormalisedRatingOfSoftSkills)
					.normalisationVersion(normalisationVersion)
					.build());
		}
		return Arrays.asList(FeedbackData.builder()
				.difficulty(EvaluationStrategy_V5Constants.FeedbackDifficulty.MODERATE.getValue())
				.categoryId(Constants.SOFT_SKILL_ID)
				.rating(totalSoftSkillRatings)
				.normalisedRatingMappings(normalisedRatingMappings)
				.isSaasFeedback(false)
				.build());
	}

	public List<InterviewData> populateQuestionsInInterview(final List<InterviewData> interviews,
			final List<QuestionData> questions) {
		return interviews.stream().map(x -> x
				.toBuilder()
				.questions(questions.stream().filter(y -> y.getInterviewId().equals(x.getId()))
						.collect(Collectors.toList()).stream()
						.map(z -> z.toBuilder().feedbacks(z.getFeedbacks().stream().map(u -> u.toBuilder()
								.feedback(HtmlTagsRemover.removeHtmlTags(u.getFeedback())).build())
								.collect(Collectors.toList())).build())
						.collect(Collectors.toList()))
				.build())
				.collect(Collectors.toList());
	}

	private List<QuestionData> populateFeedbacksInQuestions(final List<QuestionData> questions) {
		final List<FeedbackData> feedbacks = this.feedbackRepository
				.findByReferenceIdIn(questions.stream().map(QuestionData::getId).collect(Collectors.toList())).stream()
				.map(x -> this.objectMapper.convertValue(x, FeedbackData.class)).collect(Collectors.toList());
		return questions.stream()
				.map(x -> x.toBuilder()
						.feedbacks(feedbacks.stream()
								.filter(y -> y.getReferenceId().equals(x.getId()))
								.map(f -> f.toBuilder().isSaasFeedback(x.getIsSaasQuestion()).build())
								.collect(Collectors.toList()))
						.build())
				.collect(Collectors.toList());
	}
}
