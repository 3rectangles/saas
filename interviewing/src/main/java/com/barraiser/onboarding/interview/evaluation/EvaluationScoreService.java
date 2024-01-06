/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation;

import com.barraiser.common.graphql.types.SkillScore;
import com.barraiser.onboarding.dal.EvaluationScoreDAO;
import com.barraiser.onboarding.dal.EvaluationScoreHistoryDAO;
import com.barraiser.onboarding.dal.EvaluationScoreHistoryRepository;
import com.barraiser.onboarding.dal.EvaluationScoreRepository;
import com.barraiser.onboarding.interview.evaluation.enums.InterviewProcessType;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class EvaluationScoreService {

	private final EvaluationScoreRepository evaluationScoreRepository;
	private final EvaluationScoreHistoryRepository evaluationScoreHistoryRepository;

	public void save(final String evaluationId,
			final String scoringAlgoVersion,
			final InterviewProcessType processType,
			final List<SkillScore> skillScores) {

		this.saveEvaluationScores(evaluationId, scoringAlgoVersion, processType, skillScores);
		this.saveEvaluationScoreHistory(evaluationId, scoringAlgoVersion, processType, skillScores);
	}

	private void saveEvaluationScores(
			final String evaluationId,
			final String scoringAlgoVersion,
			final InterviewProcessType processType,
			final List<SkillScore> skillScores) {

		log.info("Saving evaluation scores for evaluation : {} , algoVersion  : {} , processType : {}", evaluationId,
				scoringAlgoVersion, processType);
		skillScores.stream().forEach(x -> log.info(
				"evaluationId : {} , scoringAlgoVersion : {} , processType : {} | SkillID : {} , Score : {} , Weightage : {}",
				evaluationId, scoringAlgoVersion, processType, x.getSkillId(), x.getScore(), x.getWeightage()));

		this.evaluationScoreRepository.deleteAllByEvaluationIdAndScoringAlgoVersionAndProcessType(evaluationId,
				scoringAlgoVersion, processType);
		this.evaluationScoreRepository.flush();
		final List<EvaluationScoreDAO> evaluationScores = skillScores.stream()
				.map(
						ss -> EvaluationScoreDAO.builder()
								.id(UUID.randomUUID().toString())
								.evaluationId(evaluationId)
								.processType(processType)
								.skillId(ss.getSkillId())
								.scoringAlgoVersion(
										scoringAlgoVersion)
								.score(ss.getScore())
								.weightage(ss.getWeightage())
								.build())
				.collect(Collectors.toList());

		this.evaluationScoreRepository.saveAll(evaluationScores);
	}

	private void saveEvaluationScoreHistory(
			final String evaluationId,
			final String scoringAlgoVersion,
			final InterviewProcessType processType,
			final List<SkillScore> skillScores) {

		this.evaluationScoreHistoryRepository.save(
				EvaluationScoreHistoryDAO.builder()
						.id(UUID.randomUUID().toString())
						.evaluationId(evaluationId)
						.processType(processType)
						.scoringAlgoVersion(scoringAlgoVersion)
						.scores(skillScores)
						.build());
	}

}
