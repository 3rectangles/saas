/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpertNormalisedRatingRepository extends JpaRepository<ExpertNormalisedRatingDAO, String> {

	List<ExpertNormalisedRatingDAO> findAllByInterviewerId(String interviewerId);

	void deleteAllByInterviewerIdInAndNormalisationVersion(List<String> expertIds,
			String expertNormalisedRatingCalculationVersion);
}
