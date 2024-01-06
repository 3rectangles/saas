/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.recommendation;

import com.barraiser.common.graphql.types.EvaluationRecommendation;
import com.barraiser.onboarding.dal.EvaluationRecommendationDAO;
import com.barraiser.onboarding.dal.EvaluationRecommendationRepository;
import com.barraiser.onboarding.graphql.DataLoaderFactory;
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
public class EvaluationRecommendationDataLoaderFactory
		implements DataLoaderFactory<EvaluationRecommendationCriteria, EvaluationRecommendation> {

	public static final String DATA_LOADER_NAME = "evaluation-recommendation-dataloader";
	private static final Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	private final EvaluationRecommendationRepository evaluationRecommendationRepository;
	private final EvaluationRecommendationMapper evaluationRecommendationMapper;

	@Override
	public String dataLoaderName() {
		return DATA_LOADER_NAME;
	}

	@Override
	public DataLoader<EvaluationRecommendationCriteria, EvaluationRecommendation> getDataLoader() {
		return DataLoader.newMappedDataLoader(
				(Set<EvaluationRecommendationCriteria> EvaluationRecommendationCriteria) -> CompletableFuture
						.supplyAsync(
								() -> getData(EvaluationRecommendationCriteria), executor));
	}

	@Override
	public Map<EvaluationRecommendationCriteria, EvaluationRecommendation> getData(
			Set<EvaluationRecommendationCriteria> evaluationRecommendationCriteriaSet) {
		final List<String> evaluationIds = evaluationRecommendationCriteriaSet.stream()
				.map(EvaluationRecommendationCriteria::getEvaluationId).collect(Collectors.toList());
		final List<EvaluationRecommendationDAO> evaluationRecommendationDAOs = this.evaluationRecommendationRepository
				.findAllByEvaluationIdIn(evaluationIds);
		final Map<EvaluationRecommendationCriteria, EvaluationRecommendation> evaluationToRecommendationMapping = new HashMap<>();
		evaluationRecommendationCriteriaSet.forEach(
				x -> {
					final Optional<EvaluationRecommendationDAO> evaluationRecommendationDAO = evaluationRecommendationDAOs
							.stream()
							.filter(s -> s.getEvaluationId().equals(x.getEvaluationId())
									&& s.getRecommendationAlgoVersion().equals(x.getRecommendationVersion()))
							.findFirst();
					evaluationRecommendationDAO.ifPresent(recommendationDAO -> evaluationToRecommendationMapping.put(x,
							this.evaluationRecommendationMapper.toEvaluationRecommendation(recommendationDAO)));
				});

		return evaluationToRecommendationMapping;
	}
}
