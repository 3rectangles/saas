/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.scores;

import com.barraiser.common.graphql.types.SkillScore;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.EvaluationScoreDAO;
import com.barraiser.onboarding.dal.EvaluationScoreRepository;
import com.barraiser.onboarding.interview.evaluation.enums.InterviewProcessType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class EvaluationScoreFetcher {
	private final ObjectMapper objectMapper;
	private final EvaluationScoreRepository evaluationScoreRepository;

	public Map<String, Map<String, List<SkillScore>>> getAllScores(
			final List<EvaluationDAO> evaluationDAOList, final String scoringAlgo) {
		final Map<String, Map<String, List<SkillScore>>> processToEvaluationScoresMap = new HashMap<String, Map<String, List<SkillScore>>>();

		final List<EvaluationScoreDAO> evaluationScoreDAOList = this.evaluationScoreRepository.findAllByEvaluationIdIn(
				evaluationDAOList.stream()
						.map(EvaluationDAO::getId)
						.collect(Collectors.toList()));

		final List<EvaluationScoreDAO> barraiserProccessScoreDAOList = new ArrayList<>();
		final List<EvaluationScoreDAO> partnerInternalProcessScoreDAOList = new ArrayList<>();
		final List<EvaluationScoreDAO> overallProcessScoreDAOList = new ArrayList<>();

		for (final EvaluationScoreDAO evaluationScoreDAO : evaluationScoreDAOList) {
			if (InterviewProcessType.BARRAISER == evaluationScoreDAO.getProcessType()) {
				barraiserProccessScoreDAOList.add(evaluationScoreDAO);
			}
			if (InterviewProcessType.PARTNER_INTERNAL == evaluationScoreDAO.getProcessType()) {
				partnerInternalProcessScoreDAOList.add(evaluationScoreDAO);
			}
			if (InterviewProcessType.OVERALL == evaluationScoreDAO.getProcessType()) {
				overallProcessScoreDAOList.add(evaluationScoreDAO);
			}
		}

		processToEvaluationScoresMap.put(
				InterviewProcessType.BARRAISER.getValue(),
				this.getEvaluationScores(
						barraiserProccessScoreDAOList, evaluationDAOList, scoringAlgo));
		processToEvaluationScoresMap.put(
				InterviewProcessType.PARTNER_INTERNAL.getValue(),
				this.getEvaluationScores(
						partnerInternalProcessScoreDAOList, evaluationDAOList, scoringAlgo));
		processToEvaluationScoresMap.put(
				InterviewProcessType.OVERALL.getValue(),
				this.getEvaluationScores(
						overallProcessScoreDAOList, evaluationDAOList, scoringAlgo));

		return processToEvaluationScoresMap;
	}

	// If scoringAlgo is passed, it will be used for all evaluationIds. Otherwise,
	// default scoring
	// algo for each evaluation will be used.
	public Map<String, List<SkillScore>> getEvaluationScores(
			final List<EvaluationScoreDAO> evaluationScoreDAOList,
			final List<EvaluationDAO> evaluationDAOList,
			final String scoringAlgo) {
		final Map<String, List<EvaluationScoreDAO>> evaluationScoreDAOMap = new HashedMap();
		for (final EvaluationScoreDAO evaluationScoreDAO : evaluationScoreDAOList) {
			final List<EvaluationScoreDAO> evaluationScoreDAOs = evaluationScoreDAOMap.getOrDefault(
					evaluationScoreDAO.getEvaluationId(), new ArrayList<>());
			evaluationScoreDAOs.add(evaluationScoreDAO);
			evaluationScoreDAOMap.put(evaluationScoreDAO.getEvaluationId(), evaluationScoreDAOs);
		}
		final Map<String, List<SkillScore>> evaluationScoreMap = new HashedMap();
		for (final EvaluationDAO evaluationDAO : evaluationDAOList) {
			final List<EvaluationScoreDAO> evaluationScoreDAOs = evaluationScoreDAOMap
					.getOrDefault(evaluationDAO.getId(), new ArrayList<>());
			final List<EvaluationScoreDAO> evaluationScoreDAOsForScoringAlgo = new ArrayList<>();
			final String scoringAlgoForEvaluation = scoringAlgo == null
					? evaluationDAO.getDefaultScoringAlgoVersion()
					: scoringAlgo;
			for (final EvaluationScoreDAO evaluationScoreDAO : evaluationScoreDAOs) {
				if (evaluationScoreDAO.getScoringAlgoVersion().equals(scoringAlgoForEvaluation)) {
					evaluationScoreDAOsForScoringAlgo.add(evaluationScoreDAO);
				}
			}
			evaluationScoreMap.put(
					evaluationDAO.getId(),
					evaluationScoreDAOsForScoringAlgo.stream()
							.map(x -> this.objectMapper.convertValue(x, SkillScore.class))
							.sorted(Comparator.comparing(SkillScore::getWeightage).reversed())
							.collect(Collectors.toList()));
		}

		return evaluationScoreMap;
	}
}
