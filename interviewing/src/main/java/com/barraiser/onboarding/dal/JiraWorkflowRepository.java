package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JiraWorkflowRepository extends JpaRepository<JiraWorkflowDAO, String> {
    Optional<JiraWorkflowDAO> findByFromStateAndToStateAndAction(String fromState, String toState, String action);
    Optional<JiraWorkflowDAO> findByFromStateAndAction(String fromState, String action);
}
