/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.percentile;

import com.barraiser.common.graphql.types.SkillScore;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.evaluation.enums.InterviewProcessType;
import com.barraiser.onboarding.interview.evaluation.scores.BgsCalculator;
import com.barraiser.onboarding.interview.evaluation.scores.EvaluationScoreFetcher;
import com.barraiser.onboarding.interview.jobrole.JobRoleManager;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.math3.stat.ranking.NaturalRanking;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Log4j2
@Component
public class EvaluationPercentileCalculator {
	private static final Integer MINIMUM_POOL_SIZE = 65;
	private static final List<String> CATEGORIES_IN_ORDER = List.of("A", "B", "C", "D", "E", "F");

	private final EvaluationRepository evaluationRepository;
	private final JobRoleManager jobRoleManager;
	private final EvaluationScoreRepository evaluationScoreRepository;
	private final EvaluationScoreFetcher evaluationScoreFetcher;

	public Double calculatePercentile(final String evaluationId) {
		final EvaluationDAO evaluation = this.evaluationRepository.findById(evaluationId).get();
		final List<EvaluationDAO> evaluationsPool = this.getEvaluationsPoolForPercentileCalculation(evaluation);
		evaluationsPool.add(evaluation);
		final Map<String, Integer> evaluationToBgsMap = this.getBgsForEvaluations(evaluationsPool);
		final List<Double> bgsList = new ArrayList<>();
		final int[] bgsIndex = new int[1];
		evaluationToBgsMap.forEach(
				(eid, bgs) -> {
					bgsList.add(Double.valueOf(bgs));
					if (eid.equals(evaluationId)) {
						bgsIndex[0] = bgsList.size() - 1;
					}
				});
		final NaturalRanking naturalRanking = new NaturalRanking();
		final double[] ranks = naturalRanking.rank(bgsList.stream().mapToDouble(b -> b).toArray());
		final Double percentile = ranks[bgsIndex[0]] / bgsList.size();
		return percentile;
	}

	private List<EvaluationDAO> getEvaluationsPoolForPercentileCalculation(
			final EvaluationDAO evaluation) {
		final JobRoleDAO jobRole = this.jobRoleManager.getJobRoleFromEvaluation(evaluation).get();

		final List<EvaluationDAO> evaluationsWithSameDomain = this.getEvaluationIdsForDomain(jobRole.getDomainId(),
				evaluation.getId());
		final List<EvaluationDAO> evaluationsWithSameCategory = this.getEvaluationIdsForCategory(jobRole.getCategory(),
				evaluation.getId());
		final List<EvaluationDAO> evaluationsWithSameCategoryAndDomain = evaluationsWithSameDomain.stream()
				.filter(
						e1 -> evaluationsWithSameCategory.stream()
								.anyMatch(e2 -> e2.getId().equals(e1.getId())))
				.collect(Collectors.toList());

		if (evaluationsWithSameCategoryAndDomain.size() >= MINIMUM_POOL_SIZE - 1) {
			return evaluationsWithSameCategoryAndDomain;
		} else if (evaluationsWithSameDomain.size() >= MINIMUM_POOL_SIZE - 1) {
			return evaluationsWithSameDomain;
		} else {
			final List<EvaluationDAO> evaluationsWithCategoryCapped = new ArrayList<>(evaluationsWithSameCategory);
			final int categoryIndex = CATEGORIES_IN_ORDER.indexOf(jobRole.getCategory()) - 1;
			while (evaluationsWithCategoryCapped.size() < MINIMUM_POOL_SIZE - 1
					&& categoryIndex >= 0) {
				evaluationsWithCategoryCapped.addAll(
						this.getEvaluationIdsForCategory(
								CATEGORIES_IN_ORDER.get(categoryIndex), evaluation.getId()));
			}
			if (evaluationsWithCategoryCapped.size() >= MINIMUM_POOL_SIZE - 1) {
				return evaluationsWithCategoryCapped;
			}
			return this.getAllEvaluations(evaluation.getId());
		}
	}

	private List<EvaluationDAO> getEvaluationIdsForDomain(
			final String domainId, final String excludeEvaluationId) {
		final List<JobRoleDAO> jobRoles = this.jobRoleManager.getJobRoleFromDomainId(domainId);
		return this.getEvaluationIdsForJobRoles(jobRoles, excludeEvaluationId);
	}

	private List<EvaluationDAO> getEvaluationIdsForCategory(
			final String category, final String excludeEvaluationId) {
		final List<JobRoleDAO> jobRoles = this.jobRoleManager.getJobRoleFromCategoryId(category);
		return this.getEvaluationIdsForJobRoles(jobRoles, excludeEvaluationId);
	}

	private List<EvaluationDAO> getEvaluationIdsForJobRoles(
			final List<JobRoleDAO> jobRoles, final String excludeEvaluationId) {
		return this.evaluationRepository
				.findAllByJobRoleIdInAndStatus(
						jobRoles.stream()
								.map(jr -> jr.getEntityId().getId())
								.collect(Collectors.toList()),
						EvaluationStatus.DONE.getValue())
				.stream()
				.filter(e -> !e.getId().equals(excludeEvaluationId))
				.collect(Collectors.toList());
	}

	private List<EvaluationDAO> getAllEvaluations(final String excludeEvaluationId) {
		return this.evaluationRepository.findAllByStatus(EvaluationStatus.DONE.getValue()).stream()
				.filter(e -> !e.getId().equals(excludeEvaluationId))
				.collect(Collectors.toList());
	}

	private Map<String, Integer> getBgsForEvaluations(final List<EvaluationDAO> evaluations) {
		final List<EvaluationScoreDAO> scoreDAOs = this.evaluationScoreRepository
				.findAllByEvaluationIdIn(
						evaluations.stream()
								.map(EvaluationDAO::getId)
								.collect(Collectors.toList()))
				.stream()
				.filter(s -> InterviewProcessType.BARRAISER.equals(s.getProcessType()))
				.collect(Collectors.toList());

		final Map<String, List<SkillScore>> evaluationToScoresMap = this.evaluationScoreFetcher
				.getEvaluationScores(scoreDAOs, evaluations, null);

		final Map<String, Integer> evaluationToBgsMap = new HashedMap();
		for (final EvaluationDAO evaluation : evaluations) {
			List<SkillScore> scores = List.of();
			if (evaluationToScoresMap.containsKey(evaluation.getId())) {
				scores = evaluationToScoresMap.get(evaluation.getId());
			}
			evaluationToBgsMap.put(evaluation.getId(), BgsCalculator.calculateBgsNoScale(scores));
		}
		return evaluationToBgsMap;
	}
}
