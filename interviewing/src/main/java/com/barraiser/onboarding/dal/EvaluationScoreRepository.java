/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.onboarding.interview.evaluation.enums.InterviewProcessType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationScoreRepository extends JpaRepository<EvaluationScoreDAO, String> {
	List<EvaluationScoreDAO> findAllByEvaluationIdIn(List<String> evaluationId);

	Optional<EvaluationScoreDAO> findByEvaluationIdAndSkillIdAndScoringAlgoVersion(
			String evaluationId, String skillId, String scoringAlgoVersion);

	Optional<EvaluationScoreDAO> findByEvaluationIdAndSkillIdAndScoringAlgoVersionAndProcessType(
			String evaluationId,
			String skillId,
			String scoringAlgoVersion,
			InterviewProcessType processType);

	List<EvaluationScoreDAO> findAllByEvaluationIdAndScoringAlgoVersion(
			String evaluationId, String scoringAlgoVersion);

	List<EvaluationScoreDAO> findAllByEvaluationIdAndScoringAlgoVersionAndProcessType(
			String evaluationId,
			String scoringAlgoVersion,
			InterviewProcessType interviewProcessType);

	void deleteAllByEvaluationIdAndScoringAlgoVersionAndProcessType(String evaluationId, String scoringAlgoVersion,
			InterviewProcessType processType);
}
