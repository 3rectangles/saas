package com.barraiser.onboarding.interview.evaluation;

import com.barraiser.onboarding.dal.EvaluationChangeHistoryDAO;
import com.barraiser.onboarding.dal.EvaluationChangeHistoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class EvaluationChangeHistoryManager {
    private final EvaluationChangeHistoryRepository evaluationChangeHistoryRepository;

    public String getCurrentFieldValue(final String fieldName, final String evaluationId) {
        final Optional<EvaluationChangeHistoryDAO> evaluationChangeHistoryDAO = this.evaluationChangeHistoryRepository
            .findTopByEvaluationIdAndFieldNameOrderByFieldChangedOnDesc(evaluationId, fieldName);
        return evaluationChangeHistoryDAO.map(EvaluationChangeHistoryDAO::getFieldValue).orElse(null);
    }

    public void saveHistory(final String evaluationId, final String fieldName, final String fieldValue, final String createdBy, final Instant fieldChangedOn) {
        this.evaluationChangeHistoryRepository.save(EvaluationChangeHistoryDAO.builder()
            .id(UUID.randomUUID().toString())
            .evaluationId(evaluationId)
            .fieldName(fieldName)
            .fieldValue(fieldValue)
            .createdBy(createdBy)
            .fieldChangedOn(fieldChangedOn != null ? fieldChangedOn : Instant.now()).build());
    }

    public void removeHistoryForField(final String evaluationId, final String fieldName) {
        this.evaluationChangeHistoryRepository.deleteAllByEvaluationIdAndFieldName(evaluationId, fieldName);
    }
}
