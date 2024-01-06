/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InterviewerRecommendationRepository extends JpaRepository<InterviewerRecommendationDAO, String> {
	Optional<InterviewerRecommendationDAO> findByInterviewId(String interviewId);
}
