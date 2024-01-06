package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomizableEvaluationHistoryRepository extends JpaRepository<CustomizableEvaluationHistoryDAO, String> {
    Optional<CustomizableEvaluationHistoryDAO> findTopByEvaluationIdAndEditIdAndScoringAlgorithmVersionOrderByCreatedOnDesc(String evaluationId, String editId,String scoringAlgoVersion);
}
