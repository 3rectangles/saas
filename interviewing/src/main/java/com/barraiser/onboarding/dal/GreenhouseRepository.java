/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GreenhouseRepository extends JpaRepository<GreenhouseDAO, String> {
	Optional<GreenhouseDAO> findByEvaluationId(String evaluationId);
}
