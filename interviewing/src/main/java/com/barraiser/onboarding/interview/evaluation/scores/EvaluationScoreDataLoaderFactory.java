/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.scores;

import com.barraiser.common.graphql.types.EvaluationScore;
import com.barraiser.common.graphql.types.SkillScore;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.EvaluationRepository;
import com.barraiser.onboarding.dal.EvaluationScoreDAO;
import com.barraiser.onboarding.dal.EvaluationScoreRepository;
import com.barraiser.onboarding.graphql.DataLoaderFactory;
import com.barraiser.onboarding.interview.evaluation.enums.InterviewProcessType;
import lombok.AllArgsConstructor;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class EvaluationScoreDataLoaderFactory
		implements DataLoaderFactory<EvaluationScoreCriteria, EvaluationScore> {
	public static final String DATA_LOADER_NAME = "evaluation-score-dataloader";
	private static final Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	private final EvaluationRepository evaluationRepository;
	private final EvaluationScoreRepository evaluationScoreRepository;
	private final ScoreScaleConverter scoreScaleConverter;

	@Override
	public String dataLoaderName() {
		return DATA_LOADER_NAME;
	}

	@Override
	public DataLoader<EvaluationScoreCriteria, EvaluationScore> getDataLoader() {

		return DataLoader.newMappedDataLoader(
				(Set<EvaluationScoreCriteria> evaluationScoreCriterias) -> CompletableFuture.supplyAsync(
						() -> getData(evaluationScoreCriterias), executor));
	}

	@Override
	public Map<EvaluationScoreCriteria, EvaluationScore> getData(
			final Set<EvaluationScoreCriteria> scoreCriteriaSet) {
		final List<String> evaluationIds = scoreCriteriaSet.stream()
				.map(EvaluationScoreCriteria::getEvaluationId)
				.collect(Collectors.toList());

		final Map<String, List<EvaluationScoreDAO>> evaluationScoresByEvaluation = this.evaluationScoreRepository
				.findAllByEvaluationIdIn(evaluationIds)
				.stream()
				.filter(x -> scoreCriteriaSet.stream().filter(s -> s.getEvaluationId().equals(x.getEvaluationId()))
						.findFirst().get().getScoringAlgo().equals(x.getScoringAlgoVersion()))
				.collect(Collectors.groupingBy(EvaluationScoreDAO::getEvaluationId));

		final Map<EvaluationScoreCriteria, EvaluationScore> evaluationScoreMap = new HashMap<>();
		for (final EvaluationScoreCriteria e : scoreCriteriaSet) {
			String evaluationID = e.getEvaluationId();
			EvaluationDAO evaluationDAO = evaluationRepository.findById(evaluationID).get();
			final EvaluationScore score = generateScore(evaluationScoresByEvaluation.get(e.getEvaluationId()),
					evaluationDAO.getPartnerId());
			evaluationScoreMap.put(e, score);
		}
		return evaluationScoreMap;
	}

	private EvaluationScore generateScore(final List<EvaluationScoreDAO> evaluationScoreDAOS, String partnerId) {
		if (evaluationScoreDAOS == null) {
			return EvaluationScore.builder().build();
		}

		final Map<InterviewProcessType, List<EvaluationScoreDAO>> scoreMapByProcessType = new HashMap<>();

		scoreMapByProcessType.put(InterviewProcessType.PARTNER_INTERNAL, new ArrayList<>());
		scoreMapByProcessType.put(InterviewProcessType.BARRAISER, new ArrayList<>());
		scoreMapByProcessType.put(InterviewProcessType.OVERALL, new ArrayList<>());
		evaluationScoreDAOS.forEach(x -> scoreMapByProcessType.get(x.getProcessType()).add(x));

		final List<SkillScore> partnerScores = scoreMapByProcessType.get(InterviewProcessType.PARTNER_INTERNAL).stream()
				.map(evaluationScoreDAO -> toSkillScoreScale(evaluationScoreDAO, partnerId))
				.sorted(Comparator.comparing(SkillScore::getWeightage).reversed())
				.collect(Collectors.toList());
		final List<SkillScore> overallScores = scoreMapByProcessType.get(InterviewProcessType.OVERALL).stream()
				.map(evaluationScoreDAO -> toSkillScoreScale(evaluationScoreDAO, partnerId))
				.sorted(Comparator.comparing(SkillScore::getWeightage).reversed())
				.collect(Collectors.toList());

		final List<SkillScore> barraiserScores = scoreMapByProcessType.get(InterviewProcessType.BARRAISER).stream()
				.map(evaluationScoreDAO -> toSkillScoreScale(evaluationScoreDAO, partnerId))
				.sorted(Comparator.comparing(SkillScore::getWeightage).reversed())
				.collect(Collectors.toList());
		// No scale calulator used as scores are already scaled
		return EvaluationScore.builder()
				.partnerBGS(
						BgsCalculator.calculateBgsNoScaleDouble(partnerScores))
				.overallBGS(
						BgsCalculator.calculateBgsNoScaleDouble(overallScores))
				.bgs(
						BgsCalculator.calculateBgsNoScaleDouble(barraiserScores))
				.barraiserScores(barraiserScores)
				.overallScores(overallScores)
				.partnerScores(partnerScores)
				.build();
	}

	// todo: 722 change depending on config
	private SkillScore toSkillScoreScale(final EvaluationScoreDAO evaluationScoreDAO, String partnerId) {
		return SkillScore.builder()
				.skillId(evaluationScoreDAO.getSkillId())
				.weightage(evaluationScoreDAO.getWeightage())
				.score(scoreScaleConverter.convertScoreFrom800(evaluationScoreDAO.getScore(), partnerId))
				.build();
	}
}
