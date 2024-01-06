package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface EvaluationChangeHistoryRepository extends JpaRepository<EvaluationChangeHistoryDAO, String> {
    Optional<EvaluationChangeHistoryDAO> findTopByEvaluationIdAndFieldNameAndFieldValueOrderByCreatedOnDesc(String evaluationId, String fieldName, String fieldValue);

    Optional<EvaluationChangeHistoryDAO> findTopByEvaluationIdAndFieldNameOrderByFieldChangedOnDesc(String evaluationId, String followUpDate);

    void deleteAllByEvaluationIdAndFieldName(String evaluationId, String fieldName);
}
