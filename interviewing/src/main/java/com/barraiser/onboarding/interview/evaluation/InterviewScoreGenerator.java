/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation;

import com.barraiser.common.graphql.types.SkillScore;
import com.barraiser.onboarding.analytics.BGSScoreDataAnalyticsPopulator;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.EvaluationRepository;
import com.barraiser.onboarding.interview.evaluation.scores.EvaluationStrategy;
import com.barraiser.onboarding.interview.evaluation.scores.pojo.ComputeEvaluationScoresData;
import com.barraiser.onboarding.interview.evaluation.scores.pojo.EvaluationScoreData;
import com.barraiser.onboarding.interview.pojo.InterviewData;
import com.barraiser.onboarding.interview.scoring.dal.InterviewScoreDAO;
import com.barraiser.onboarding.interview.scoring.dal.InterviewScoreRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class InterviewScoreGenerator {
	public static final String ENTITY_TYPE_INTERVIEW = "interview";
	public static final String ERROR_MESSAGE = "Error occured while generating score for interview %s, with algo version %s";

	private final EvaluationRepository evaluationRepository;
	private final List<EvaluationStrategy> evaluationStrategyList;
	private final BGSScoreDataAnalyticsPopulator BGSScoreDataAnalyticsPopulator;
	private final EvaluationGenerationRequestToDTOConverter evaluationGenerationRequestPreProcessor;
	private final InterviewScoreRepository interviewScoreRepository;

	@Transactional
	public void generateScores(final InterviewData interview) {
		final EvaluationDAO evaluationDAO = evaluationRepository.findById(interview.getEvaluationId()).get();
		this.evaluationStrategyList.forEach(
				evaluationStrategy -> {
					if (evaluationDAO.getDefaultScoringAlgoVersion().equals(evaluationStrategy.version())) {
						try {
							this.generateInterviewScores(evaluationStrategy, interview);
						} catch (final ObjectOptimisticLockingFailureException exception) {
							throw exception;
						} catch (final Exception exception) {
							log.error(String.format(ERROR_MESSAGE, interview.getId(), evaluationStrategy.version()),
									exception);
						}
					}
				});
	}

	private void generateInterviewScores(
			final EvaluationStrategy evaluationStrategy, final InterviewData interview) {
		final ComputeEvaluationScoresData computeScoreInputDTO = this.evaluationGenerationRequestPreProcessor.process(
				Arrays.asList(interview), interview.getEvaluationId());
		final EvaluationScoreData evaluationScoreData = evaluationStrategy.computeEvaluationScore(computeScoreInputDTO);
		this.saveInterviewScores(
				interview.getId(),
				evaluationStrategy.version(),
				evaluationScoreData.getSkillScores());
		this.BGSScoreDataAnalyticsPopulator.saveAnalyticsForBGSCalculation(interview.getId(), ENTITY_TYPE_INTERVIEW,
				evaluationStrategy.version(),
				evaluationScoreData.getInput(),
				null);
	}

	private void saveInterviewScores(
			final String interviewId,
			final String scoringAlgoVersion,
			final List<SkillScore> skillScores) {
		this.interviewScoreRepository.deleteAllByInterviewIdAndScoringAlgoVersion(interviewId, scoringAlgoVersion);
		this.interviewScoreRepository.flush();
		final List<InterviewScoreDAO> interviewScoreDAOS = skillScores.stream()
				.map(
						ss -> InterviewScoreDAO.builder()
								.id(UUID.randomUUID().toString())
								.interviewId(interviewId)
								.skillId(ss.getSkillId())
								.scoringAlgoVersion(
										scoringAlgoVersion)
								.weightage(ss.getWeightage())
								.score(ss.getScore())
								.build())
				.collect(Collectors.toList());

		this.interviewScoreRepository.saveAll(interviewScoreDAOS);
	}
}
