/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ATSToBREvaluationRepository extends JpaRepository<ATSToBREvaluationDAO, String> {

	Optional<ATSToBREvaluationDAO> findByBrEvaluationIdAndAtsProvider(
			final String evaluationId,
			final String atsProvider);

	Optional<ATSToBREvaluationDAO> findByBrEvaluationId(final String evaluationId);

	Optional<ATSToBREvaluationDAO> findByAtsEvaluationIdAndAtsProvider(final String atsEvaluationId,
			final String atsProvider);

	Optional<ATSToBREvaluationDAO> findByAtsEvaluationId(final String atsEvaluationId);
}
