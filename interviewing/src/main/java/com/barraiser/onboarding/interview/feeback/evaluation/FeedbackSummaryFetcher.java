/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.feeback.evaluation;

import com.barraiser.common.graphql.input.SubmitFeedbackInput;
import com.barraiser.common.graphql.types.Feedback;
import com.barraiser.common.graphql.types.Question;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.InterviewStructureManager;
import com.barraiser.onboarding.interview.InterviewUtil;
import com.barraiser.onboarding.interview.pojo.FeedbackData;
import com.barraiser.onboarding.interview.pojo.InterviewData;
import com.barraiser.onboarding.interview.pojo.QuestionData;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.NormalisedRatingPopulator;
import com.barraiser.onboarding.interview.evaluation.EvaluationGenerationRequestToDTOConverter;
import com.barraiser.onboarding.interview.evaluation.scores.pojo.ComputeEvaluationScoresData;
import com.barraiser.onboarding.interview.evaluation.scores.BgsCalculator;
import com.barraiser.onboarding.interview.evaluation.scores.EvaluationStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import static com.barraiser.onboarding.common.Constants.OTHERS_SKILL_ID;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class FeedbackSummaryFetcher {
	private final SkillWeightageRepository skillWeightageRepository;
	private final EvaluationRepository evaluationRepository;
	private final InterViewRepository interViewRepository;
	private final SkillRepository skillRepository;
	private final List<EvaluationStrategy> evaluationStrategies;
	private final List<FeedbackEvaluator> feedbackEvaluatorList;
	private final InterviewStructureSkillsRepository interviewStructureSkillsRepository;
	private final ObjectMapper objectMapper;
	private final NormalisedRatingPopulator normalisedRatingPopulator;
	private final EvaluationGenerationRequestToDTOConverter evaluationGenerationRequestToDTOConverter;
	private final InterviewUtil interviewUtil;
	private final InterviewStructureManager interviewStructureManager;

	public List<String> getFeedbackSummary(final SubmitFeedbackInput input) {
		final FeedbackSummaryData feedbackSummaryData = this.createDataForFeedbackSummary(input);
		final List<String> feedbackSummaries = new ArrayList<>();
		this.feedbackEvaluatorList.sort(Comparator.comparingInt(FeedbackEvaluator::order));
		for (final FeedbackEvaluator feedbackEvaluator : this.feedbackEvaluatorList) {
			feedbackSummaries.addAll(feedbackEvaluator.getImprovement(feedbackSummaryData));
		}
		return feedbackSummaries;
	}

	public ComputeEvaluationScoresData createObjectToCalculateBGS(
			final InterviewDAO interviewDAO,
			final List<Question> questions,
			final List<SkillWeightageDAO> skillWeightageDAOs,
			final List<Feedback> softSkills) {
		final List<NormalisedRatingMapping> ratingToNormalisedRatingMapping = this.normalisedRatingPopulator
				.getRatingToNormalisedRatingOfExpert(interviewDAO.getInterviewerId());
		final List<QuestionData> questionDataList = this.prepareQuestions(questions, interviewDAO,
				ratingToNormalisedRatingMapping);
		final Boolean isSaasInterview = this.interviewUtil.isSaasInterview(interviewDAO.getInterviewRound());
		final Map<String, Double> skillWeightageMap = skillWeightageDAOs.stream()
				.collect(
						Collectors.toMap(
								SkillWeightageDAO::getSkillId,
								SkillWeightageDAO::getWeightage));

		final Double weightageOfOthersCategory = this.evaluationGenerationRequestToDTOConverter
				.getWeightageOfOthersCategory(questionDataList, skillWeightageMap);
		skillWeightageMap.put(OTHERS_SKILL_ID, weightageOfOthersCategory);

		return ComputeEvaluationScoresData.builder()
				.skillWeightageMap(skillWeightageMap)
				.questions(questionDataList)
				.softSkillFeedbackList(
						this.getUpdatedSoftSkills(softSkills, ratingToNormalisedRatingMapping, isSaasInterview))
				.interviews(this.evaluationGenerationRequestToDTOConverter
						.populateQuestionsInInterview(
								Arrays.asList(this.objectMapper.convertValue(interviewDAO, InterviewData.class)
										.toBuilder().isSaasInterview(isSaasInterview).build()),
								questionDataList))
				.build();
	}

	// tp-722 : check : FeedbackSummmary data is used to do qualty check for
	// feedback
	private FeedbackSummaryData createDataForFeedbackSummary(final SubmitFeedbackInput input) {
		final InterviewDAO interviewDAO = this.interViewRepository.findById(input.getInterviewId()).get();
		final EvaluationDAO evaluationDAO = this.evaluationRepository.findById(interviewDAO.getEvaluationId()).get();
		final List<SkillWeightageDAO> skillWeightageDAOs = this.skillWeightageRepository
				.findAllByJobRoleIdAndJobRoleVersion(evaluationDAO.getJobRoleId(), evaluationDAO.getJobRoleVersion());
		final List<SkillDAO> skills = this.skillRepository.findAllByIdIn(
				this.interviewStructureSkillsRepository
						.findAllByInterviewStructureIdAndIsSpecific(
								interviewDAO.getInterviewStructureId(), false)
						.stream()
						.map(InterviewStructureSkillsDAO::getSkillId)
						.collect(Collectors.toList()));
		final EvaluationStrategy evaluationStrategy = this.evaluationStrategies.stream()
				.filter(
						x -> x.version()
								.equals(
										evaluationDAO
												.getDefaultScoringAlgoVersion()))
				.findFirst()
				.get();
		final List<Question> parentQuestions = new ArrayList<>(input.getQuestions());
		final List<Question> followUpQuestions = input.getQuestions().stream()
				.filter(x -> x.getFollowUpQuestions() != null)
				.map(Question::getFollowUpQuestions)
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
		final List<Question> allQuestions = this.flattenQuestions(parentQuestions);
		final ComputeEvaluationScoresData computeEvaluationScoresData = this.createObjectToCalculateBGS(
				interviewDAO,
				allQuestions,
				skillWeightageDAOs,
				Integer.parseInt(evaluationDAO.getDefaultScoringAlgoVersion()) >= 8
						? input.getOverallFeedback().getSoftSkills()
						: List.of());

		return FeedbackSummaryData.builder()
				.parentQuestions(parentQuestions)
				.followUpQuestions(followUpQuestions)
				.allQuestions(allQuestions)
				.overallFeedback(input.getOverallFeedback())
				.bgsScore(
						BgsCalculator.calculateBgsNoScale(evaluationStrategy
								.computeEvaluationScore(computeEvaluationScoresData).getSkillScores()))
				.skillDAOs(skills)
				.skillWeightageDAOs(skillWeightageDAOs)
				.interviewFlowVersion(this.interviewStructureManager
						.getVersionOfInterviewStructureFlow(interviewDAO.getInterviewStructureId()))
				.build();
	}

	private List<FeedbackData> getUpdatedSoftSkills(
			final List<Feedback> softSkills,
			final List<NormalisedRatingMapping> normalisedRatingMappings,
			final Boolean isSaas) {
		List<FeedbackData> softSkillsFeedbackData = softSkills.stream()
				.map(x -> this.objectMapper.convertValue(x, FeedbackData.class))
				.map(x -> x.toBuilder().isSaasFeedback(isSaas).build())
				.collect(Collectors.toList());
		if (!isSaas) {
			softSkillsFeedbackData = this.normalisedRatingPopulator.getFeedbacksWithNormalisedRatings(
					softSkillsFeedbackData, normalisedRatingMappings);
		}
		return this.evaluationGenerationRequestToDTOConverter.populateSoftSkillsRatings(
				softSkillsFeedbackData);
	}

	private List<QuestionData> prepareQuestions(final List<Question> questions, final InterviewDAO interviewDAO,
			final List<NormalisedRatingMapping> normalisedRatingMappings) {
		final Boolean isSaasInterview = this.interviewUtil.isSaasInterview(interviewDAO.getInterviewRound());
		final AtomicInteger serialNumber = new AtomicInteger(0);
		return questions.stream().map(x -> this.objectMapper.convertValue(x, QuestionData.class).toBuilder()
				.feedbacks(this.normalisedRatingPopulator
						.getFeedbacksWithNormalisedRatings(
								x.getFeedbacks().stream()
										.map(y -> this.objectMapper.convertValue(y, FeedbackData.class)
												.toBuilder().referenceId(x.getId()).isSaasFeedback(isSaasInterview)
												.build())
										.collect(Collectors.toList()),
								normalisedRatingMappings))
				.interviewId(interviewDAO.getId())
				.serialNumber(serialNumber.getAndIncrement())
				.followUpQuestions(null)
				.isSaasQuestion(isSaasInterview)
				.build()).collect(Collectors.toList());
	}

	private List<Question> flattenQuestions(final List<Question> questions) {
		final List<Question> allQuestions = new ArrayList<>();
		questions.forEach(x -> {
			allQuestions.add(x);
			if (x.getFollowUpQuestions() != null) {
				allQuestions.addAll(x.getFollowUpQuestions());
			}
		});
		return allQuestions;
	}
}
