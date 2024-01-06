/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers.internalInterviews.repository;

import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.internalInterviews.dal.InterviewStructureToExpertsDAO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterviewStructureToExpertsRepository extends JpaRepository<InterviewStructureToExpertsDAO, String> {

	Optional<InterviewStructureToExpertsDAO> findByInterviewStructureId(String interviewStructureId);
}
