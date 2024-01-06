/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.onboarding.interview.evaluation.enums.InterviewProcessType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BgsComputationVariablesSnapshotRepository
		extends JpaRepository<BGSComputationVariablesSnapShotDAO, String> {

	void deleteAllByEntityIdAndScoringAlgoVersionAndProcessType(String entityId, String scoringAlgoVersion,
			InterviewProcessType processType);
}
