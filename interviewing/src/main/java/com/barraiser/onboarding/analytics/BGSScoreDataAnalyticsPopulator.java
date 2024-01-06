/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.analytics;

import com.barraiser.onboarding.dal.BGSComputationVariablesSnapShotDAO;
import com.barraiser.onboarding.dal.BgsComputationVariablesSnapshotRepository;
import com.barraiser.onboarding.interview.evaluation.scores.pojo.ComputeEvaluationScoresData;
import com.barraiser.onboarding.interview.evaluation.enums.InterviewProcessType;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.UUID;

@Log4j2
@Component
@AllArgsConstructor
public class BGSScoreDataAnalyticsPopulator {
	private final BgsComputationVariablesSnapshotRepository bgsComputationVariablesSnapshotRepository;

	@Transactional
	public void saveAnalyticsForBGSCalculation(final String entityId, final String entityType,
			final String scoringAlgoVersion,
			final ComputeEvaluationScoresData payload,
			final InterviewProcessType processType) {
		this.bgsComputationVariablesSnapshotRepository.deleteAllByEntityIdAndScoringAlgoVersionAndProcessType(entityId,
				scoringAlgoVersion, processType);
		final BGSComputationVariablesSnapShotDAO bgsComputationVariablesSnapShotDAO = BGSComputationVariablesSnapShotDAO
				.builder()
				.id(UUID.randomUUID().toString())
				.entityId(entityId)
				.entityType(entityType)
				.payload(payload)
				.processType(processType)
				.scoringAlgoVersion(scoringAlgoVersion)
				.build();
		this.bgsComputationVariablesSnapshotRepository.save(bgsComputationVariablesSnapShotDAO);
	}
}
